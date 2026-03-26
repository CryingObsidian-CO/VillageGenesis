# VillageGenesis 代码结构分析

**分析日期:** 2026-03-26
**分析焦点:** arch (架构)
**项目路径:** `G:\mcModProject\VillageGenesis`
**源码路径:** `src/main/java/cn/ykcryobs/vg`

---

## 1. 目录结构总览

```
G:\mcModProject\VillageGenesis\
├── build.gradle                    # Gradle 构建配置
├── gradle.properties               # Gradle 属性配置
├── settings.gradle                 # Gradle 设置
├── src/
│   ├── main/
│   │   ├── java/cn/ykcryobs/vg/    # 主源码目录
│   │   ├── resources/              # 资源文件
│   │   └── templates/              # 模板文件 (neoforge.mods.toml)
│   └── generated/                  # 数据生成输出目录
└── .planning/                      # 规划文档目录
```

---

## 2. Java 包结构

### 2.1 根包 (`cn.ykcryobs.vg`)

| 文件 | 行数 | 说明 |
|------|------|------|
| `VillageGenesis.java` | 125 | 主入口类，@Mod 注解 |
| `VillageGenesisClient.java` | 29 | 客户端入口类 |
| `ModDataGenerator.java` | - | 数据生成入口 |

### 2.2 初始化包 (`cn.ykcryobs.vg.init`)

**文件清单 (14 个文件):**

| 文件 | 说明 | 注册类型 |
|------|------|----------|
| `ModItems.java` | 物品注册 | DeferredRegister.Items |
| `ModBlocks.java` | 方块注册 | DeferredRegister.Blocks |
| `ModBlockEntities.java` | 方块实体注册 | DeferredRegister |
| `ModDataComponents.java` | 数据组件注册 | DeferredRegister |
| `ModAttachment.java` | Attachment 注册 | DeferredRegister |
| `ModRegistries.java` | 自定义注册表 | RegistryBuilder |
| `ModDataPackRegistries.java` | 数据包注册表 | DataPackRegistryEvent |
| `ModPayload.java` | 网络载荷注册 | RegisterPayloadHandlersEvent |
| `ModDataSave.java` | SavedData 注册 | ServerStartingEvent |
| `ModDataMap.java` | DataMap 注册 | - |
| `ModSellerFilters.java` | 卖家过滤器注册 | - |
| `ModStructurePoolRegistries.java` | 结构池注册 | DeferredRegister |
| `FacilityTypeRegistries.java` | 设施类型注册 | - |
| `CommandInit.java` | 命令注册 | RegisterCommandsEvent |

### 2.3 村庄系统包 (`cn.ykcryobs.vg.villageSystem`)

**核心文件:**

| 文件 | 行数 | 说明 |
|------|------|------|
| `VillageManager.java` | 215 | 村庄管理器 (SavedData) |
| `VillageData.java` | 672 | 村庄数据核心类 |
| `VillageNameGenerator.java` | - | 村庄名称生成器 |

**子包结构:**

#### 2.3.1 经济子系统 (`economy/`)

```
economy/
├── VillageEconomyData.java           # 村庄经济数据
├── market/
│   ├── MarketType.java               # 市场类型枚举
│   └── ResourceType.java             # 资源类型枚举
├── payment/
│   ├── IPayment.java                 # 支付接口
│   ├── PaymentMethod.java            # 支付方式枚举
│   └── BarterPayment.java            # 物物交换实现
├── tax/tradeTax/
│   ├── ITradeTaxCalculator.java      # 税收计算接口
│   └── TradeTaxCalculator.java       # 税收计算实现
├── trader/
│   ├── ITrader.java                  # 交易者接口
│   ├── TraderSnapshot.java           # 交易者快照
│   └── sellerFilter/
│       ├── ISellerFilter.java        # 卖家过滤器接口
│       ├── QuantityScoreFilter.java  # 数量评分过滤器
│       └── PriceScoreFilter.java     # 价格评分过滤器
├── transaction/
│   ├── Transaction.java              # 交易执行类
│   ├── TransactionContext.java       # 交易上下文
│   ├── TransactionManager.java       # 交易管理器
│   └── TransactionResult.java        # 交易结果枚举
└── tradeRoute/
    ├── TradeRoute.java               # 贸易路线
    ├── TradeRouteManager.java        # 贸易路线管理器
    ├── TradeType.java                # 贸易类型枚举
    ├── TradeRelationType.java        # 贸易关系类型
    ├── RouteType.java                # 路线类型枚举
    └── VillageTradeRelation.java     # 村庄贸易关系
```

#### 2.3.2 设施子系统 (`facility/`)

```
facility/
├── FacilityManager.java              # 设施管理器
├── VillageFacility.java              # 村庄设施实例
└── types/
    ├── FacilityType.java             # 设施类型抽象基类
    ├── ThatchedHutType.java          # 茅草屋类型
    ├── VillageCenterType.java        # 村庄中心类型
    └── interfaces/
        ├── IFacilityCategory.java    # 设施分类接口
        ├── IResidentialCategory.java # 居住分类接口
        └── IInfrastructureCategory.java # 基础设施分类接口
```

### 2.4 村民增强包 (`cn.ykcryobs.vg.villagerEnhance`)

| 文件 | 说明 |
|------|------|
| `IVillagerMixin.java` | Mixin 接口定义 |
| `VillagerData.java` | 村民扩展数据 (301 行) |

**子包:**

```
villagerEnhance/
├── behavior/
│   └── WorkAtWarehouse.java          # 仓库工作行为
├── profession/
│   ├── IProfessionEnhancement.java   # 职业增强接口
│   └── WarehouseManagerProfession.java # 仓库管理员职业
└── state/
    ├── StateManager.java             # 状态管理器
    ├── VillagerState.java            # 状态接口
    └── VillagerStates.java           # 状态枚举
```

### 2.5 物品包 (`cn.ykcryobs.vg.item`)

| 文件 | 说明 |
|------|------|
| `ITradableItem.java` | 可交易物品接口 |
| `BoundaryScepterItem.java` | 边界权杖物品 |
| `currency/` | 货币子包 |

**货币子包 (`currency/`):**

| 文件 | 说明 |
|------|------|
| `BaseCurrencyItem.java` | 基础货币物品 |
| `BasePaperCurrencyItem.java` | 基础纸币物品 |
| `CurrencyType.java` | 货币类型枚举 |
| `CopperCoinItem.java` | 铜币 |
| `SilverCoinItem.java` | 银币 |
| `GoldCoinItem.java` | 金币 |
| `OneYuanPaperItem.java` | 一元纸币 |
| `FiveYuanPaperItem.java` | 五元纸币 |
| `TenYuanPaperItem.java` | 十元纸币 |
| `FiftyYuanPaperItem.java` | 五十元纸币 |
| `OneHundredYuanPaperItem.java` | 一百元纸币 |

### 2.6 方块包 (`cn.ykcryobs.vg.block`)

| 文件 | 说明 |
|------|------|
| `VillageInfoPanelBlock.java` | 村庄信息面板方块 |
| `entity/VillageInfoPanelBlockEntity.java` | 方块实体 |
| `item/VillageInfoPanelBlockItem.java` | 方块物品 |

### 2.7 网络包 (`cn.ykcryobs.vg.networking`)

| 文件 | 说明 |
|------|------|
| `ServerVillageInfoPayloadHandler.java` | 服务端载荷处理器 |
| `payload/VillageBaseInfoPayload.java` | 村庄基础信息载荷 |
| `payload/VillageInfoRequestPayload.java` | 村庄信息请求载荷 |
| `client/networking/ClientVillageInfoPayloadHandler.java` | 客户端载荷处理器 |

### 2.8 命令包 (`cn.ykcryobs.vg.commands`)

| 文件 | 说明 |
|------|------|
| `CommandUtils.java` | 命令工具类 |
| `VillagerCommands.java` | 村民命令 |
| `VillageCommands.java` | 村庄命令 |
| `EconomyCommands.java` | 经济命令 |
| `economy/TransactionCommands.java` | 交易命令 |
| `village/EvolutionStageCommands.java` | 演进阶段命令 |
| `villager/InventoryCommands.java` | 背包命令 |

### 2.9 配置包 (`cn.ykcryobs.vg.config`)

| 文件 | 说明 |
|------|------|
| `CommonConfig.java` | 通用配置 |
| `ServerConfig.java` | 服务端配置 |
| `ClientConfig.java` | 客户端配置 |
| `ConfigEventListeners.java` | 配置事件监听器 |
| `utils/ConfigUtils.java` | 配置工具类 |

### 2.10 数据生成包 (`cn.ykcryobs.vg.datagen`)

| 文件 | 说明 |
|------|------|
| `ModDataMapProvider.java` | DataMap 提供器 |
| `ModEnUsLangProvider.java` | 英语语言提供器 |
| `ModZhCnLangProvider.java` | 中文语言提供器 |
| `ModBlockStatesProvider.java` | 方块状态提供器 |
| `ModBlockLootTablesProvider.java` | 战利品表提供器 |
| `ModItemTagsProvider.java` | 物品标签提供器 |
| `ModBlockTagsProvider.java` | 方块标签提供器 |
| `ModItemModelsProvider.java` | 物品模型提供器 |
| `ModRecipeProvider.java` | 配方提供器 |
| `ModWordGenProvider.java` | 世界生成提供器 |
| `ModFacilityDataProvider.java` | 设施数据提供器 |
| `ModFacilityLevelDataProvider.java` | 设施等级数据提供器 |
| `provider/FacilityDataProvider.java` | 设施数据提供器基类 |
| `provider/FacilityLevelDataProvider.java` | 设施等级数据提供器基类 |

### 2.11 事件包 (`cn.ykcryobs.vg.event`)

| 文件 | 说明 |
|------|------|
| `ServerTickEvents.java` | 服务端 Tick 事件 |
| `VillageEvent.java` | 村庄事件基类 |
| `VillageNewLevelEvent.java` | 村庄升级事件 |
| `VillageNewStageEvent.java` | 村庄阶段变化事件 |

### 2.12 Mixin 包 (`cn.ykcryobs.vg.mixin`)

| 文件 | 说明 |
|------|------|
| `VillagerMixin.java` | 村民 Mixin |
| `AbstractVillagerMixin.java` | 抽象村民 Mixin |
| `ItemMixin.java` | 物品 Mixin |
| `ChunkGeneratorMixin.java` | 区块生成器 Mixin |

### 2.13 其他包

| 包名 | 说明 |
|------|------|
| `utils/` | 工具类 (BoundingBox2D, MapUtils) |
| `dataComponents/` | 数据组件 (BoundaryScepterComponent, CurrencyDataComponent) |
| `dataMap/` | 数据映射 (ItemDataMap) |
| `structures/` | 结构相关 (VillageStructurePoolElement, LegacyVillageStructurePoolElement) |
| `client/` | 客户端代码 |
| `client/event/` | 客户端事件 (ClientParticleEvents) |
| `client/screen/` | 客户端屏幕 (VillageInfoPanelScreen) |

---

## 3. 文件统计

### 3.1 按包统计

| 包名 | 文件数 | 说明 |
|------|--------|------|
| `init` | 14 | 注册初始化 |
| `villageSystem` | 3 | 村庄核心 |
| `villageSystem.economy` | 17 | 经济系统 |
| `villageSystem.facility` | 7 | 设施系统 |
| `villagerEnhance` | 9 | 村民增强 |
| `item` | 13 | 物品定义 |
| `block` | 3 | 方块定义 |
| `networking` | 4 | 网络通信 |
| `commands` | 7 | 命令系统 |
| `config` | 5 | 配置系统 |
| `datagen` | 14 | 数据生成 |
| `event` | 4 | 事件处理 |
| `mixin` | 4 | Mixin 注入 |
| `utils` | 2 | 工具类 |
| `dataComponents` | 2 | 数据组件 |
| `dataMap` | 1 | 数据映射 |
| `structures` | 2 | 结构定义 |
| `client` | 4 | 客户端代码 |
| **总计** | **~115** | - |

### 3.2 核心文件行数

| 文件 | 行数 | 职责 |
|------|------|------|
| `VillageData.java` | 672 | 村庄数据核心 |
| `TransactionManager.java` | 364 | 交易管理 |
| `VillagerData.java` | 301 | 村民数据 |
| `VillageManager.java` | 215 | 村庄管理器 |
| `FacilityManager.java` | 362 | 设施管理器 |
| `FacilityType.java` | 192 | 设施类型基类 |

---

## 4. 资源文件结构

```
src/main/resources/
├── META-INF/
│   └── neoforge.mods.toml       # 模组元数据 (由模板生成)
├── assets/village_genesis/
│   ├── lang/
│   │   ├── en_us.json           # 英语翻译
│   │   └── zh_cn.json           # 中文翻译
│   ├── models/
│   │   ├── item/                # 物品模型
│   │   └── block/               # 方块模型
│   ├── blockstates/             # 方块状态
│   ├── textures/                # 纹理文件
│   └── sounds/                  # 音效文件
└── data/village_genesis/
    ├── recipes/                 # 配方数据
    ├── loot_tables/             # 战利品表
    ├── tags/                    # 标签数据
    └── village/                 # 村庄数据
        └── facilities/          # 设施数据包
```

---

## 5. 构建配置

### 5.1 Gradle 配置要点

**build.gradle 关键配置:**

```groovy
// NeoForge 版本
neoForge {
  version = project.neo_version
  
  parchment {
    mappingsVersion = project.parchment_mappings_version
    minecraftVersion = project.parchment_minecraft_version
  }
}

// Java 版本
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

// 运行配置
runs {
  client { client() }
  server { server() }
  data { data() }
  gameTestServer { type = "gameTestServer" }
}
```

### 5.2 版本管理

项目使用动态版本号：
```
{major}.{minor}.{patch}[-pr.{pr_number}][-dev.{branch}][-SNAPSHOT]+{mc_version}
```

示例: `1.0.0-pr.42-dev.feature-economy+1.21.1`

---

## 6. 关键依赖关系

### 6.1 初始化顺序

```
VillageGenesis 构造函数
    │
    ├── modContainer.registerConfig() (配置注册)
    │
    ├── ModAttachment.register()
    ├── ModBlockEntities.register()
    ├── ModBlocks.register()
    ├── ModDataComponents.register()
    ├── ModItems.register()
    ├── ModStructurePoolRegistries.register()
    │
    └── VillageData.register() (事件总线引用)
```

### 6.2 服务启动顺序

```
ServerStartingEvent
    │
    ├── VillageGenesis.serverStarting()
    │   ├── level = server.overworld()
    │   └── TransactionManager.setup()
    │
    └── ModDataSave.register()
        └── overworld.getDataStorage().computeIfAbsent(VillageManager)
```

---

## 7. 代码规范

### 7.1 命名约定

| 类型 | 命名风格 | 示例 |
|------|----------|------|
| 类名 | PascalCase | `VillageManager` |
| 方法名 | camelCase | `getVillageData()` |
| 常量 | UPPER_SNAKE | `MOD_ID`, `FACILITY_REGISTRY_KEY` |
| 包名 | 小写 | `cn.ykcryobs.vg.villageSystem` |

### 7.2 注释规范

- 类级别使用 JavaDoc
- 复杂逻辑使用行内注释
- TODO 标记待完成功能
- NOTE 标记重要说明

---

**文档版本:** 1.0
**最后更新:** 2026-03-26
