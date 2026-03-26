# 外部集成分析

**项目:** Village Genesis
**分析日期:** 2026-03-26
**分析范围:** 外部系统、API、生态集成

---

## 1. Minecraft 核心集成

### 1.1 原版系统集成

| 系统 | 集成方式 | 关键文件 |
|------|----------|----------|
| 村民系统 | Mixin 注入 | `mixin/VillagerMixin.java`, `mixin/AbstractVillagerMixin.java` |
| 世界生成 | 结构池扩展 | `structures/VillageStructurePoolElement.java` |
| 区块生成 | Mixin 修改 | `mixin/ChunkGeneratorMixin.java` |
| 物品系统 | Mixin + 接口 | `mixin/ItemMixin.java`, `item/ITradableItem.java` |
| 命令系统 | Brigadier | `commands/` 包下所有命令类 |
| NBT 持久化 | WorldSavedData | `villageSystem/VillageData.java` |

### 1.2 原版 API 使用

| API | 用途 | 示例位置 |
|-----|------|----------|
| `ResourceLocation` | 资源定位 | `VillageGenesis.java#L15` |
| `CompoundTag` | 数据序列化 | `VillageData.java`, `VillageEconomyData.java` |
| `BlockPos` | 位置计算 | 多处使用 |
| `ServerLevel` | 世界访问 | `VillageGenesis.java#L16` |
| `ItemStack` | 物品堆栈 | `economy/` 包下大量使用 |

---

## 2. NeoForge 平台集成

### 2.1 事件系统集成

| 事件类型 | 订阅方式 | 处理类 |
|----------|----------|--------|
| Mod 生命周期事件 | `@EventBusSubscriber` | `VillageGenesis.java`, `VillageGenesisClient.java` |
| 服务器启动事件 | `@SubscribeEvent` | `VillageGenesis.java#L117` |
| 通用设置事件 | `@SubscribeEvent` | `VillageGenesis.java#L123` |
| 客户端设置事件 | `@SubscribeEvent` | `VillageGenesisClient.java#L26` |
| 服务器 Tick 事件 | 手动注册 | `event/ServerTickEvents.java` |

### 2.2 注册表系统集成

| 注册类型 | 注册方式 | 文件位置 |
|----------|----------|----------|
| 方块 | DeferredRegister | `init/ModBlocks.java` |
| 物品 | DeferredRegister | `init/ModItems.java` |
| 方块实体 | DeferredRegister | `init/ModBlockEntities.java` |
| 数据组件 | DeferredRegister | `init/ModDataComponents.java` |
| 自定义注册表 | NewRegistryEvent | `init/ModRegistries.java` |
| 数据包注册表 | DataPackRegistryEvent | `init/ModDataPackRegistries.java` |
| 结构池 | 注册监听 | `init/ModStructurePoolRegistries.java` |
| 卖家过滤器 | DeferredRegister | `init/ModSellerFilters.java` |
| 设施类型 | 自定义注册 | `init/FacilityTypeRegistries.java` |

### 2.3 配置系统集成

| 配置类型 | 作用域 | 文件位置 |
|----------|--------|----------|
| CommonConfig | 通用 | `config/CommonConfig.java` |
| ServerConfig | 服务端 | `config/ServerConfig.java` |
| ClientConfig | 客户端 | `config/ClientConfig.java` |

**配置注册:**
```java
// VillageGenesis.java#L50-L52
modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
```

### 2.4 网络系统集成

| 组件 | 类型 | 文件位置 |
|------|------|----------|
| VillageBaseInfoPayload | 自定义数据包 | `networking/payload/VillageBaseInfoPayload.java` |
| VillageInfoRequestPayload | 请求包 | `networking/payload/VillageInfoRequestPayload.java` |
| ServerVillageInfoPayloadHandler | 服务端处理 | `networking/ServerVillageInfoPayloadHandler.java` |
| ClientVillageInfoPayloadHandler | 客户端处理 | `client/networking/ClientVillageInfoPayloadHandler.java` |

### 2.5 附件系统集成

```java
// init/ModAttachment.java
// 用于在实体/方块实体上存储额外数据
```

### 2.6 数据映射集成

```java
// init/ModDataMap.java
// dataMap/ItemDataMap.java
// 用于物品的静态数据映射
```

---

## 3. Mixin 集成详情

### 3.1 Mixin 配置

```json
// src/main/resources/village_genesis.mixins.json
{
  "required": true,
  "package": "cn.ykcryobs.vg.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
    "AbstractVillagerMixin",
    "ChunkGeneratorMixin",
    "ItemMixin",
    "VillagerMixin"
  ],
  "injectors": {
    "defaultRequire": 1
  },
  "overwrites": {
    "requireAnnotations": true
  }
}
```

### 3.2 Mixin 目标分析

| Mixin | 目标类 | 注入类型 | 功能描述 |
|-------|--------|----------|----------|
| `AbstractVillagerMixin` | AbstractVillager | 行为修改 | 村民基础交易行为扩展 |
| `ChunkGeneratorMixin` | ChunkGenerator | 结构注入 | 村庄生成逻辑修改 |
| `ItemMixin` | Item | 接口实现 | 物品可交易性支持 |
| `VillagerMixin` | Villager | 行为扩展 | 村民状态管理、仓库工作行为 |

---

## 4. 世界生成集成

### 4.1 结构池系统集成

| 组件 | 用途 | 文件位置 |
|------|------|----------|
| `VillageStructurePoolElement` | 自定义结构元素 | `structures/VillageStructurePoolElement.java` |
| `LegacyVillageStructurePoolElement` | 旧版兼容 | `structures/LegacyVillageStructurePoolElement.java` |
| `ModStructurePoolRegistries` | 结构池注册 | `init/ModStructurePoolRegistries.java` |

### 4.2 模板池集成

```json
// src/generated/resources/data/minecraft/worldgen/template_pool/village/plains/town_centers.json
// 覆盖原版平原村庄中心模板池
```

---

## 5. 数据生成集成

### 5.1 NeoForge DataGen API

| Provider 基类 | 实现类 | 输出内容 |
|---------------|--------|----------|
| BlockLootSubProvider | ModBlockLootTablesProvider | 方块战利品表 |
| BlockStateProvider | ModBlockStatesProvider | 方块状态模型 |
| BlockTagsProvider | ModBlockTagsProvider | 方块标签 |
| ItemTagsProvider | ModItemTagsProvider | 物品标签 |
| ItemModelProvider | ModItemModelsProvider | 物品模型 |
| RecipeProvider | ModRecipeProvider | 合成配方 |
| LanguageProvider | ModEnUsLangProvider, ModZhCnLangProvider | 语言文件 |
| DataMapProvider | ModDataMapProvider | 数据映射 |

### 5.2 自定义数据生成

| Provider | 输出类型 |
|----------|----------|
| FacilityDataProvider | 设施配置数据 |
| FacilityLevelDataProvider | 设施等级数据 |

---

## 6. 命令系统集成

### 6.1 Brigadier 命令框架

| 命令节点 | 注册位置 |
|----------|----------|
| `/village` | `VillageCommands.java` |
| `/villager` | `VillagerCommands.java` |
| `/economy` | `EconomyCommands.java` |

### 6.2 命令注册

```java
// init/CommandInit.java
// 在 RegisterCommandsEvent 中注册所有命令
```

---

## 7. 外部模组兼容性

### 7.1 当前状态

**无外部模组依赖**

```groovy
// build.gradle#L182-L203
dependencies {
  // 当前无外部模组依赖
  // 预留了 JEI 集成的注释示例
}
```

### 7.2 预留集成点

| 模组 | 集成状态 | 注释位置 |
|------|----------|----------|
| JEI (Just Enough Items) | 预留接口 | `build.gradle#L185-L188` |

---

## 8. CI/CD 集成

### 8.1 GitHub Actions

| Workflow | 触发条件 | 文件位置 |
|----------|----------|----------|
| build.yml | Push/PR | `.github/workflows/build.yml` |
| issue-closed.yml | Issue 关闭 | `.github/workflows/issue-closed.yml` |
| issue-reopened.yml | Issue 重开 | `.github/workflows/issue-reopened.yml` |
| issue_inactive.yml | Issue 不活跃 | `.github/workflows/issue_inactive.yml` |
| label-sync.yml | 标签同步 | `.github/workflows/label-sync.yml` |

### 8.2 版本信息生成

```groovy
// build.gradle#L139-L172
// 自动生成 version.properties
// 包含: full_version, major, minor, patch, pr_number, branch_name, is_snapshot
```

---

## 9. 资源包集成

### 9.1 资源结构

```
src/main/resources/
├── village_genesis.mixins.json    # Mixin 配置
└── (通过模板生成)
    └── META-INF/
        └── neoforge.mods.toml     # 模组元数据

src/generated/resources/
├── assets/
│   └── village_genesis/
│       └── lang/                  # 语言文件
└── data/
    ├── minecraft/                 # 覆盖原版数据
    │   └── worldgen/template_pool/
    └── village_genesis/           # 模组数据
        ├── data_maps/
        ├── loot_table/
        └── village_genesis/
```

---

## 10. 本地化集成

### 10.1 支持的语言

| 语言 | 文件位置 |
|------|----------|
| English (en_us) | `ModEnUsLangProvider.java` |
| 简体中文 (zh_cn) | `ModZhCnLangProvider.java` |

---

## 11. 集成架构图

```
┌────────────────────────────────────────────────────────────┐
│                     Village Genesis                         │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │   Mixin     │    │   Events    │    │  Registry   │    │
│  │  System     │    │   System    │    │   System    │    │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘    │
│         │                  │                   │           │
│         └──────────────────┼───────────────────┘           │
│                            │                               │
│                            ▼                               │
│  ┌─────────────────────────────────────────────────────┐  │
│  │                    NeoForge API                      │  │
│  └─────────────────────────────────────────────────────┘  │
│                            │                               │
│                            ▼                               │
│  ┌─────────────────────────────────────────────────────┐  │
│  │                   Minecraft API                      │  │
│  │  • Brigadier Commands                                │  │
│  │  • NBT Serialization                                 │  │
│  │  • World Generation                                  │  │
│  │  • Entity System                                     │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
├────────────────────────────────────────────────────────────┤
│  External Integrations                                      │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │   GitHub    │    │   Gradle    │    │   Parchment │    │
│  │   Actions   │    │   Plugins   │    │   Mappings  │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    │
└────────────────────────────────────────────────────────────┘
```

---

## 12. 集成风险与建议

### 12.1 当前风险

| 风险 | 级别 | 说明 |
|------|------|------|
| Mixin 兼容性 | 中 | 多个 Mixin 可能与其他模组冲突 |
| 原版数据覆盖 | 中 | 覆盖 town_centers.json 可能影响其他模组 |
| 无外部依赖 | 低 | 当前无第三方模组依赖，但需注意未来扩展 |

### 12.2 建议

1. **Mixin 优先级**: 考虑添加 Mixin 配置优先级处理
2. **兼容性测试**: 建议添加与主流模组的兼容性测试
3. **API 设计**: 考虑为其他模组提供公开 API 接口

---

**文档生成:** GSD Codebase Mapper
**最后更新:** 2026-03-26
