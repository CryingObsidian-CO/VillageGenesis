# VillageGenesis 架构分析

**分析日期:** 2026-03-26
**分析焦点:** arch (架构)
**项目版本:** Minecraft 1.21.1 + NeoForge

---

## 1. 项目概述

VillageGenesis 是一个基于 NeoForge 1.21.1 的 Minecraft 模组，专注于村庄系统的深度扩展。该模组实现了完整的村庄经济系统、设施管理、村民增强和村庄演进机制。

### 核心特性
- 村庄生命周期管理（创建、演进、衰退）
- 多阶段经济系统（物物交换 -> 现代货币）
- 设施系统（建筑类型、等级、耐久）
- 村民行为增强（状态机、职业扩展）
- 异步交易处理系统

---

## 2. 架构层次

### 2.1 入口层 (Entry Layer)

```
cn.ykcryobs.vg
├── VillageGenesis.java          # 主入口类 (@Mod)
└── VillageGenesisClient.java    # 客户端入口 (@Mod dist=CLIENT)
```

**主入口类职责:**
- 注册所有 DeferredRegister（Items, Blocks, BlockEntities, DataComponents, Attachments）
- 注册配置文件（Common, Server, Client）
- 监听 `ServerStartingEvent` 初始化 ServerLevel 引用
- 提供 `getIdentifier()` 工具方法

**架构模式:** 标准 NeoForge Mod 入口模式，使用 `@EventBusSubscriber` 自动注册事件监听器。

### 2.2 注册层 (Registration Layer)

```
cn.ykcryobs.vg.init
├── ModItems.java              # 物品注册 (DeferredRegister.Items)
├── ModBlocks.java             # 方块注册 (DeferredRegister.Blocks)
├── ModBlockEntities.java      # 方块实体注册
├── ModDataComponents.java     # 数据组件注册
├── ModAttachment.java         # Attachment 数据注册
├── ModRegistries.java         # 自定义注册表 (ISellerFilter)
├── ModDataPackRegistries.java # 数据包注册表 (FacilityType)
├── ModPayload.java            # 网络载荷注册
├── ModDataSave.java           # SavedData 注册
├── ModDataMap.java            # DataMap 注册
├── ModSellerFilters.java      # 卖家过滤器注册
├── ModStructurePoolRegistries.java # 结构池注册
├── FacilityTypeRegistries.java # 设施类型注册
└── CommandInit.java           # 命令注册
```

**关键注册模式:**

1. **DeferredRegister 模式** - 标准的 NeoForge 延迟注册
   ```java
   public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
   public static final Supplier<Item> COPPER_COIN = ITEMS.register("copper_coin", CopperCoinItem::new);
   ```

2. **自定义注册表模式** - 用于扩展注册系统
   ```java
   // ModRegistries.java
   public static final ResourceKey<Registry<ISellerFilter>> SELLER_FILTER_REGISTRY_KEY = ...;
   public static final Registry<ISellerFilter> SELLER_FILTER_REGISTRY = new RegistryBuilder<>(...).create();
   ```

3. **DataPack 注册表模式** - 支持数据包定义的类型
   ```java
   // ModDataPackRegistries.java
   public static final ResourceKey<Registry<FacilityType>> FACILITY_REGISTRY_KEY = ...;
   event.dataPackRegistry(FACILITY_REGISTRY_KEY, codec, codec);
   ```

### 2.3 核心系统层 (Core System Layer)

#### 2.3.1 村庄系统 (Village System)

```
cn.ykcryobs.vg.villageSystem
├── VillageManager.java        # 村庄管理器 (SavedData)
├── VillageData.java           # 村庄数据核心类
├── VillageNameGenerator.java  # 村庄名称生成器
├── economy/                   # 经济子系统
├── facility/                  # 设施子系统
└── [枚举类型内嵌于 VillageData]
```

**VillageManager 架构:**
- 继承 `SavedData` 实现世界级数据持久化
- 单例模式管理所有村庄实例
- 维护 `Map<UUID, VillageData>` 村庄映射
- 维护 `Map<Integer, Set<ITrader>>` 商品-交易商映射

**VillageData 核心属性:**
| 属性 | 类型 | 说明 |
|------|------|------|
| villageId | UUID | 村庄唯一标识 |
| villageLevel | int | 村庄等级 (1-∞) |
| villageExp | int | 村庄经验值 |
| evolutionStage | VillageEvolutionStage | 演进阶段 |
| facilityManager | FacilityManager | 设施管理器 |
| economyData | VillageEconomyData | 经济数据 |

**演进阶段枚举:**
```
PRIMITIVE (1-10)     -> 原始部落
AGRICULTURAL (11-20) -> 农业村庄
HANDICRAFT (21-30)   -> 手工业城镇
COMMERCIAL (31-40)   -> 商业都市
INDUSTRIAL (41-50)   -> 工业城市
MODERN (51+)         -> 现代都市
```

#### 2.3.2 经济系统 (Economy System)

```
cn.ykcryobs.vg.villageSystem.economy
├── VillageEconomyData.java       # 村庄经济数据
├── market/                       # 市场系统
│   ├── MarketType.java
│   └── ResourceType.java
├── payment/                      # 支付系统
│   ├── IPayment.java             # 支付接口
│   ├── PaymentMethod.java        # 支付方式枚举
│   └── BarterPayment.java        # 物物交换实现
├── tax/                          # 税收系统
│   └── tradeTax/
│       ├── ITradeTaxCalculator.java
│       └── TradeTaxCalculator.java
├── trader/                       # 交易者系统
│   ├── ITrader.java              # 交易者接口
│   ├── TraderSnapshot.java       # 交易者快照
│   └── sellerFilter/             # 卖家过滤器
│       ├── ISellerFilter.java
│       ├── QuantityScoreFilter.java
│       └── PriceScoreFilter.java
├── transaction/                  # 交易系统
│   ├── Transaction.java
│   ├── TransactionContext.java
│   ├── TransactionManager.java
│   └── TransactionResult.java
└── tradeRoute/                   # 贸易路线
    ├── TradeRoute.java
    ├── TradeRouteManager.java
    ├── TradeType.java
    └── VillageTradeRelation.java
```

**TransactionManager 架构特点:**
- 线程池异步处理交易评分计算
- 主线程执行游戏对象操作
- 过滤器链模式选择最佳卖家
- 支持部分交易和递归补单

**支付方式演进:**
```
PRIMITIVE     -> BARTER (物物交换)
AGRICULTURAL  -> +COMMODITY_PAYMENT (商品支付)
HANDICRAFT    -> +METAL_PAYMENT, MIXED_PAYMENT
COMMERCIAL    -> +PAPER_PAYMENT
INDUSTRIAL    -> +ELECTRONIC_PAYMENT
```

#### 2.3.3 设施系统 (Facility System)

```
cn.ykcryobs.vg.villageSystem.facility
├── FacilityManager.java          # 设施管理器
├── VillageFacility.java          # 村庄设施实例
└── types/
    ├── FacilityType.java         # 设施类型抽象基类
    ├── ThatchedHutType.java      # 茅草屋类型
    ├── VillageCenterType.java    # 村庄中心类型
    └── interfaces/
        ├── IFacilityCategory.java
        ├── IResidentialCategory.java
        └── IInfrastructureCategory.java
```

**FacilityManager 数据结构:**
```java
Map<FacilityType, List<VillageFacility>> facilities;  // 类型 -> 设施列表
Map<BlockPos, VillageFacility> positionToFacilityMap;  // 位置 -> 设施
```

**FacilityType Codec 模式:**
使用 Dispatch Codec 支持多态序列化：
```java
Codec<FacilityType> DISPATCH_CODEC = Codec.STRING.partialDispatch(
    "type_identifier",
    FacilityTypeCodec::getIdentifier,
    FacilityTypeCodec::getCodecByType
);
```

### 2.4 村民增强层 (Villager Enhancement Layer)

```
cn.ykcryobs.vg.villagerEnhance
├── IVillagerMixin.java           # Mixin 接口
├── VillagerData.java             # 村民扩展数据
├── behavior/
│   └── WorkAtWarehouse.java      # 仓库工作行为
├── profession/
│   ├── IProfessionEnhancement.java
│   └── WarehouseManagerProfession.java
└── state/
    ├── StateManager.java         # 状态管理器
    ├── VillagerState.java        # 状态接口
    └── VillagerStates.java       # 状态枚举
```

**VillagerData 扩展属性:**
- happiness (幸福度)
- loyalty (忠诚度)
- adaptability (适应度)
- curiosity (好奇心)
- fatigue (疲劳)
- stress (压力)
- itemPreferences (物品偏好)
- supportedPaymentMethods (支持的支付方式)

### 2.5 网络层 (Networking Layer)

```
cn.ykcryobs.vg.networking
├── ServerVillageInfoPayloadHandler.java
├── payload/
│   ├── VillageBaseInfoPayload.java
│   └── VillageInfoRequestPayload.java
└── client/networking/
    └── ClientVillageInfoPayloadHandler.java
```

**网络架构模式:**
- 使用 NeoForge Payload 系统
- 双向通信支持（客户端请求 -> 服务端响应）
- `HandlerThread.NETWORK` 执行模式

---

## 3. 数据流架构

### 3.1 村庄创建流程

```
[世界生成]
    ↓
[ChunkGeneratorMixin] 拦截村庄生成
    ↓
[VillageStructurePoolElement] 自定义结构元素
    ↓
[VillageManager.registerVillage()] 注册村庄
    ↓
[VillageData] 创建村庄数据实例
    ↓
[FacilityManager] 初始化设施管理器
    ↓
[SavedData] 持久化到世界数据
```

### 3.2 交易处理流程

```
[玩家/村民请求购买]
    ↓
[TransactionManager.tryToBuyWithType()]
    ↓
[主线程] 构建交易上下文 (TransactionContext)
    ↓
[异步线程池] 执行卖家评分计算
    ↓
[过滤器链] QuantityScoreFilter → PriceScoreFilter → ...
    ↓
[主线程] 验证交易、协商支付、执行交易
    ↓
[Transaction.execute()] 完成交换
    ↓
[回调] 返回交易结果
```

### 3.3 村庄演进流程

```
[村庄活动] → [获得经验]
    ↓
[VillageData.addExp()] 检查升级
    ↓
[等级提升] → [重新计算演进阶段]
    ↓
[阶段变化] → [发布 VillageNewStageEvent]
    ↓
[事件监听器]
    ├── VillagerData 更新支付方式
    ├── FacilityManager 解锁新设施
    └── 其他系统响应
```

---

## 4. 设计模式应用

### 4.1 单例模式
- `VillageManager.getInstance()` - 全局村庄管理

### 4.2 工厂模式
- `PaymentMethod.fromId()` - 支付方式工厂
- `FacilityType.FacilityTypeCodec` - Codec 工厂

### 4.3 策略模式
- `ISellerFilter` - 卖家过滤策略
- `IPayment` - 支付策略
- `ITradeTaxCalculator` - 税收计算策略

### 4.4 观察者模式
- `VillageNewLevelEvent` / `VillageNewStageEvent` - 村庄事件
- NeoForge EventBus 事件系统

### 4.5 快照模式
- `TraderSnapshot` - 交易者状态快照，用于异步计算

### 4.6 建造者模式
- `TransactionContext.Builder` - 交易上下文构建

---

## 5. 扩展点

### 5.1 设施类型扩展
实现 `FacilityType` 抽象类，注册 Codec：
```java
FacilityTypeCodec.registerCodec("my_facility", MyFacilityType.CODEC);
```

### 5.2 卖家过滤器扩展
实现 `ISellerFilter` 接口，注册到自定义注册表。

### 5.3 支付方式扩展
实现 `IPayment` 接口，添加到 `PaymentMethod` 枚举。

### 5.4 村民行为扩展
- 实现 `VillagerState` 添加新状态
- 实现 `IProfessionEnhancement` 扩展职业

---

## 6. 技术债务与改进建议

### 6.1 已知问题
1. `VillageGenesis.level` 静态变量可能在多世界场景下有问题
2. `VillageManager.commodityMap` 未持久化（有 TODO 注释）
3. `TransactionManager` 回调嵌套可能导致栈溢出（部分交易递归）

### 6.2 架构优化建议
1. 考虑使用 `Capability`/`Attachment` 替代静态变量存储世界引用
2. 将 `commodityMap` 移入 `VillageManager` 持久化逻辑
3. 交易递归改为循环或限制最大递归深度
4. 添加交易事务回滚机制

---

## 7. 依赖关系图

```
VillageGenesis (主入口)
    │
    ├── init/* (注册层)
    │       │
    │       └── villageSystem/* (核心系统)
    │               │
    │               ├── economy/* (经济系统)
    │               │       │
    │               │       └── item/currency/* (货币物品)
    │               │
    │               └── facility/* (设施系统)
    │
    ├── villagerEnhance/* (村民增强)
    │       │
    │       └── mixin/* (Mixin 注入)
    │
    ├── networking/* (网络层)
    │       │
    │       └── client/* (客户端处理)
    │
    ├── commands/* (命令系统)
    │
    └── datagen/* (数据生成)
```

---

**文档版本:** 1.0
**最后更新:** 2026-03-26
