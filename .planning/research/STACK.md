# 技术栈研究：贸易路线与村庄互联系统

**项目:** VillageGenesis
**研究日期:** 2026-03-26
**研究范围:** 贸易路线、村庄外交、经济依赖、人口迁移、军事冲突系统
**Minecraft版本:** 1.21.1
**NeoForge版本:** 21.1.215

---

## 执行摘要

本研究针对 VillageGenesis 模组的村庄互联系统扩展，提供了完整的技术方案。现有系统已具备基础的贸易路线框架，需要扩展为包含外交、经济、人口、政治、军事五个维度的综合系统。

**关键发现:**
- 现有架构（SavedData + Payload + ServerTickEvent）为扩展提供了良好基础
- 需要重点优化性能，避免大规模模拟导致的卡顿
- 推荐使用分批处理、异步计算、事件驱动等模式
- 避免使用静态变量存储世界引用、每 tick 遍历所有关系等反模式

---

## 1. 现有系统分析

### 1.1 已实现的核心类

| 类名 | 职责 | 状态 |
|------|------|------|
| `TradeRoute` | 贸易路线数据模型 | ✓ 基础实现 |
| `TradeRouteManager` | 贸易路线管理器 | ✓ 基础实现 |
| `VillageTradeRelation` | 村庄贸易关系 | ✓ 基础实现 |
| `RouteType` | 路线类型枚举 | ✓ 完整实现 |
| `TradeRelationType` | 贸易关系类型枚举 | ✓ 完整实现 |

### 1.2 现有架构特点

**优势:**
- 使用 UUID 标识村庄和路线，支持持久化
- 已实现 NBT 序列化/反序列化
- 已有基础的税收和运输成本计算
- 已有村庄关系类型（HOSTILE/COLD/NEUTRAL/FRIENDLY/ALLIED）
- 已有路线类型（LAND/WATER/MIXED）

**不足:**
- 缺少路径验证机制（仅使用欧几里得距离）
- 缺少路线自动发现机制
- 缺少运输任务调度系统
- 缺少外交、经济依赖、人口迁移、军事冲突系统

---

## 2. NeoForge 1.21.1 技术方案

### 2.1 数据持久化与同步

#### 推荐方案：SavedData + StreamCodec 组合

**置信度：高 (95%)**

**理由：**
- SavedData 是 NeoForge 官方推荐的世界级数据存储方式
- StreamCodec 提供高效的二进制序列化，适合网络传输
- 现有 VillageManager 已使用 SavedData，保持一致性

**实现模式：**

```java
// 扩展 VillageManager 存储新系统数据
public class VillageManager extends SavedData {
    private final TradeRouteManager tradeRouteManager;
    private final DiplomaticRelationManager diplomaticManager;
    private final EconomicDependencyGraph dependencyGraph;
    private final MigrationQueue migrationQueue;

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("tradeRoutes", tradeRouteManager.serializeNBT());
        tag.put("diplomacy", diplomaticManager.serializeNBT());
        tag.put("dependencies", dependencyGraph.serializeNBT());
        tag.put("migrations", migrationQueue.serializeNBT());
        return tag;
    }
}
```

**网络同步：**

```java
// 使用 StreamCodec 定义高效的 Payload
public record VillageDiplomacyPayload(
    UUID villageA,
    UUID villageB,
    DiplomaticRelation relation
) implements CustomPacketPayload {

    public static final Type<VillageDiplomacyPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "diplomacy"));

    public static final StreamCodec<ByteBuf, VillageDiplomacyPayload> STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, VillageDiplomacyPayload::villageA,
            UUIDUtil.STREAM_CODEC, VillageDiplomacyPayload::villageB,
            DiplomaticRelation.STREAM_CODEC, VillageDiplomacyPayload::relation,
            VillageDiplomacyPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
```

**同步策略：**
- 仅同步玩家可见的数据（附近村庄、有贸易关系的村庄）
- 使用增量更新，避免全量同步
- 客户端缓存，服务端推送变更

#### 避免方案：静态变量存储世界引用

**置信度：高 (90%)**

**理由：**
- 多世界场景下会导致数据混乱
- 世界卸载后引用失效
- 已在 ARCHITECTURE.md 中标记为技术债务

**替代方案：**
使用 Attachment 系统或通过方法参数传递 ServerLevel 引用。

---

### 2.2 定时任务与模拟更新

#### 推荐方案：分批处理 + ServerTickEvent

**置信度：高 (90%)**

**理由：**
- 避免单 tick 处理过多数据导致卡顿
- 保持模拟的连续性和响应性
- 现有 ServerTickEvents 已实现基础框架

**实现模式：**

```java
@EventBusSubscriber(modid = MOD_ID)
public class SimulationTickHandler {

    private static int tickCounter = 0;
    private static final int BATCH_SIZE = 10; // 每tick处理10个村庄

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;

        // 每 tick 处理一批村庄的贸易路线更新
        processTradeRouteBatch();

        // 每 20 tick (1秒) 更新一次外交关系
        if (tickCounter % 20 == 0) {
            processDiplomacyBatch();
        }

        // 每 200 tick (10秒) 更新一次经济依赖图
        if (tickCounter % 200 == 0) {
            processDependencyGraph();
        }

        // 每 24000 tick (1游戏日) 评估人口迁移
        if (tickCounter % 24000 == 0) {
            processMigration();
        }
    }

    private static void processTradeRouteBatch() {
        List<VillageData> villages = new ArrayList<>(VillageManager.getAllVillages());
        int startIndex = (tickCounter / 20) % villages.size();
        int endIndex = Math.min(startIndex + BATCH_SIZE, villages.size());

        for (int i = startIndex; i < endIndex; i++) {
            villages.get(i).updateTradeRoutes();
        }
    }
}
```

#### 推荐方案：异步计算复杂模拟

**置信度：中 (75%)**

**理由：**
- 军事冲突、经济传播等复杂计算不应阻塞主线程
- TransactionManager 已使用线程池，保持一致性
- 需要注意线程安全和数据一致性

**实现模式：**

```java
public class ConflictSimulationEngine {

    private static final ExecutorService SIMULATION_POOL =
        Executors.newFixedThreadPool(2);

    public static CompletableFuture<ConflictResult> simulateConflict(
        UUID attackerId,
        UUID defenderId
    ) {
        return CompletableFuture.supplyAsync(() -> {
            // 在后台线程执行复杂计算
            MilitaryStrength attackerStrength = calculateStrength(attackerId);
            MilitaryStrength defenderStrength = calculateStrength(defenderId);

            // 使用 Lanchester 方程模拟战斗
            return simulateBattle(attackerStrength, defenderStrength);
        }, SIMULATION_POOL);
    }
}
```

**注意事项：**
- 异步任务中不能访问游戏对象（实体、方块等）
- 使用快照模式（如 TraderSnapshot）传递数据
- 结果通过主线程回调应用

#### 避免方案：每 tick 遍历所有村庄关系

**置信度：高 (95%)**

**理由：**
- 村庄数量可能达到数百个，关系数量为 O(n²)
- 每 tick 遍历会导致严重性能问题
- 大部分关系在短时间内不会变化

**替代方案：**
- 使用事件驱动更新（交易发生时更新关系）
- 分批处理，每 tick 只处理一部分
- 缓存计算结果，定期刷新

---

### 2.3 村庄间通信与事件系统

#### 推荐方案：NeoForge EventBus + 自定义事件

**置信度：高 (90%)**

**理由：**
- 解耦系统，降低复杂度
- 支持多个监听器响应同一事件
- NeoForge 官方推荐模式

**实现模式：**

```java
// 定义自定义事件
public class VillageDiplomacyEvent extends Event {
    private final UUID villageA;
    private final UUID villageB;
    private final DiplomaticAction action;

    public VillageDiplomacyEvent(UUID villageA, UUID villageB, DiplomaticAction action) {
        this.villageA = villageA;
        this.villageB = villageB;
        this.action = action;
    }

    // Getters...
}

// 发布事件
NeoForge.EVENT_BUS.post(new VillageDiplomacyEvent(villageA, villageB, action));

// 监听事件
@SubscribeEvent
public static void onDiplomacyEvent(VillageDiplomacyEvent event) {
    // 更新贸易路线、经济依赖等
    tradeRouteManager.updateForDiplomacyChange(event);
    dependencyGraph.recalculate(event.getVillageA(), event.getVillageB());
}
```

**推荐的事件类型：**
- `VillageDiplomacyEvent` - 外交关系变化
- `TradeRouteEstablishedEvent` - 贸易路线建立
- `TradeRouteClosedEvent` - 贸易路线关闭
- `MigrationEvent` - 人口迁移
- `ConflictStartEvent` - 冲突开始
- `ConflictEndEvent` - 冲突结束
- `TreatySignedEvent` - 条约签署

#### 推荐方案：Payload 系统进行客户端同步

**置信度：高 (95%)**

**理由：**
- NeoForge 1.21.1 官方推荐的网络通信方式
- 类型安全，编译时检查
- 支持双向通信

**实现模式：**

```java
// 注册双向 Payload
public class ModPayload {
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(MOD_ID);

        // 客户端 -> 服务端：请求外交信息
        registrar.playBidirectional(
            DiplomacyRequestPayload.TYPE,
            DiplomacyRequestPayload.STREAM_CODEC,
            new DirectionalPayloadHandler<>(
                ClientDiplomacyHandler::handleRequest,
                ServerDiplomacyHandler::handleRequest
            )
        );

        // 服务端 -> 客户端：推送外交更新
        registrar.playToClient(
            DiplomacyUpdatePayload.TYPE,
            DiplomacyUpdatePayload.STREAM_CODEC,
            ClientDiplomacyHandler::handleUpdate
        );
    }
}
```

---

### 2.4 路径计算与导航

#### 推荐方案：增强的欧几里得距离 + 路径验证

**置信度：中 (70%)**

**理由：**
- 完整的 A* 路径查找对性能影响过大
- 欧几里得距离作为基础，增加地形修正因子
- 路径验证可异步执行

**实现模式：**

```java
public class EnhancedPathCalculator {

    /**
     * 计算两个村庄之间的有效路径距离
     * 考虑地形、生物群系、障碍物等因素
     */
    public float calculateEffectiveDistance(
        ServerLevel level,
        VillageData villageA,
        VillageData villageB
    ) {
        BlockPos posA = villageA.getCenterPos();
        BlockPos posB = villageB.getCenterPos();

        // 基础欧几里得距离
        float baseDistance = (float) Math.sqrt(
            posA.distSqr(posB)
        );

        // 地形修正因子
        float terrainFactor = calculateTerrainFactor(level, posA, posB);

        // 生物群系修正因子
        float biomeFactor = calculateBiomeFactor(level, posA, posB);

        // 路线类型修正
        RouteType routeType = determineRouteType(level, posA, posB);

        return baseDistance * terrainFactor * biomeFactor * routeType.getSpeedFactor();
    }

    private float calculateTerrainFactor(ServerLevel level, BlockPos from, BlockPos to) {
        // 采样路径上的高度变化
        int samples = 10;
        float totalHeightChange = 0;

        for (int i = 0; i < samples; i++) {
            double t = (double) i / samples;
            int x = (int) (from.getX() + (to.getX() - from.getX()) * t);
            int z = (int) (from.getZ() + (to.getZ() - from.getZ()) * t);
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            if (i > 0) {
                // 计算高度变化
                totalHeightChange += Math.abs(y - previousY);
            }
            previousY = y;
        }

        // 高度变化越大，地形因子越高
        return 1.0f + (totalHeightChange / samples) * 0.1f;
    }
}
```

#### 避免方案：实时 A* 路径查找

**置信度：高 (85%)**

**理由：**
- 村庄间距离可能达到数千格
- A* 算法在大尺度地图上性能极差
- 需要加载大量区块，进一步降低性能

**替代方案：**
- 使用预计算的路径网络
- 玩家探索时逐步建立路径缓存
- 允许一定程度的近似，不追求最优路径

---

### 2.5 数据结构设计

#### 推荐方案：关系矩阵 + 图结构

**置信度：高 (90%)**

**理由：**
- 村庄关系天然形成图结构
- 关系矩阵支持快速查询
- 图算法（Dijkstra、PageRank）可用于依赖分析

**实现模式：**

```java
/**
 * 村庄关系管理器
 * 使用关系矩阵存储村庄间的各类关系
 */
public class VillageRelationMatrix {

    // 关系矩阵：relationMatrix[villageA][villageB] = relation
    private final Map<UUID, Map<UUID, DiplomaticRelation>> relationMatrix;

    // 邻接表：用于图遍历
    private final Map<UUID, Set<UUID>> adjacencyList;

    /**
     * 获取两个村庄的关系
     * O(1) 时间复杂度
     */
    public DiplomaticRelation getRelation(UUID villageA, UUID villageB) {
        return relationMatrix
            .getOrDefault(villageA, Map.of())
            .get(villageB);
    }

    /**
     * 获取村庄的所有邻接村庄
     * O(1) 时间复杂度
     */
    public Set<UUID> getNeighbors(UUID villageId) {
        return adjacencyList.getOrDefault(villageId, Set.of());
    }

    /**
     * 计算村庄的影响力（类似 PageRank）
     * 用于确定村庄在经济网络中的重要性
     */
    public Map<UUID, Float> calculateInfluenceScores() {
        Map<UUID, Float> scores = new HashMap<>();
        int iterations = 10;
        float dampingFactor = 0.85f;

        // 初始化分数
        for (UUID villageId : adjacencyList.keySet()) {
            scores.put(villageId, 1.0f / adjacencyList.size());
        }

        // 迭代计算
        for (int i = 0; i < iterations; i++) {
            Map<UUID, Float> newScores = new HashMap<>();

            for (UUID villageId : adjacencyList.keySet()) {
                float score = (1 - dampingFactor) / adjacencyList.size();

                for (UUID neighbor : getNeighbors(villageId)) {
                    DiplomaticRelation relation = getRelation(villageId, neighbor);
                    float weight = relation.getTradeVolume() / getTotalTradeVolume(neighbor);
                    score += dampingFactor * scores.get(neighbor) * weight;
                }

                newScores.put(villageId, score);
            }

            scores = newScores;
        }

        return scores;
    }
}
```

#### 推荐方案：优先级队列管理迁移

**置信度：高 (85%)**

**理由：**
- 迁移意愿有优先级差异
- 支持批量处理迁移请求
- 避免迁移过于频繁

**实现模式：**

```java
/**
 * 人口迁移队列
 * 使用优先级队列管理待迁移村民
 */
public class MigrationQueue {

    private final PriorityQueue<MigrationRequest> queue;

    public MigrationQueue() {
        // 按迁移意愿降序排列
        this.queue = new PriorityQueue<>(
            Comparator.comparingDouble(MigrationRequest::getWillingness).reversed()
        );
    }

    /**
     * 添加迁移请求
     */
    public void enqueue(MigrationRequest request) {
        // 检查冷却时间，避免同一村民频繁迁移
        if (isInCooldown(request.getVillagerId())) {
            return;
        }

        // 检查目标村庄容量
        if (!hasCapacity(request.getTargetVillage())) {
            return;
        }

        queue.offer(request);
    }

    /**
     * 处理一批迁移请求
     * 每游戏周调用一次
     */
    public void processBatch(int batchSize) {
        int processed = 0;

        while (!queue.isEmpty() && processed < batchSize) {
            MigrationRequest request = queue.poll();

            if (validateRequest(request)) {
                executeMigration(request);
                processed++;
            }
        }
    }
}
```

---

## 3. 系统架构设计

### 3.1 贸易路线系统扩展

**扩展 TradeRoute 类：**

```java
public class TradeRoute {
    // 现有字段...

    // 新增字段
    private float safetyLevel;          // 安全等级 (0-1)
    private int capacity;               // 路线容量
    private int currentLoad;            // 当前负载
    private List<UUID> waystations;     // 中继站（可选）
    private RouteStatus status;         // 路线状态

    /**
     * 计算实际运输时间
     * 考虑安全等级、负载、天气等因素
     */
    public int calculateActualTransportTime(WeatherCondition weather) {
        float baseTime = this.calculateTransportTime();

        // 安全等级影响：低安全等级增加运输时间
        float safetyMultiplier = 1.0f + (1.0f - safetyLevel) * 0.5f;

        // 负载影响：超载增加运输时间
        float loadMultiplier = 1.0f;
        if (currentLoad > capacity) {
            loadMultiplier = 1.0f + (currentLoad - capacity) * 0.1f;
        }

        // 天气影响
        float weatherMultiplier = weather.getTransportMultiplier();

        return (int) (baseTime * safetyMultiplier * loadMultiplier * weatherMultiplier);
    }
}
```

**新增 TradeRouteDiscovery 类：**

```java
/**
 * 贸易路线发现引擎
 * 自动发现并建立村庄间的贸易关系
 */
public class TradeRouteDiscovery {

    /**
     * 为村庄发现潜在的贸易伙伴
     */
    public List<VillageData> discoverPotentialPartners(
        VillageData village,
        ServerLevel level
    ) {
        List<VillageData> candidates = new ArrayList<>();

        for (VillageData other : VillageManager.getAllVillages()) {
            if (other.equals(village)) continue;

            // 计算贸易潜力分数
            double score = calculateTradePotential(village, other, level);

            if (score > MIN_TRADE_POTENTIAL) {
                candidates.add(other);
            }
        }

        // 按潜力分数排序
        candidates.sort((a, b) ->
            Double.compare(
                calculateTradePotential(village, b, level),
                calculateTradePotential(village, a, level)
            )
        );

        return candidates;
    }

    /**
     * 计算两个村庄之间的贸易潜力
     */
    private double calculateTradePotential(
        VillageData villageA,
        VillageData villageB,
        ServerLevel level
    ) {
        // 距离因子：距离越近，潜力越高
        float distance = EnhancedPathCalculator.calculateEffectiveDistance(
            level, villageA, villageB
        );
        double distanceFactor = 1.0 / (1.0 + distance / 1000.0);

        // 资源互补性：互补性越高，潜力越高
        double complementarity = calculateResourceComplementarity(
            villageA.getEconomyData(),
            villageB.getEconomyData()
        );

        // 外交关系：关系越好，潜力越高
        DiplomaticRelation relation = diplomaticManager.getRelation(
            villageA.getVillageId(),
            villageB.getVillageId()
        );
        double relationFactor = relation != null ?
            relation.getRelationLevel() / 100.0 : 0.5;

        // 规模因子：村庄规模越大，潜力越高
        double scaleFactor = Math.sqrt(
            villageA.getPopulation() * villageB.getPopulation()
        ) / 100.0;

        return distanceFactor * complementarity * relationFactor * scaleFactor;
    }
}
```

### 3.2 外交系统设计

**扩展 VillageTradeRelation 为 VillageDiplomaticRelation：**

```java
/**
 * 村庄外交关系
 * 扩展自贸易关系，增加外交维度
 */
public class VillageDiplomaticRelation extends VillageTradeRelation {

    // 外交状态
    private DiplomaticState diplomaticState;

    // 条约列表
    private List<Treaty> activeTreaties;

    // 声望值（村庄A对村庄B的评价）
    private float reputationAtoB;
    private float reputationBtoA;

    // 外交历史
    private List<DiplomaticEvent> history;

    /**
     * 外交状态枚举
     */
    public enum DiplomaticState {
        WAR(0, false, 2.0f),           // 战争
        CEASEFIRE(1, true, 1.5f),      // 停火
        COLD_WAR(2, true, 1.2f),       // 冷战
        NEUTRAL(3, true, 1.0f),        // 中立
        PEACE(4, true, 0.8f),          // 和平
        ALLIANCE(5, true, 0.5f),       // 结盟
        FEDERATION(6, true, 0.2f);     // 联邦

        private final int level;
        private final boolean canTrade;
        private final float taxModifier;

        // Constructor and getters...
    }

    /**
     * 签署条约
     */
    public boolean signTreaty(Treaty treaty) {
        // 检查是否满足条约条件
        if (!treaty.canSign(this)) {
            return false;
        }

        activeTreaties.add(treaty);
        treaty.applyEffects(this);

        // 发布条约签署事件
        NeoForge.EVENT_BUS.post(new TreatySignedEvent(
            this.getVillageA(),
            this.getVillageB(),
            treaty
        ));

        return true;
    }

    /**
     * 计算综合外交分数
     * 用于确定外交状态
     */
    public float calculateDiplomaticScore() {
        float score = 0;

        // 基础关系等级
        score += getRelationLevel() * 0.3f;

        // 声望
        score += (reputationAtoB + reputationBtoA) * 0.2f;

        // 贸易量
        score += Math.min(getTotalTradeVolume() / 10000f, 1.0f) * 0.2f;

        // 条约加成
        for (Treaty treaty : activeTreaties) {
            score += treaty.getDiplomaticBonus();
        }

        return score;
    }
}
```

**新增 Treaty 系统：**

```java
/**
 * 条约基类
 */
public abstract class Treaty {

    protected final UUID treatyId;
    protected final TreatyType type;
    protected final long signedTime;
    protected final long duration; // 持续时间（tick），-1表示永久

    /**
     * 检查是否可以签署
     */
    public abstract boolean canSign(VillageDiplomaticRelation relation);

    /**
     * 应用条约效果
     */
    public abstract void applyEffects(VillageDiplomaticRelation relation);

    /**
     * 检查条约是否仍然有效
     */
    public boolean isValid(long currentGameTime) {
        if (duration == -1) return true;
        return currentGameTime < signedTime + duration;
    }
}

/**
 * 贸易协定
 */
public class TradeAgreement extends Treaty {

    private final float taxReduction;    // 税收减免
    private final List<Item> tariffFreeItems; // 免税商品

    @Override
    public boolean canSign(VillageDiplomaticRelation relation) {
        // 至少需要 NEUTRAL 关系
        return relation.getDiplomaticState().ordinal() >=
            DiplomaticState.NEUTRAL.ordinal();
    }

    @Override
    public void applyEffects(VillageDiplomaticRelation relation) {
        // 降低贸易税率
        relation.setTaxModifier(
            relation.getTaxModifier() * (1 - taxReduction)
        );
    }
}

/**
 * 互不侵犯条约
 */
public class NonAggressionPact extends Treaty {

    @Override
    public boolean canSign(VillageDiplomaticRelation relation) {
        // 不能在战争状态签署
        return relation.getDiplomaticState() != DiplomaticState.WAR;
    }

    @Override
    public void applyEffects(VillageDiplomaticRelation relation) {
        // 防止军事冲突
        relation.setMilitaryAccess(false);
    }
}
```

### 3.3 经济依赖系统设计

**新增 EconomicDependencyGraph：**

```java
/**
 * 经济依赖图
 * 记录村庄间的资源依赖关系
 */
public class EconomicDependencyGraph {

    // 依赖矩阵：dependencyMatrix[villageA][villageB][resource] = dependencyLevel
    private final Map<UUID, Map<UUID, Map<Resource, Float>>> dependencyMatrix;

    // 供应链网络
    private final SupplyChainNetwork supplyChainNetwork;

    /**
     * 计算村庄A对村庄B的资源依赖度
     */
    public float calculateDependency(
        UUID villageA,
        UUID villageB,
        Resource resource
    ) {
        // 获取村庄A的资源需求
        float demand = getDemand(villageA, resource);

        // 获取从村庄B进口的数量
        float importVolume = getImportVolume(villageA, villageB, resource);

        // 依赖度 = 进口量 / 总需求
        return demand > 0 ? importVolume / demand : 0;
    }

    /**
     * 计算经济冲击传播
     * 当某个村庄经济变化时，计算对其他村庄的影响
     */
    public Map<UUID, Float> calculateShockPropagation(
        UUID sourceVillage,
        EconomicShock shock
    ) {
        Map<UUID, Float> impacts = new HashMap<>();

        // 使用广度优先搜索传播冲击
        Queue<UUID> queue = new LinkedList<>();
        Set<UUID> visited = new HashSet<>();

        queue.offer(sourceVillage);
        visited.add(sourceVillage);
        impacts.put(sourceVillage, shock.getInitialImpact());

        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            float currentImpact = impacts.get(current);

            // 找到依赖当前村庄的其他村庄
            for (UUID dependent : findDependents(current)) {
                if (!visited.contains(dependent)) {
                    visited.add(dependent);

                    // 计算传播影响
                    float dependencyStrength = calculateTotalDependency(dependent, current);
                    float propagatedImpact = currentImpact * dependencyStrength * shock.getPropagationFactor();

                    impacts.put(dependent, propagatedImpact);
                    queue.offer(dependent);
                }
            }
        }

        return impacts;
    }

    /**
     * 计算市场整合度
     * 用于判断村庄间经济联系的紧密程度
     */
    public float calculateMarketIntegration(UUID villageA, UUID villageB) {
        // 计算价格相关性
        float priceCorrelation = calculatePriceCorrelation(villageA, villageB);

        // 计算贸易强度
        float tradeIntensity = calculateTradeIntensity(villageA, villageB);

        // 计算依赖对称性
        float dependencySymmetry = calculateDependencySymmetry(villageA, villageB);

        return (priceCorrelation + tradeIntensity + dependencySymmetry) / 3.0f;
    }
}
```

**新增 SupplyChain 类：**

```java
/**
 * 供应链
 * 跟踪从原材料到成品的完整链路
 */
public class SupplyChain {

    private final List<SupplyChainNode> nodes;
    private final Map<Resource, Float> efficiency; // 各环节效率

    /**
     * 计算供应链脆弱性
     */
    public float calculateVulnerability() {
        float vulnerability = 0;

        for (SupplyChainNode node : nodes) {
            // 单一来源依赖增加脆弱性
            if (node.getSupplierCount() == 1) {
                vulnerability += 0.2f;
            }

            // 低效率环节增加脆弱性
            float efficiency = this.efficiency.get(node.getResource());
            if (efficiency < 0.5f) {
                vulnerability += (1.0f - efficiency) * 0.3f;
            }
        }

        return Math.min(vulnerability, 1.0f);
    }

    /**
     * 模拟供应链中断
     */
    public SupplyChainDisruption simulateDisruption(
        UUID disruptedVillage,
        int duration
    ) {
        SupplyChainDisruption disruption = new SupplyChainDisruption();

        for (SupplyChainNode node : nodes) {
            if (node.getSupplierVillage().equals(disruptedVillage)) {
                // 计算替代供应商
                List<UUID> alternatives = findAlternativeSuppliers(node);

                if (alternatives.isEmpty()) {
                    // 无替代，完全中断
                    disruption.addCompleteLoss(node.getResource());
                } else {
                    // 有替代，部分中断
                    float replacementRate = estimateReplacementRate(node, alternatives);
                    disruption.addPartialLoss(node.getResource(), 1.0f - replacementRate);
                }
            }
        }

        return disruption;
    }
}
```

### 3.4 人口迁移系统设计

**新增 MigrationSystem：**

```java
/**
 * 人口迁移系统
 * 基于推拉理论模拟村民迁移
 */
public class MigrationSystem {

    private final MigrationQueue queue;
    private final Map<UUID, Long> cooldownMap; // 迁移冷却

    /**
     * 评估村民迁移意愿
     */
    public float evaluateMigrationWillingness(
        VillagerData villager,
        VillageData currentVillage,
        VillageData targetVillage
    ) {
        // 推力因素（当前村庄）
        float pushFactor = calculatePushFactor(villager, currentVillage);

        // 拉力因素（目标村庄）
        float pullFactor = calculatePullFactor(villager, targetVillage);

        // 阻力因素
        float resistanceFactor = calculateResistanceFactor(
            currentVillage,
            targetVillage
        );

        // 迁移意愿 = (推力 + 拉力) / 阻力
        return (pushFactor + pullFactor) / resistanceFactor;
    }

    /**
     * 计算推力因素
     */
    private float calculatePushFactor(VillagerData villager, VillageData village) {
        float push = 0;

        // 低幸福度
        if (villager.getHappiness() < 0.3f) {
            push += (0.3f - villager.getHappiness()) * 2.0f;
        }

        // 低忠诚度
        if (villager.getLoyalty() < 0.2f) {
            push += (0.2f - villager.getLoyalty()) * 1.5f;
        }

        // 高压力
        if (villager.getStress() > 0.7f) {
            push += (villager.getStress() - 0.7f) * 1.0f;
        }

        // 村庄经济衰退
        if (village.getEconomyData().isInRecession()) {
            push += 0.5f;
        }

        // 战争威胁
        DiplomaticState state = getDiplomaticState(village);
        if (state == DiplomaticState.WAR || state == DiplomaticState.COLD_WAR) {
            push += 0.8f;
        }

        return push;
    }

    /**
     * 计算拉力因素
     */
    private float calculatePullFactor(VillagerData villager, VillageData targetVillage) {
        float pull = 0;

        // 经济机会
        float economicOpportunity = targetVillage.getEconomyData()
            .getJobOpportunityScore(villager.getProfession());
        pull += economicOpportunity * 0.4f;

        // 村庄繁荣度
        pull += targetVillage.getProsperityScore() * 0.3f;

        // 已有亲友
        int relativesInTarget = countRelativesInVillage(villager, targetVillage);
        pull += relativesInTarget * 0.1f;

        // 文化相似性
        float culturalSimilarity = calculateCulturalSimilarity(
            villager.getHomeVillage(),
            targetVillage
        );
        pull += culturalSimilarity * 0.2f;

        return pull;
    }

    /**
     * 计算阻力因素
     */
    private float calculateResistanceFactor(
        VillageData currentVillage,
        VillageData targetVillage
    ) {
        float resistance = 1.0f;

        // 距离成本
        float distance = EnhancedPathCalculator.calculateEffectiveDistance(
            currentVillage,
            targetVillage
        );
        resistance += distance / 1000.0f * 0.5f;

        // 迁移成本
        float migrationCost = calculateMigrationCost(currentVillage, targetVillage);
        resistance += migrationCost * 0.3f;

        // 外交限制
        DiplomaticRelation relation = getDiplomaticRelation(
            currentVillage.getVillageId(),
            targetVillage.getVillageId()
        );
        if (relation != null && !relation.allowsMigration()) {
            resistance += 2.0f;
        }

        return resistance;
    }
}
```

### 3.5 军事冲突系统设计（纯模拟）

**新增 ConflictSimulationEngine：**

```java
/**
 * 冲突模拟引擎
 * 使用 Lanchester 方程模拟军事冲突
 */
public class ConflictSimulationEngine {

    /**
     * 模拟冲突
     * 完全后台计算，玩家不直接参与
     */
    public CompletableFuture<ConflictResult> simulateConflict(
        ConflictContext context
    ) {
        return CompletableFuture.supplyAsync(() -> {
            // 计算双方军事实力
            MilitaryStrength attackerStrength = calculateMilitaryStrength(
                context.getAttacker()
            );
            MilitaryStrength defenderStrength = calculateMilitaryStrength(
                context.getDefender()
            );

            // 应用地形加成
            float terrainBonus = calculateTerrainBonus(
                context.getBattlefield(),
                context.getDefender()
            );
            defenderStrength.applyTerrainBonus(terrainBonus);

            // 应用外交加成（盟友支援）
            applyAllianceBonus(context.getAttacker(), attackerStrength);
            applyAllianceBonus(context.getDefender(), defenderStrength);

            // 使用 Lanchester 方程模拟战斗
            BattleSimulation simulation = new BattleSimulation(
                attackerStrength,
                defenderStrength,
                context.getDuration()
            );

            return simulation.run();
        }, SIMULATION_POOL);
    }

    /**
     * 计算村庄军事实力
     */
    private MilitaryStrength calculateMilitaryStrength(VillageData village) {
        MilitaryStrength strength = new MilitaryStrength();

        // 人口基数
        int population = village.getPopulation();
        strength.setManpower(population);

        // 设施贡献
        for (VillageFacility facility : village.getFacilities()) {
            if (facility.getType() instanceof IMilitaryFacility) {
                IMilitaryFacility military = (IMilitaryFacility) facility.getType();
                strength.addEquipment(military.getEquipmentContribution());
                strength.addTraining(military.getTrainingLevel());
            }
        }

        // 经济支持
        float economicSupport = village.getEconomyData()
            .getMilitaryBudget() / 1000.0f;
        strength.setLogistics(economicSupport);

        // 士气
        float morale = calculateMorale(village);
        strength.setMorale(morale);

        return strength;
    }

    /**
     * 应用战斗结果
     * 在主线程执行
     */
    public void applyConflictResult(ConflictResult result) {
        // 更新外交关系
        updateDiplomaticRelations(result);

        // 处理领土变更
        if (result.hasTerritorialChange()) {
            transferTerritory(result);
        }

        // 处理赔款
        if (result.hasReparations()) {
            transferReparations(result);
        }

        // 处理人口损失
        applyPopulationLoss(result);

        // 处理设施破坏
        applyFacilityDamage(result);

        // 发布冲突结束事件
        NeoForge.EVENT_BUS.post(new ConflictEndEvent(result));
    }
}

/**
 * Lanchester 战斗模拟
 */
public class BattleSimulation {

    private final MilitaryStrength attacker;
    private final MilitaryStrength defender;
    private final int maxDuration;

    /**
     * 使用 Lanchester 方程模拟战斗过程
     */
    public ConflictResult run() {
        int time = 0;
        float attackerForces = attacker.getTotalStrength();
        float defenderForces = defender.getTotalStrength();

        while (time < maxDuration && attackerForces > 0 && defenderForces > 0) {
            // Lanchester 线性律（适用于近战）
            // dA/dt = -β * D
            // dD/dt = -α * A

            float attackerLoss = defender.getCombatPower() * 0.01f;
            float defenderLoss = attacker.getCombatPower() * 0.01f;

            attackerForces -= attackerLoss;
            defenderForces -= defenderLoss;

            time++;
        }

        // 确定胜负
        ConflictOutcome outcome;
        if (attackerForces <= 0) {
            outcome = ConflictOutcome.DEFENDER_VICTORY;
        } else if (defenderForces <= 0) {
            outcome = ConflictOutcome.ATTACKER_VICTORY;
        } else {
            outcome = ConflictOutcome.STALEMATE;
        }

        return new ConflictResult(
            outcome,
            attackerForces / attacker.getTotalStrength(),
            defenderForces / defender.getTotalStrength(),
            time
        );
    }
}
```

---

## 4. 性能优化策略

### 4.1 分批处理

**策略：** 将大规模计算分散到多个 tick

| 系统类型 | 更新频率 | 每tick处理量 |
|---------|---------|-------------|
| 贸易路线 | 每 20 tick | 10 个村庄 |
| 外交关系 | 每 100 tick | 5 对关系 |
| 经济依赖 | 每 200 tick | 全量（图算法） |
| 人口迁移 | 每 24000 tick | 100 个迁移请求 |
| 军事冲突 | 按需触发 | 异步计算 |

### 4.2 缓存策略

**推荐缓存的数据：**
- 村庄间距离（仅在村庄位置变化时更新）
- 关系矩阵（仅在关系变化时更新）
- 经济依赖图（每游戏日更新一次）
- 影响力分数（每游戏周更新一次）

**缓存失效策略：**
- 事件驱动失效（村庄创建/删除、关系变化）
- 定时失效（经济数据每日刷新）
- 手动失效（玩家干预）

### 4.3 异步计算

**适合异步的计算：**
- 军事冲突模拟
- 经济冲击传播
- 路径验证
- 供应链分析

**注意事项：**
- 使用快照模式传递数据
- 结果通过主线程回调应用
- 限制线程池大小（建议 2-4 个线程）

### 4.4 网络优化

**同步策略：**
- 仅同步玩家可见的数据
- 使用增量更新
- 客户端缓存，服务端推送变更

**Payload 大小限制：**
- 单个 Payload 不超过 32KB
- 大数据分片传输
- 使用压缩（如 GZIP）

---

## 5. 反模式与避免方案

### 5.1 性能反模式

| 反模式 | 问题 | 替代方案 |
|--------|------|---------|
| 每 tick 遍历所有村庄关系 | O(n²) 复杂度 | 分批处理 + 事件驱动 |
| 主线程执行复杂计算 | 阻塞游戏循环 | 异步计算 + 回调 |
| 频繁同步大量数据 | 网络带宽浪费 | 增量更新 + 客户端缓存 |
| 实时 A* 路径查找 | 加载大量区块 | 预计算 + 近似算法 |
| 静态变量存储世界引用 | 多世界问题 | Attachment 或参数传递 |

### 5.2 设计反模式

| 反模式 | 问题 | 替代方案 |
|--------|------|---------|
| 紧耦合系统 | 难以维护和测试 | 事件驱动 + 接口隔离 |
| 过度模拟 | 性能问题 | 抽象层次 + 采样模拟 |
| 忽略边界情况 | 运行时错误 | 防御性编程 + 单元测试 |
| 硬编码配置 | 灵活性差 | 配置文件 + 数据驱动 |

### 5.3 数据持久化反模式

| 反模式 | 问题 | 替代方案 |
|--------|------|---------|
| NBT 过度嵌套 | 序列化性能差 | 扁平化结构 |
| 不调用 setDirty() | 数据丢失 | 数据变化时立即标记 |
| 忽略版本迁移 | 存档不兼容 | 版本号 + 迁移逻辑 |
| 同步阻塞 I/O | 卡顿 | 异步保存 |

---

## 6. 实施建议

### 6.1 分阶段实施

**阶段 1：贸易路线扩展（优先级：高）**
- 增强路径计算
- 实现路线自动发现
- 添加运输任务调度
- 优化性能

**阶段 2：外交系统（优先级：高）**
- 扩展 VillageTradeRelation
- 实现条约系统
- 添加外交事件
- 实现声望系统

**阶段 3：经济依赖（优先级：中）**
- 实现依赖图
- 添加供应链网络
- 实现冲击传播
- 计算市场整合度

**阶段 4：人口迁移（优先级：中）**
- 实现迁移意愿评估
- 添加迁移队列
- 实现迁移限制
- 添加迁移事件

**阶段 5：军事冲突（优先级：低）**
- 实现冲突模拟引擎
- 添加军事实力计算
- 实现战斗结果应用
- 添加战后重建

### 6.2 测试策略

**单元测试：**
- 距离计算
- 关系矩阵操作
- 迁移意愿评估
- 军事实力计算

**集成测试：**
- 贸易路线建立流程
- 外交事件传播
- 经济冲击传播
- 迁移执行

**性能测试：**
- 100 个村庄的关系矩阵更新
- 1000 个迁移请求处理
- 军事冲突模拟耗时

**压力测试：**
- 500 个村庄的完整系统运行
- 长时间运行稳定性
- 内存泄漏检测

### 6.3 监控与调试

**日志记录：**
- 关键操作日志（路线建立、条约签署、冲突开始）
- 性能日志（处理耗时、队列长度）
- 错误日志（异常、失败操作）

**调试工具：**
- 命令行工具（查询关系、强制迁移、模拟冲突）
- 可视化工具（关系图、依赖图）
- 性能分析工具（tick 耗时分析）

---

## 7. 技术债务处理

### 7.1 现有技术债务

| 问题 | 优先级 | 解决方案 |
|------|--------|---------|
| `VillageGenesis.level` 静态变量 | 高 | 使用 Attachment 或参数传递 |
| `VillageManager.commodityMap` 未持久化 | 中 | 移入 VillageManager 持久化 |
| `TransactionManager` 回调嵌套 | 中 | 改为循环或限制递归深度 |

### 7.2 新增技术债务预防

**代码审查清单：**
- [ ] 是否避免了静态变量存储世界引用？
- [ ] 是否实现了分批处理？
- [ ] 是否调用了 setDirty()？
- [ ] 是否处理了多世界场景？
- [ ] 是否添加了单元测试？

**文档要求：**
- 每个新系统需要架构文档
- 复杂算法需要注释说明
- 性能关键路径需要标注

---

## 8. 参考资源

### 8.1 NeoForge 官方文档

- [SavedData](https://docs.neoforged.net/docs/1.21.1/datastorage/saveddata)
- [Networking Payload](https://docs.neoforged.net/docs/1.21.1/networking/payload)
- [Event System](https://docs.neoforged.net/docs/1.21.1/concepts/events)
- [BlockEntity Ticking](https://docs.neoforged.net/docs/1.21.1/blockentities)

### 8.2 学术参考

- **推拉理论（Push-Pull Theory）**: Everett Lee, "A Theory of Migration", 1966
- **Lanchester 方程**: Frederick Lanchester, "Aircraft in Warfare", 1916
- **PageRank 算法**: Page et al., "The PageRank Citation Ranking", 1999
- **供应链脆弱性**: Wagner & Bode, "An Empirical Investigation into Supply Chain Vulnerability", 2006

### 8.3 类似模组参考

- **Millénaire**: 村庄经济与贸易系统
- **TerraFirmaCraft**: 复杂经济模拟
- **MineColonies**: 村庄管理与外交

---

## 9. 结论

本研究为 VillageGenesis 模组的村庄互联系统提供了完整的技术方案，涵盖贸易路线、外交、经济依赖、人口迁移、军事冲突五个维度。

**核心建议：**
1. 使用 SavedData + StreamCodec 组合进行数据持久化和同步
2. 使用分批处理和异步计算优化性能
3. 使用事件驱动架构解耦系统
4. 避免静态变量、每 tick 遍历等反模式
5. 分阶段实施，优先完成贸易路线和外交系统

**预期效果：**
- 村庄间形成复杂的经济与外交网络
- 玩家可通过间接手段影响村庄发展
- 系统性能保持稳定，不影响游戏体验
- 代码结构清晰，易于维护和扩展

---

**文档版本:** 1.0
**最后更新:** 2026-03-26
**作者:** GSD Project Researcher
**审核:** Pending
