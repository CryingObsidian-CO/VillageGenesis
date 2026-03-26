# 技术栈分析

**项目:** Village Genesis
**分析日期:** 2026-03-26
**分析范围:** 技术栈与依赖

---

## 1. 核心技术栈

### 1.1 编程语言

| 技术 | 版本 | 用途 | 配置位置 |
|------|------|------|----------|
| Java | 21 | 主要开发语言 | `build.gradle#L31` |

### 1.2 构建工具

| 技术 | 版本 | 用途 | 配置位置 |
|------|------|------|----------|
| Gradle | 8.x (via wrapper) | 构建系统 | `gradle/wrapper/gradle-wrapper.properties` |
| ModDevGradle | 2.0.120 | NeoForge 模组开发插件 | `build.gradle#L6` |

---

## 2. Minecraft/NeoForge 生态

### 2.1 核心平台

| 组件 | 版本 | 说明 | 配置位置 |
|------|------|------|----------|
| Minecraft | 1.21.1 | 目标游戏版本 | `gradle.properties#L14` |
| NeoForge | 21.1.215 | 模组加载器 | `gradle.properties#L20` |
| Parchment | 2024.11.17 | 参数名映射 | `gradle.properties#L10` |

### 2.2 NeoForge 子系统使用

| 子系统 | 使用情况 | 关键文件 |
|--------|----------|----------|
| Event Bus | 重度使用 | `VillageGenesis.java`, `VillageGenesisClient.java` |
| Registry System | 重度使用 | `ModBlocks.java`, `ModItems.java`, `ModRegistries.java` |
| Configuration | 完整实现 | `CommonConfig.java`, `ServerConfig.java`, `ClientConfig.java` |
| Networking | 已实现 | `ModPayload.java`, payload 包下所有类 |
| Data Generation | 完整实现 | `datagen/` 包下所有 Provider |
| Mixin | 4个 Mixin 类 | `village_genesis.mixins.json` |
| Attachment System | 已使用 | `ModAttachment.java` |
| Data Pack Registry | 已使用 | `ModDataPackRegistries.java` |
| Data Maps | 已使用 | `ModDataMap.java`, `ItemDataMap.java` |

---

## 3. 数据序列化与存储

### 3.1 NBT 系统

| 用途 | 关键类 |
|------|--------|
| 村庄数据持久化 | `VillageData.java`, `VillageManager.java` |
| 经济系统数据 | `VillageEconomyData.java`, `TransactionManager.java` |
| 设施数据 | `FacilityManager.java`, `VillageFacility.java` |
| 贸易路线 | `TradeRouteManager.java`, `TradeRoute.java` |

### 3.2 世界生成结构

| 类型 | 关键文件 |
|------|----------|
| 自定义结构池元素 | `VillageStructurePoolElement.java` |
| 模板池注册 | `ModStructurePoolRegistries.java` |
| Jigsaw 结构集成 | `ChunkGeneratorMixin.java` |

---

## 4. 日志系统

| 组件 | 版本 | 用途 |
|------|------|------|
| SLF4J | (via NeoForge) | 日志门面 |
| LogUtils | Mojang 提供 | 日志工具类 |

**使用示例:**
```java
// VillageGenesis.java#L40
private static final Logger LOGGER = LogUtils.getLogger();
```

---

## 5. 数据生成工具链

### 5.1 已实现的 Provider

| Provider | 输出类型 | 文件位置 |
|----------|----------|----------|
| ModBlockLootTablesProvider | 战利品表 | `src/generated/resources/data/` |
| ModBlockStatesProvider | 方块状态 | `src/generated/resources/assets/` |
| ModBlockTagsProvider | 方块标签 | `src/generated/resources/data/tags/` |
| ModItemModelsProvider | 物品模型 | `src/generated/resources/assets/` |
| ModItemTagsProvider | 物品标签 | `src/generated/resources/data/tags/` |
| ModRecipeProvider | 配方 | `src/generated/resources/data/recipe/` |
| ModDataMapProvider | 数据映射 | `src/generated/resources/data/data_maps/` |
| ModEnUsLangProvider | 英文语言 | `src/generated/resources/assets/lang/` |
| ModZhCnLangProvider | 中文语言 | `src/generated/resources/assets/lang/` |
| ModFacilityDataProvider | 设施数据 | 自定义数据生成 |
| ModFacilityLevelDataProvider | 设施等级数据 | 自定义数据生成 |
| ModWordGenProvider | 世界生成 | `src/generated/resources/data/worldgen/` |

---

## 6. 版本控制系统

| 组件 | 配置 |
|------|------|
| Git | `.gitignore`, `.gitattributes` |
| GitHub Actions | `.github/workflows/` |
| CI/CD | `build.yml` |

### 6.1 版本号策略

```groovy
// build.gradle#L38-L60
version = version_major + "." + version_minor + "." + version_patch
// 支持 PR 预发布版本
// 支持分支开发版本
// 支持 SNAPSHOT 版本
// 自动附加游戏版本后缀
```

---

## 7. 客户端技术

### 7.1 客户端专属功能

| 功能 | 文件位置 |
|------|----------|
| 配置界面 | `VillageGenesisClient.java#L22` |
| 屏幕界面 | `screen/VillageInfoPanelScreen.java` |
| 粒子效果 | `client/event/ClientParticleEvents.java` |
| 网络处理 | `client/networking/ClientVillageInfoPayloadHandler.java` |

---

## 8. Mixin 技术

### 8.1 已实现的 Mixin

| Mixin 类 | 目标类 | 用途 |
|----------|--------|------|
| `AbstractVillagerMixin.java` | AbstractVillager | 村民交易增强 |
| `ChunkGeneratorMixin.java` | ChunkGenerator | 区块生成修改 |
| `ItemMixin.java` | Item | 物品行为扩展 |
| `VillagerMixin.java` | Villager | 村民行为增强 |

**配置文件:** `src/main/resources/village_genesis.mixins.json`

---

## 9. 命令系统

| 命令类别 | 实现文件 |
|----------|----------|
| 村庄命令 | `commands/VillageCommands.java` |
| 村民命令 | `commands/VillagerCommands.java` |
| 经济命令 | `commands/EconomyCommands.java` |
| 交易命令 | `commands/economy/TransactionCommands.java` |
| 进化阶段命令 | `commands/village/EvolutionStageCommands.java` |
| 库存命令 | `commands/villager/InventoryCommands.java` |

---

## 10. 依赖管理

### 10.1 当前依赖

```groovy
// build.gradle#L182-L203
// 无外部模组依赖
// 预留 JEI 集成注释示例
```

### 10.2 仓库配置

```groovy
// build.gradle#L22-L24
repositories {
  // 使用默认仓库
}
```

---

## 11. 开发工具配置

| 工具 | 配置 | 文件位置 |
|------|------|----------|
| IDEA | 下载源码/Javadoc | `build.gradle#L248-L253` |
| EditorConfig | 编码规范 | `.editorconfig` |
| UTF-8 编码 | 编译时强制 | `build.gradle#L244` |

---

## 12. 技术栈总结

```
┌─────────────────────────────────────────────────────┐
│                    Village Genesis                   │
├─────────────────────────────────────────────────────┤
│  Java 21 + Gradle 8.x + ModDevGradle 2.0.120        │
├─────────────────────────────────────────────────────┤
│  Minecraft 1.21.1 + NeoForge 21.1.215               │
│  + Parchment Mappings                               │
├─────────────────────────────────────────────────────┤
│  NeoForge Subsystems:                               │
│  • Event Bus • Registry • Config • Networking       │
│  • DataGen • Mixin • Attachment • DataMaps          │
├─────────────────────────────────────────────────────┤
│  Custom Systems:                                    │
│  • Village System • Economy System                  │
│  • Facility System • Trade Route System             │
├─────────────────────────────────────────────────────┤
│  CI/CD: GitHub Actions                              │
└─────────────────────────────────────────────────────┘
```

---

**文档生成:** GSD Codebase Mapper
**最后更新:** 2026-03-26
