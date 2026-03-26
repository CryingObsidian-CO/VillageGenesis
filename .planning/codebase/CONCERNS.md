# 代码库关注点分析 (CONCERNS.md)

**项目:** VillageGenesis - Minecraft 1.21.1 NeoForge Mod  
**分析日期:** 2026-03-26  
**分析范围:** 核心业务逻辑、经济系统、村民增强、网络通信

---

## 一、高优先级问题 (HIGH)

### 1.1 静态共享状态导致的线程安全问题

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\VillageManager.java`

**问题描述:**
```java
protected static final Map<UUID, VillageData> villageMap = new HashMap<>();
private static final Map<Integer, Set<ITrader>> commodityMap = new HashMap<>();
```

`VillageManager` 使用静态 `HashMap` 存储村庄数据和商品映射，但没有任何同步机制。虽然 `TransactionManager` 使用线程池异步处理交易评分，但这些静态 Map 可能在异步操作中被并发访问。

**影响:**
- 多玩家同时交易时可能导致数据竞争
- HashMap 在并发写入时可能导致数据丢失或死循环
- 服务器崩溃风险

**修复建议:**
1. 使用 `ConcurrentHashMap` 替代 `HashMap`
2. 或者在 `TransactionManager` 中确保所有对 `VillageManager` 的访问都在主线程执行
3. 添加适当的同步机制

---

### 1.2 commodityMap 未持久化问题

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\VillageManager.java:37`

**问题描述:**
```java
// TODO 这个要不要持久化
private static final Map<Integer, Set<ITrader>> commodityMap = new HashMap<>();
```

商品交易商映射未被持久化，服务器重启后数据丢失。

**影响:**
- 服务器重启后交易系统需要重新建立映射
- 可能导致交易中断或数据不一致

**修复建议:**
1. 实现 `commodityMap` 的 NBT 序列化/反序列化
2. 或者在服务器启动时重新扫描所有交易者并重建映射

---

### 1.3 村民状态系统完全未实现

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villagerEnhance\state\VillagerStates.java`

**问题描述:**
所有状态（SICK, CHILD, HOMELESS, UNEMPLOYED）的实现都是空方法：
```java
SICK("sick", 50) {
    @Override
    public void onActivate() {
        // TODO 生病状态激活逻辑
    }
    @Override
    public void tick() {
        // TODO 生病状态每刻更新
    }
    // ...
}
```

**影响:**
- 村民状态系统形同虚设，无实际功能
- 影响村民行为多样性
- 与设计文档预期不符

**修复建议:**
1. 按优先级逐步实现各状态的逻辑
2. 先实现 UNEMPLOYED（失业）状态，影响交易能力
3. 再实现 CHILD（未成年）状态，影响工作能力

---

### 1.4 网络通信处理器大量未实现

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\networking\ServerVillageInfoPayloadHandler.java`

**问题描述:**
```java
case VillageInfoPanelScreen.VillageInfoCategory.VILLAGER_LIST -> {
    // TODO 发村民列表包
}
case VillageInfoPanelScreen.VillageInfoCategory.FACILITY_LIST -> {
    // TODO 发设施信息包
}
default -> {
    // TODO 继续完善别的
}
```

**影响:**
- 村庄信息面板功能不完整
- 客户端无法获取完整村庄信息
- 用户体验受损

**修复建议:**
1. 创建对应的 Payload 类
2. 实现数据序列化和反序列化
3. 完善客户端渲染逻辑

---

## 二、中优先级问题 (MEDIUM)

### 2.1 交易系统支付协商逻辑未完成

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\economy\transaction\TransactionManager.java:323`

**问题描述:**
```java
private static IPayment negotiatePayment(ITrader buyer, ITrader seller, float totalWorkPoint) {
    // TODO: 实现支付方式协商逻辑
    // 当前只是简单取第一个匹配的支付方式
}
```

**影响:**
- 支付方式选择不够智能
- 无法根据交易金额选择最优支付方式
- 混合支付无法实现

**修复建议:**
1. 实现基于交易金额的支付方式选择策略
2. 支持多种支付方式的组合
3. 考虑交易双方的支付偏好

---

### 2.2 交易者协商能力等级硬编码

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\mixin\VillagerMixin.java:195`

**问题描述:**
```java
// TODO 交易者协商能力等级，暂时写 0
@Unique
public int villagerTrader$getNegotiationLevel() {
    return 0;
}
```

**影响:**
- 所有村民协商能力相同
- 无法体现村民个体差异
- 影响交易系统的深度

**修复建议:**
1. 在 `VillagerData` 中添加 `negotiationLevel` 字段
2. 根据村民职业、经验等因素计算协商等级
3. 实现协商等级对交易的影响

---

### 2.3 TradeRouteManager 返回 null 过多

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\economy\tradeRoute\TradeRouteManager.java`

**问题描述:**
多个方法在边界情况下返回 `null`：
```java
public VillageTradeRelation getOrCreateRelation(UUID villageA, UUID villageB) {
    if (villageA.equals(villageB)) {
        return null;  // 第45行
    }
    // ...
}
```

**影响:**
- 调用方需要频繁进行 null 检查
- 容易遗漏 null 检查导致 NPE
- 代码可读性降低

**修复建议:**
1. 使用 `Optional<T>` 包装返回值
2. 或抛出明确的异常（如 `IllegalArgumentException`）
3. 添加文档说明何时返回 null

---

### 2.4 FacilityManager 数据结构设计疑问

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\facility\FacilityManager.java:38`

**问题描述:**
```java
// HACK 似乎 category 到 facility 的映射更重要，因为 facility 有 FacilityType::tick 方法来区分不同 type 的逻辑，但是 category 没有，考虑要不要改改
private final Map<FacilityType, List<VillageFacility>> facilities;
```

**影响:**
- 按 Category 查询效率低（需要遍历所有类型）
- 数据结构可能需要重构

**修复建议:**
1. 评估 Category 查询频率
2. 如果频繁，添加 `Map<IFacilityCategory, List<VillageFacility>>` 索引
3. 或使用复合数据结构

---

### 2.5 村庄 tick 方法未实现清理逻辑

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\VillageManager.java:199`

**问题描述:**
```java
public void tick() {
    // TODO 剔除不必要的村庄优化性能
    villageMap.values().forEach(VillageData::tick);
}
```

**影响:**
- 废弃村庄不会被清理
- 内存可能持续增长
- 服务器长期运行性能下降

**修复建议:**
1. 实现村庄状态检测
2. 清理 ABANDONED 状态超过一定时间的村庄
3. 添加配置项控制清理策略

---

## 三、低优先级问题 (LOW)

### 3.1 废弃的 LegacyVillageStructurePoolElement

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\structures\LegacyVillageStructurePoolElement.java`

**问题描述:**
该类已被标记为 `@Deprecated`，但仍在代码库中保留。

**影响:**
- 代码库膨胀
- 可能造成混淆

**修复建议:**
1. 确认无引用后删除
2. 或添加 `@Deprecated(forRemoval = true)` 注解

---

### 3.2 TradeTaxCalculator 废弃方法

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\economy\tax\tradeTax\TradeTaxCalculator.java:145,153`

**问题描述:**
存在 `@Deprecated` 方法但未说明替代方案。

**修复建议:**
1. 添加 `@deprecated` JavaDoc 说明替代方法
2. 评估是否可以删除

---

### 3.3 ITradableItem 数据包配置 TODO

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\item\ITradableItem.java:10`

**问题描述:**
```java
// TODO 可以利用数据包更改
```

**影响:**
- 可配置性受限
- 需要重新编译才能修改交易属性

**修复建议:**
1. 实现数据包驱动的配置系统
2. 使用 NeoForge 的 DataMap 机制

---

### 3.4 VillagerData.addItem 方法空实现

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villagerEnhance\VillagerData.java:228`

**问题描述:**
```java
public void addItem(Item item) {
    // 空方法体
}
```

**影响:**
- 功能不完整
- 可能是遗留代码

**修复建议:**
1. 实现方法逻辑
2. 或删除该方法

---

### 3.5 贸易路线属性扩展 TODO

**位置:** `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\economy\tradeRoute\TradeRoute.java:12`

**问题描述:**
```java
// TODO 贸易路线后续考虑增加安全等级，容量等属性
```

**影响:**
- 功能扩展受限

**修复建议:**
1. 作为后续版本规划
2. 在设计文档中记录

---

## 四、代码质量统计

| 指标 | 数量 |
|------|------|
| TODO 注释 | 28 处 |
| HACK 注释 | 1 处 |
| 废弃代码 | 6 处 |
| return null | 10 处 |
| 最大文件行数 | 98 行 (StateManager.java) |

---

## 五、测试覆盖缺失区域

基于代码分析，以下区域缺乏明显的测试覆盖：

1. **交易系统核心逻辑** - `TransactionManager` 的异步交易流程
2. **村庄数据持久化** - `VillageManager` 的 NBT 序列化/反序列化
3. **贸易路线计算** - `TradeRouteManager` 的距离和税率计算
4. **村民状态管理** - `StateManager` 的状态切换逻辑
5. **设施注册与查询** - `FacilityManager` 的 CRUD 操作

---

## 六、建议优先修复顺序

1. **P0 (立即修复):** 静态 Map 线程安全问题
2. **P1 (本周修复):** 村民状态系统核心实现
3. **P2 (本月修复):** 网络通信处理器完善
4. **P3 (下版本):** 支付协商逻辑、commodityMap 持久化
5. **P4 (持续优化):** 代码清理、测试补充

---

**文档生成:** GSD Codebase Mapper  
**最后更新:** 2026-03-26
