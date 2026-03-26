# 村庄间系统架构设计

**研究日期:** 2026-03-26
**研究焦点:** 村庄经济模拟与互联架构
**目标里程碑:** 添加 TradeRoute 和村庄互联系统

---

## 1. 架构概览

### 1.1 设计目标

在现有 VillageGenesis 单村庄系统基础上，构建村庄间交互层，实现：
- 贸易路线网络
- 外交关系系统
- 经济依赖机制
- 人口迁移模拟
- 政治军事互动

### 1.2 核心设计原则

| 原则 | 说明 |
|------|------|
| **最小侵入** | 不修改 VillageManager/VillageData 核心逻辑 |
| **事件驱动** | 通过事件系统解耦村庄间系统与单村庄系统 |
| **分层协调** | 新增协调层管理村庄间关系，不破坏现有层级 |
| **性能优先** | 分片更新 + 异步计算，保证游戏流畅度 |
| **玩家影响者** | 玩家可影响但非决定性因素（最大 30% 影响力） |

---

## 2. 组件架构

### 2.1 层级结构

```
┌─────────────────────────────────────────────────────────────┐
│                    World 级别 (SavedData)                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              InterVillageManager                     │    │
│  │  ┌─────────────┬─────────────┬─────────────────┐    │    │
│  │  │ TradeRoute  │ Diplomacy   │ Economic        │    │    │
│  │  │ Manager     │ Manager     │ Interdependence │    │    │
│  │  │ (现有增强)   │ (新增)      │ Manager (新增)   │    │    │
│  │  └─────────────┴─────────────┴─────────────────┘    │    │
│  │  ┌─────────────┬─────────────┐                      │    │
│  │  │ Migration   │ Political   │                      │    │
│  │  │ Manager     │ Manager     │                      │    │
│  │  │ (新增)      │ (新增)      │                      │    │
│  │  └─────────────┴─────────────┘                      │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 事件/接口
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Village 级别 (SavedData)                  │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                  VillageManager                      │    │
│  │  Map<UUID, VillageData> villages                     │    │
│  │  Map<Integer, Set<ITrader>> commodityMap             │    │
│  └─────────────────────────────────────────────────────┘    │
│                              │                               │
│                              ▼                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                   VillageData                        │    │
│  │  - villageId, villageLevel, evolutionStage           │    │
│  │  - FacilityManager facilityManager                   │    │
│  │  - VillageEconomyData economyData                    │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 组件边界定义

#### 2.2.1 InterVillageManager（新增 - 协调层）

**职责：**
- 管理所有村庄间关系数据
- 协调各子系统（贸易、外交、迁移等）
- 提供统一的村庄间交互接口
- 世界级数据持久化

**边界：**
- 不直接操作 VillageData 内部数据
- 通过 VillageManager.getInstance() 获取村庄引用
- 通过事件系统通知村庄状态变化

**数据结构：**
```java
public class InterVillageManager extends SavedData {
    private final Map<RelationKey, VillageRelation> relations;
    private final TradeRouteManager tradeRouteManager;  // 现有增强
    private final DiplomacyManager diplomacyManager;     // 新增
    private final EconomicInterdependenceManager economicManager; // 新增
    private final MigrationManager migrationManager;     // 新增
    private final PoliticalManager politicalManager;     // 新增
}
```

#### 2.2.2 TradeRouteManager（现有增强）

**现有功能：**
- VillageTradeRelation 管理（关系等级、信任度）
- TradeRoute 管理（路线、运输成本）
- 税收计算
- 距离计算

**需要增强：**
- 自动贸易路线发现与建立
- 批量贸易执行
- 贸易流量统计与分析
- 与外交系统集成（关系影响贸易条件）

**集成点：**
```java
// 现有：VillageTradeRelation 已有 relationLevel, trustLevel
// 增强：与 DiplomacyManager 共享关系数据
public class TradeRouteManager {
    // 新增：引用外交管理器
    private DiplomacyManager diplomacyManager;

    // 新增：自动建立贸易路线
    public void autoEstablishRoutes();

    // 新增：执行批量贸易
    public void executeBatchTrade(UUID fromVillage, UUID toVillage);
}
```

#### 2.2.3 DiplomacyManager（新增）

**职责：**
- 管理村庄间外交状态
- 处理外交事件（结盟、宣战、和谈）
- 计算外交关系评分
- 触发外交事件

**数据结构：**
```java
public class DiplomacyManager {
    // 外交状态枚举
    public enum DiplomacyState {
        HOSTILE,      // 敌对
        UNFRIENDLY,   // 不友好
        NEUTRAL,      // 中立
        FRIENDLY,     // 友好
        ALLIED,       // 联盟
        VASSAL        // 附庸
    }

    // 外交关系数据
    public static class DiplomacyRelation {
        private DiplomacyState state;
        private float relationScore;      // -100 到 100
        private long establishedTime;
        private List<DiplomacyEvent> history;
        private float playerInfluence;    // 玩家影响系数
    }
}
```

#### 2.2.4 EconomicInterdependenceManager（新增）

**职责：**
- 计算村庄间经济依赖度
- 分析资源互补性
- 预测经济影响
- 管理经济制裁

**数据结构：**
```java
public class EconomicInterdependenceManager {
    // 资源依赖数据
    public static class ResourceDependency {
        private Item resource;
        private float dependencyScore;    // 0-1，依赖程度
        private UUID primarySupplier;     // 主要供应村庄
        private float importRatio;        // 进口占比
    }

    // 经济依赖关系
    public static class EconomicRelation {
        private Map<Item, ResourceDependency> dependencies;
        private float overallDependency;  // 总体依赖度
        private float tradeBalance;       // 贸易顺差/逆差
    }
}
```

#### 2.2.5 MigrationManager（新增）

**职责：**
- 计算村民迁移意愿
- 处理迁移决策
- 更新村民所属村庄
- 统计迁移数据

**数据结构：**
```java
public class MigrationManager {
    // 迁移原因枚举
    public enum MigrationReason {
        ECONOMIC,      // 经济因素
        SAFETY,        // 安全因素
        SOCIAL,        // 社会因素
        POLITICAL,     // 政治因素
        RANDOM         // 随机因素
    }

    // 迁移决策
    public static class MigrationDecision {
        private UUID fromVillage;
        private UUID toVillage;
        private MigrationReason reason;
        private float willingness;        // 迁移意愿 0-1
        private List<UUID> villagers;     // 迁移村民列表
    }
}
```

#### 2.2.6 PoliticalManager（新增）

**职责：**
- 管理政治互动（联盟、战争、吞并）
- 模拟军事冲突（纯模拟，无玩家战斗）
- 处理政治事件
- 管理附庸关系

**数据结构：**
```java
public class PoliticalManager {
    // 政治事件类型
    public enum PoliticalEventType {
        ALLIANCE_FORMED,    // 结盟
        ALLIANCE_BROKEN,    // 破盟
        WAR_DECLARED,       // 宣战
        PEACE_TREATY,       // 和约
        ANNEXATION,         // 吞并
        VASSALAGE,          // 附庸化
        LIBERATION          // 解放
    }

    // 军事冲突模拟
    public static class MilitaryConflict {
        private UUID attacker;
        private UUID defender;
        private float attackerStrength;
        private float defenderStrength;
        private ConflictResult result;    // 模拟结果
    }
}
```

---

## 3. 数据流架构

### 3.1 主数据流

```
[世界 Tick 事件]
        │
        ▼
┌───────────────────────────┐
│   InterVillageManager     │
│   .tick()                 │
└───────────────────────────┘
        │
        ├──▶ [分片索引计算] tickCount % UPDATE_INTERVAL
        │
        ├──▶ [TradeRouteManager]
        │        │
        │        ├── 计算贸易流量
        │        ├── 执行批量贸易
        │        └── 更新 VillageTradeRelation
        │                │
        │                └──▶ VillageA.economyData ←→ VillageB.economyData
        │
        ├──▶ [DiplomacyManager]
        │        │
        │        ├── 计算外交评分
        │        ├── 检查状态变化
        │        └── 发布 DiplomacyStateChangedEvent
        │                │
        │                └──▶ TradeRouteManager 响应（调整贸易条件）
        │
        ├──▶ [EconomicInterdependenceManager]
        │        │
        │        ├── 更新依赖度矩阵
        │        └── 计算经济影响
        │
        ├──▶ [MigrationManager]
        │        │
        │        ├── 计算迁移意愿
        │        ├── 执行迁移决策
        │        └── 更新 VillagerData.villageId
        │
        └──▶ [PoliticalManager]
                 │
                 ├── 处理政治事件
                 └── 模拟军事冲突
```

### 3.2 事件驱动流

```
[VillageCreatedEvent]
        │
        └──▶ InterVillageManager.initializeVillageRelations()

[VillageRemovedEvent]
        │
        └──▶ InterVillageManager.cleanupVillageRelations()

[VillageStageChangedEvent]
        │
        ├──▶ DiplomacyManager.recalculateRelations()
        ├──▶ TradeRouteManager.reassessTradeConditions()
        └──▶ MigrationManager.updateAttractionScores()

[DiplomacyStateChangedEvent]
        │
        ├──▶ TradeRouteManager.adjustTradeConditions()
        └──▶ MigrationManager.recalculateSafety()

[TradeCompletedEvent]
        │
        ├──▶ EconomicInterdependenceManager.updateDependency()
        └──▶ DiplomacyManager.improveRelation()

[MigrationEvent]
        │
        ├──▶ VillageA.population--
        ├──▶ VillageB.population++
        └──▶ VillagerData.villageId 更新
```

### 3.3 关系键设计

**问题：** 如何唯一标识两个村庄之间的关系？

**解决方案：**
```java
public class RelationKey {
    private final UUID villageA;
    private final UUID villageB;

    // 构造时确保顺序一致
    public RelationKey(UUID v1, UUID v2) {
        if (v1.compareTo(v2) < 0) {
            this.villageA = v1;
            this.villageB = v2;
        } else {
            this.villageA = v2;
            this.villageB = v1;
        }
    }

    // 字符串键用于 Map
    public String toKey() {
        return villageA + "_" + villageB;
    }
}
```

**现有实现：** TradeRouteManager.createRelationKey() 已实现相同逻辑。

---

## 4. 构建顺序

### 4.1 Phase 依赖图

```
Phase 1: 基础设施层
├── RelationKey 工具类
├── VillageRelation 基类
└── InterVillageManager SavedData
    │
    ▼
Phase 2: 贸易系统增强
├── TradeRouteManager 增强
│   ├── 自动路线发现
│   ├── 批量贸易执行
│   └── 贸易流量统计
└── TradeEvent 事件系统
    │
    ▼
Phase 3: 外交系统
├── DiplomacyManager
├── DiplomacyState 枚举
├── DiplomacyRelation 数据类
└── DiplomacyEvent 事件
    │
    ▼
Phase 4: 经济依赖系统
├── EconomicInterdependenceManager
├── ResourceDependency 数据类
└── EconomicRelation 数据类
    │
    ▼
Phase 5: 人口迁移系统
├── MigrationManager
├── MigrationReason 枚举
└── MigrationDecision 数据类
    │
    ▼
Phase 6: 政治军事系统
├── PoliticalManager
├── MilitaryConflict 模拟器
└── PoliticalEvent 事件
```

### 4.2 详细构建计划

#### Phase 1: 基础设施层（预计 2-3 天）

**目标：** 建立村庄间系统的数据存储和协调框架

**任务：**
1. 创建 `InterVillageManager` SavedData 类
2. 创建 `VillageRelation` 基类（包含通用关系属性）
3. 创建 `RelationKey` 工具类（复用现有 TradeRouteManager 逻辑）
4. 注册 `InterVillageManager` 到世界存储
5. 创建 `IInterVillageContext` 接口

**验收标准：**
- [ ] InterVillageManager 可持久化到世界数据
- [ ] 可通过 RelationKey 唯一标识村庄对
- [ ] 提供获取村庄列表、关系列表的基础接口

#### Phase 2: 贸易系统增强（预计 3-4 天）

**目标：** 增强现有 TradeRouteManager，实现自动化贸易

**任务：**
1. 扩展 `TradeRouteManager` 添加自动路线发现
2. 实现批量贸易执行逻辑
3. 添加贸易流量统计和分析
4. 创建 `TradeCompletedEvent` 事件
5. 集成到 `InterVillageManager`

**验收标准：**
- [ ] 村庄可自动建立贸易路线
- [ ] 贸易执行正确更新双方经济数据
- [ ] 贸易统计数据可查询

#### Phase 3: 外交系统（预计 3-4 天）

**目标：** 实现村庄间外交关系管理

**任务：**
1. 创建 `DiplomacyManager` 类
2. 创建 `DiplomacyState` 枚举
3. 创建 `DiplomacyRelation` 数据类
4. 实现外交评分计算算法
5. 创建 `DiplomacyStateChangedEvent` 事件
6. 集成到 `InterVillageManager`
7. 与 `TradeRouteManager` 联动

**验收标准：**
- [ ] 外交状态可正确计算和更新
- [ ] 外交状态变化触发事件
- [ ] 外交状态影响贸易条件

#### Phase 4: 经济依赖系统（预计 2-3 天）

**目标：** 实现村庄间经济依赖度计算

**任务：**
1. 创建 `EconomicInterdependenceManager` 类
2. 创建 `ResourceDependency` 数据类
3. 创建 `EconomicRelation` 数据类
4. 实现依赖度计算算法
5. 集成到 `InterVillageManager`

**验收标准：**
- [ ] 可计算村庄间资源依赖度
- [ ] 依赖度数据持久化
- [ ] 依赖度影响外交和迁移

#### Phase 5: 人口迁移系统（预计 3-4 天）

**目标：** 实现村民自动迁移机制

**任务：**
1. 创建 `MigrationManager` 类
2. 创建 `MigrationReason` 枚举
3. 创建 `MigrationDecision` 数据类
4. 实现迁移意愿计算算法
5. 实现迁移执行逻辑
6. 创建 `MigrationEvent` 事件
7. 集成到 `InterVillageManager`

**验收标准：**
- [ ] 村民可因经济、安全等因素迁移
- [ ] 迁移正确更新村庄人口和村民数据
- [ ] 迁移事件可被监听

#### Phase 6: 政治军事系统（预计 4-5 天）

**目标：** 实现政治互动和军事冲突模拟

**任务：**
1. 创建 `PoliticalManager` 类
2. 创建 `PoliticalEventType` 枚举
3. 创建 `MilitaryConflict` 模拟器
4. 实现政治事件处理
5. 实现军事冲突模拟算法
6. 创建 `PoliticalEvent` 事件
7. 集成到 `InterVillageManager`

**验收标准：**
- [ ] 村庄可结盟、宣战、和谈
- [ ] 军事冲突模拟结果合理
- [ ] 政治事件影响外交和经济

---

## 5. 现有架构集成

### 5.1 VillageManager 集成

**现有接口：**
```java
public class VillageManager extends SavedData {
    private static VillageManager instance;
    private final Map<UUID, VillageData> villages;

    public static VillageManager getInstance();
    public static Optional<VillageData> getVillageData(UUID villageId);
    public void registerVillage(VillageData village);
}
```

**集成策略：**
- **不修改 VillageManager 核心代码**
- InterVillageManager 持有 VillageManager 引用
- 通过 `VillageManager.getInstance()` 获取村庄数据

**集成代码示例：**
```java
public class InterVillageManager extends SavedData {
    private VillageManager villageManager;

    public void initialize() {
        this.villageManager = VillageManager.getInstance();
    }

    public List<VillageData> getNearbyVillages(UUID villageId, double radius) {
        Optional<VillageData> center = this.villageManager.getVillageData(villageId);
        if (center.isEmpty()) return List.of();

        BlockPos centerPos = center.get().getCenterPos();
        return this.villageManager.getAllVillages().stream()
            .filter(v -> v.getCenterPos().closerThan(centerPos, radius))
            .toList();
    }
}
```

### 5.2 VillageData 集成

**现有属性：**
```java
public class VillageData {
    private UUID villageId;
    private int villageLevel;
    private VillageEvolutionStage evolutionStage;
    private FacilityManager facilityManager;
    private VillageEconomyData economyData;
}
```

**集成策略：**
- **不修改 VillageData 核心属性**
- 通过事件监听 VillageData 状态变化
- 村庄间数据存储在 InterVillageManager

**事件集成示例：**
```java
@SubscribeEvent
public void onVillageStageChanged(VillageNewStageEvent event) {
    UUID villageId = event.getVillageData().getVillageId();

    // 重新评估外交关系
    this.diplomacyManager.recalculateRelations(villageId);

    // 重新评估贸易条件
    this.tradeRouteManager.reassessTradeConditions(villageId);

    // 更新迁移吸引力
    this.migrationManager.updateAttractionScore(villageId);
}
```

### 5.3 TransactionManager 集成

**现有功能：**
- 异步交易处理
- 线程池管理
- 过滤器链模式

**集成策略：**
- 复用 TransactionManager 线程池进行异步计算
- 村庄间贸易使用现有交易系统

**集成代码示例：**
```java
public class TradeRouteManager {
    public void executeBatchTrade(UUID fromVillage, UUID toVillage) {
        // 异步计算贸易评分
        CompletableFuture.supplyAsync(() -> {
            return calculateTradeScore(fromVillage, toVillage);
        }, TransactionManager.getExecutor()).thenAccept(score -> {
            // 主线程执行实际贸易
            MinecraftServer server = ...;
            server.execute(() -> {
                executeTrade(fromVillage, toVillage, score);
            });
        });
    }
}
```

### 5.4 网络层集成

**现有网络架构：**
- NeoForge Payload 系统
- 双向通信支持

**集成策略：**
- 新增村庄间信息 Payload
- 客户端可查询村庄间关系

**新增 Payload：**
```java
public class InterVillageInfoPayload implements CustomPacketPayload {
    private List<VillageRelationInfo> relations;
    private List<TradeRouteInfo> routes;
    // ...
}
```

---

## 6. 性能优化策略

### 6.1 分片更新机制

**问题：** N 个村庄产生 O(N²) 的关系矩阵，每 tick 更新所有关系会严重影响性能。

**解决方案：**
```java
public class InterVillageManager {
    private static final int UPDATE_INTERVAL = 20; // 每 20 tick 更新一轮
    private int currentTick = 0;

    public void tick() {
        this.currentTick++;

        // 分片索引
        int shardIndex = this.currentTick % UPDATE_INTERVAL;

        // 只更新属于当前分片的关系
        List<VillageRelation> shardRelations = getRelationsForShard(shardIndex);
        for (VillageRelation relation : shardRelations) {
            updateRelation(relation);
        }
    }

    private List<VillageRelation> getRelationsForShard(int shardIndex) {
        return this.relations.values().stream()
            .filter(r -> Math.abs(r.hashCode()) % UPDATE_INTERVAL == shardIndex)
            .toList();
    }
}
```

**效果：** 将 O(N²) 的更新分散到 20 个 tick 中，每个 tick 只更新 1/20 的关系。

### 6.2 异步计算

**策略：** 复用 TransactionManager 线程池

```java
public class DiplomacyManager {
    public void calculateRelationScore(UUID villageA, UUID villageB) {
        CompletableFuture.supplyAsync(() -> {
            // 异步计算评分
            return computeScore(villageA, villageB);
        }, TransactionManager.getExecutor()).thenAcceptAsync(score -> {
            // 主线程更新数据
            updateRelationScore(villageA, villageB, score);
        }, MinecraftServer::execute);
    }
}
```

### 6.3 距离过滤

**策略：** 只计算一定距离内的村庄关系

```java
public class InterVillageManager {
    private static final double MAX_RELATION_DISTANCE = 2000.0; // 最大关系距离

    public List<VillageData> getPotentialPartners(UUID villageId) {
        return getNearbyVillages(villageId, MAX_RELATION_DISTANCE);
    }
}
```

### 6.4 懒加载关系

**策略：** 只在需要时创建关系

```java
public class InterVillageManager {
    public VillageRelation getOrCreateRelation(UUID villageA, UUID villageB) {
        RelationKey key = new RelationKey(villageA, villageB);
        return this.relations.computeIfAbsent(key, k -> {
            return new VillageRelation(villageA, villageB);
        });
    }
}
```

### 6.5 缓存策略

**策略：** 缓存计算结果，设置有效期

```java
public class EconomicInterdependenceManager {
    private Map<RelationKey, CachedDependency> dependencyCache;

    private static class CachedDependency {
        private ResourceDependency dependency;
        private long calculatedTick;
        private static final long CACHE_TTL = 100; // 100 tick 有效期

        public boolean isValid(long currentTick) {
            return currentTick - this.calculatedTick < CACHE_TTL;
        }
    }

    public ResourceDependency getDependency(UUID villageA, UUID villageB, Item resource) {
        RelationKey key = new RelationKey(villageA, villageB);
        CachedDependency cached = this.dependencyCache.get(key);

        if (cached != null && cached.isValid(currentTick)) {
            return cached.dependency;
        }

        // 重新计算
        ResourceDependency dependency = calculateDependency(villageA, villageB, resource);
        this.dependencyCache.put(key, new CachedDependency(dependency, currentTick));
        return dependency;
    }
}
```

---

## 7. 玩家影响者设计

### 7.1 影响力模型

**核心原则：** 玩家是影响者，不是决策者。可影响但非决定性因素。

**影响系数设计：**
```java
public class PlayerInfluence {
    private float tradeBonus = 0.0f;           // 贸易加成 -0.3 ~ +0.3
    private float diplomacyModifier = 0.0f;    // 外交修正 -0.3 ~ +0.3
    private float migrationAttraction = 0.0f;  // 迁移吸引力 -0.3 ~ +0.3

    private static final float MAX_INFLUENCE = 0.3f; // 最大影响 30%

    public void addTradeBonus(float amount) {
        this.tradeBonus = Math.max(-MAX_INFLUENCE,
            Math.min(MAX_INFLUENCE, this.tradeBonus + amount));
    }
}
```

### 7.2 影响方式

#### 7.2.1 贸易影响

| 玩家行为 | 影响效果 | 数值范围 |
|---------|---------|---------|
| 建造道路/桥梁 | 降低运输成本 | -5% ~ -20% |
| 建造市场 | 增加贸易吸引力 | +5% ~ +15% |
| 提供贸易优惠 | 影响贸易伙伴选择 | +0.05 ~ +0.15 |
| 破坏贸易路线 | 降低贸易效率 | -10% ~ -30% |

**实现：**
```java
public class TradeRouteManager {
    public float calculateTradeEfficiency(UUID villageA, UUID villageB) {
        float baseEfficiency = 1.0f;

        // 玩家影响
        PlayerInfluence influence = getPlayerInfluence(villageA, villageB);
        baseEfficiency += influence.getTradeBonus();

        // 设施影响
        if (hasRoad(villageA, villageB)) {
            baseEfficiency += 0.1f;
        }

        return Math.max(0.1f, Math.min(1.5f, baseEfficiency));
    }
}
```

#### 7.2.2 外交影响

| 玩家行为 | 影响效果 | 数值范围 |
|---------|---------|---------|
| 赠送礼物 | 提升友好度 | +5 ~ +20 |
| 破坏行为 | 降低友好度 | -10 ~ -30 |
| 调解冲突 | 影响外交状态 | +0.1 ~ +0.3 |
| 支持一方 | 影响战争结果 | +5% ~ +15% |

**实现：**
```java
public class DiplomacyManager {
    public void applyPlayerInfluence(UUID villageA, UUID villageB, float amount) {
        DiplomacyRelation relation = getOrCreateRelation(villageA, villageB);

        // 玩家影响系数
        relation.addPlayerInfluence(amount);

        // 重新计算外交评分
        recalculateScore(villageA, villageB);
    }
}
```

#### 7.2.3 经济影响

| 玩家行为 | 影响效果 | 数值范围 |
|---------|---------|---------|
| 投资村庄 | 提升经济实力 | +10% ~ +30% |
| 控制资源 | 影响资源依赖 | +5% ~ +20% |
| 建设设施 | 增加生产能力 | +5% ~ +15% |

#### 7.2.4 迁移影响

| 玩家行为 | 影响效果 | 数值范围 |
|---------|---------|---------|
| 改善村庄环境 | 增加吸引力 | +0.1 ~ +0.3 |
| 提供住房 | 增加吸引力 | +0.05 ~ +0.15 |
| 创造就业 | 增加吸引力 | +0.05 ~ +0.2 |

### 7.3 影响限制

**设计原则：**
1. 单次影响有上限（防止过度干预）
2. 影响有衰减（需要持续投入）
3. 影响有冷却（防止频繁操作）

**实现：**
```java
public class PlayerInfluenceManager {
    private static final float MAX_SINGLE_INFLUENCE = 0.1f; // 单次最大 10%
    private static final int INFLUENCE_COOLDOWN = 6000;     // 冷却 5 分钟

    private Map<UUID, Long> lastInfluenceTime = new HashMap<>();

    public boolean canApplyInfluence(UUID playerId) {
        Long lastTime = this.lastInfluenceTime.get(playerId);
        if (lastTime == null) return true;

        long currentTime = VillageGenesis.getGameTime();
        return currentTime - lastTime >= INFLUENCE_COOLDOWN;
    }

    public void applyInfluence(UUID playerId, UUID villageA, UUID villageB, float amount) {
        if (!canApplyInfluence(playerId)) {
            return; // 冷却中
        }

        // 限制单次影响
        amount = Math.max(-MAX_SINGLE_INFLUENCE, Math.min(MAX_SINGLE_INFLUENCE, amount));

        // 应用影响
        DiplomacyRelation relation = diplomacyManager.getOrCreateRelation(villageA, villageB);
        relation.addPlayerInfluence(amount);

        // 记录时间
        this.lastInfluenceTime.put(playerId, VillageGenesis.getGameTime());
    }
}
```

---

## 8. 数据持久化

### 8.1 InterVillageManager 持久化

```java
public class InterVillageManager extends SavedData {
    @Override
    public CompoundTag save(CompoundTag tag) {
        // 保存贸易关系
        tag.put("tradeRelations", this.tradeRouteManager.serializeNBT());

        // 保存外交关系
        tag.put("diplomacyRelations", this.diplomacyManager.serializeNBT());

        // 保存经济依赖
        tag.put("economicDependencies", this.economicManager.serializeNBT());

        // 保存迁移数据
        tag.put("migrationData", this.migrationManager.serializeNBT());

        // 保存政治数据
        tag.put("politicalData", this.politicalManager.serializeNBT());

        return tag;
    }

    public static InterVillageManager load(CompoundTag tag) {
        InterVillageManager manager = new InterVillageManager();

        if (tag.contains("tradeRelations")) {
            manager.tradeRouteManager.deserializeNBT(tag.getCompound("tradeRelations"));
        }

        // ... 其他子系统加载

        return manager;
    }
}
```

### 8.2 注册到世界存储

```java
public class ModDataSave {
    public static void register(RegisterSavedDataEvent event) {
        // 现有：VillageManager
        event.register(VillageManager.ID, VillageManager::new, VillageManager::load);

        // 新增：InterVillageManager
        event.register(InterVillageManager.ID, InterVillageManager::new, InterVillageManager::load);
    }
}
```

---

## 9. 扩展点

### 9.1 新增外交状态

```java
// 实现 DiplomacyState 枚举扩展
public enum DiplomacyState {
    HOSTILE(-100, -50),
    UNFRIENDLY(-50, 0),
    NEUTRAL(0, 30),
    FRIENDLY(30, 70),
    ALLIED(70, 100),
    VASSAL(100, 100),  // 特殊状态
    TRADE_PARTNER(0, 100); // 新增：贸易伙伴

    private final int minScore;
    private final int maxScore;
}
```

### 9.2 新增迁移原因

```java
// 实现 MigrationReason 枚举扩展
public enum MigrationReason {
    ECONOMIC,
    SAFETY,
    SOCIAL,
    POLITICAL,
    RANDOM,
    CLIMATE,    // 新增：气候因素
    DISASTER    // 新增：灾难因素
}
```

### 9.3 新增政治事件

```java
// 实现 PoliticalEventType 枚举扩展
public enum PoliticalEventType {
    ALLIANCE_FORMED,
    ALLIANCE_BROKEN,
    WAR_DECLARED,
    PEACE_TREATY,
    ANNEXATION,
    VASSALAGE,
    LIBERATION,
    TRADE_EMBARGO,  // 新增：贸易禁运
    SANCTIONS       // 新增：经济制裁
}
```

---

## 10. 质量门禁检查

### 10.1 组件边界明确性 ✓

- [x] InterVillageManager 作为协调层，职责清晰
- [x] TradeRouteManager, DiplomacyManager 等子系统边界明确
- [x] 每个组件职责单一，符合单一职责原则

### 10.2 数据流方向明确 ✓

- [x] 世界 Tick → InterVillageManager → 各子系统 → VillageData
- [x] 事件驱动解耦，单向依赖
- [x] 数据流向图清晰

### 10.3 构建顺序清晰 ✓

- [x] Phase 1-6 依赖关系明确
- [x] 每个 Phase 的任务和验收标准清晰
- [x] 预估时间合理

### 10.4 现有架构集成 ✓

- [x] 不修改 VillageManager/VillageData 核心代码
- [x] 通过事件系统解耦
- [x] 复用 TransactionManager 线程池
- [x] 集成点明确

---

## 11. 风险与缓解

### 11.1 性能风险

**风险：** 大量村庄导致 O(N²) 关系矩阵过大

**缓解措施：**
- 分片更新机制
- 距离过滤（只计算附近村庄）
- 懒加载关系
- 缓存策略

### 11.2 数据一致性风险

**风险：** 异步计算可能导致数据不一致

**缓解措施：**
- 主线程执行数据修改
- 使用快照模式进行异步计算
- 添加版本号检查

### 11.3 平衡性风险

**风险：** 村庄间系统可能导致游戏失衡

**缓解措施：**
- 玩家影响限制在 30% 以内
- 添加配置选项调整参数
- 充分测试和调优

---

## 12. 总结

本架构设计在现有 VillageGenesis 单村庄系统基础上，通过新增 InterVillageManager 协调层，实现了村庄间贸易、外交、经济依赖、人口迁移和政治军事等系统。

**核心设计决策：**
1. **分层协调** - InterVillageManager 作为协调层，不破坏现有架构
2. **事件驱动** - 通过事件系统解耦，保持系统灵活性
3. **性能优先** - 分片更新 + 异步计算，保证游戏流畅度
4. **玩家影响者** - 玩家可影响但非决定性因素，保持模拟真实性

**构建顺序：**
- Phase 1: 基础设施层（2-3 天）
- Phase 2: 贸易系统增强（3-4 天）
- Phase 3: 外交系统（3-4 天）
- Phase 4: 经济依赖系统（2-3 天）
- Phase 5: 人口迁移系统（3-4 天）
- Phase 6: 政治军事系统（4-5 天）

**总计：** 17-23 天

---

**文档版本:** 1.0
**最后更新:** 2026-03-26
**作者:** Mod开发者 (AI Assistant)
