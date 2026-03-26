# 编码约定分析

**项目:** VillageGenesis (Minecraft 1.21.1 NeoForge Mod)
**分析日期:** 2026-03-26
**分析范围:** 代码风格、命名规范、注解使用、错误处理、日志模式

---

## 1. 代码格式化配置

### 1.1 EditorConfig 设置

项目使用 `.editorconfig` 进行代码格式化配置，主要设置如下：

| 配置项 | 值 |
|--------|-----|
| 字符编码 | UTF-8 |
| 换行符 | CRLF (Windows) |
| 缩进风格 | Space |
| 最大行长度 | 110 字符 |

**Java 专属配置:**
- 缩进大小: 4 空格
- Tab 宽度: 4
- 延续缩进: 8 空格
- 大括号风格: `end_of_line` (K&R 风格)
- 测试类后缀: `Test`

**文件路径:** `G:\mcModProject\VillageGenesis\.editorconfig`

---

## 2. 命名约定

### 2.1 包命名

- **基础包名:** `cn.ykcryobs.vg`
- **命名风格:** 全小写，使用点分隔
- **模块化组织:** 按功能模块划分子包

**包结构示例:**
```
cn.ykcryobs.vg
├── block/              # 方块相关
├── client/             # 客户端代码
├── commands/           # 命令系统
├── config/             # 配置文件
├── dataComponents/     # 数据组件
├── datagen/            # 数据生成
├── event/              # 事件处理
├── init/               # 注册初始化
├── item/               # 物品相关
├── mixin/              # Mixin 类
├── networking/         # 网络通信
├── structures/         # 结构生成
├── utils/              # 工具类
├── villagerEnhance/    # 村民增强
└── villageSystem/      # 村庄系统
```

### 2.2 类命名

| 类型 | 命名风格 | 示例 |
|------|----------|------|
| 主类 | PascalCase | `VillageGenesis` |
| 注册类 | Mod + 功能 + s | `ModBlocks`, `ModItems` |
| 物品类 | 功能 + Item | `BaseCurrencyItem`, `CopperCoinItem` |
| 方块类 | 功能 + Block | `VillageInfoPanelBlock` |
| 管理类 | 功能 + Manager | `VillageManager` |
| 数据类 | 功能 + Data | `VillageData` |
| 配置类 | 作用域 + Config | `CommonConfig`, `ServerConfig` |
| 命令类 | 功能 + Commands | `VillageCommands`, `EconomyCommands` |
| 工具类 | 功能 + Utils | `CommandUtils`, `ConfigUtils` |
| 枚举 | PascalCase | `CurrencyType`, `NameGenerationMode` |

### 2.3 方法命名

- **风格:** camelCase (驼峰命名)
- **布尔返回方法:** 使用 `is`, `has`, `can`, `should` 前缀
  - `isVillageExist()`, `isPosInVillage()`, `isTradable()`
- **获取方法:** 使用 `get` 前缀
  - `getVillageData()`, `getCurrencyType()`, `getGameTime()`
- **设置方法:** 使用 `set` 前缀
  - `setCurrencyData()`, `setIssuingInfo()`
- **注册方法:** 使用 `register` 前缀
  - `registerVillage()`, `registerBlock()`

### 2.4 字段命名

- **静态常量:** UPPER_SNAKE_CASE
  - `MOD_ID`, `LOGGER`, `VILLAGE_INFO_PANEL`
- **实例字段:** camelCase
  - `currencyType`, `minX`, `maxX`
- **静态字段:** camelCase (非 final 时)
  - `villageMap`, `commodityMap`

### 2.5 常量命名

```java
// 主类示例
public static final String MOD_ID = "village_genesis";
private static final Logger LOGGER = LogUtils.getLogger();
private static final VillageManager INSTANCE = new VillageManager();

// 注册类示例
public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VillageGenesis.MOD_ID);
public static final Supplier<Item> COPPER_COIN = ITEMS.register("copper_coin", CopperCoinItem::new);
```

---

## 3. 注解使用模式

### 3.1 NeoForge 注解

```java
// 主类注解
@Mod(VillageGenesis.MOD_ID)
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class VillageGenesis { ... }

// 事件订阅
@SubscribeEvent
private static void serverStarting(ServerStartingEvent event) { ... }

@SubscribeEvent
private static void onCommonSetup(FMLCommonSetupEvent event) { ... }
```

### 3.2 JetBrains 注解

```java
// 空值注解
@Override
public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) { ... }

@Nullable
public static TagKey<Biome> getBiome() { ... }
```

### 3.3 JavaDoc 注释

**标准格式:**
```java
/**
 * 类/方法简短描述
 *
 * @author llykff
 * @param paramName 参数描述
 * @return 返回值描述
 * @throws ExceptionType 异常描述
 */
```

**示例:**
```java
/**
 * 村庄管理器
 *
 * @author llykff
 */
public class VillageManager extends SavedData { ... }

/**
 * 获取一个村庄的数据类
 *
 * @param id 村庄ID
 * @return 村庄数据类
 */
public static Optional<VillageData> getVillageData(UUID id) { ... }
```

---

## 4. 错误处理模式

### 4.1 异常处理

**使用 IllegalStateException:**
```java
public static ServerLevel getLevel() {
    if (level == null) {
        throw new IllegalStateException("Level is not set!");
    }
    return level;
}
```

**使用 Optional 处理可能为空的返回值:**
```java
public static Optional<VillageData> getVillageData(UUID id) {
    if (!isVillageExist(id)) {
        return Optional.empty();
    }
    return Optional.of(VillageManager.villageMap.get(id));
}
```

### 4.2 参数验证

```java
public static boolean isVillageExist(UUID id) {
    if (id == null) {
        LOGGER.warn("VillageManager: 尝试获取空ID的村庄数据类");
        return false;
    }
    // ...
}
```

---

## 5. 日志模式

### 5.1 Logger 初始化

```java
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

private static final Logger LOGGER = LogUtils.getLogger();
```

### 5.2 日志级别使用

| 级别 | 用途 | 示例 |
|------|------|------|
| INFO | 重要操作完成 | `LOGGER.info("Loaded {} villages from world data", villages.size());` |
| WARN | 可恢复的异常情况 | `LOGGER.warn("VillageManager: 未找到村庄数据类，ID：{}", id);` |
| ERROR | 严重错误 | `LOGGER.error("VillageManager: try to add an untradable item {} to commodity map", item);` |
| DEBUG | 调试信息 | 通过 `build.gradle` 配置 `logLevel = org.slf4j.event.Level.DEBUG` |

### 5.3 日志消息格式

- 使用 `{}` 占位符而非字符串拼接
- 消息前缀标识来源模块
- 支持中英文混合（项目特色）

```java
LOGGER.info("Saved {} villages to world data", villageMap.size());
LOGGER.warn("BoundingBox2D: maxX < minX || maxZ < minZ, minX: {}, minZ: {}, maxX: {}, maxZ: {}", ...);
```

---

## 6. 注册模式

### 6.1 DeferredRegister 模式

**方块注册:**
```java
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VillageGenesis.MOD_ID);
    
    public static final Supplier<VillageInfoPanelBlock> VILLAGE_INFO_PANEL = 
        BLOCKS.register("village_info_panel", () -> new VillageInfoPanelBlock());
    
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
```

**物品注册:**
```java
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VillageGenesis.MOD_ID);
    
    public static final Supplier<Item> COPPER_COIN = 
        ITEMS.register("copper_coin", CopperCoinItem::new);
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
```

### 6.2 主类初始化顺序

```java
public VillageGenesis(IEventBus modEventBus, ModContainer modContainer) {
    // 1. 配置注册
    modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    
    // 2. 模组组件注册
    ModAttachment.register(modEventBus);
    ModBlockEntities.register(modEventBus);
    ModBlocks.register(modEventBus);
    ModDataComponents.register(modEventBus);
    ModItems.register(modEventBus);
    ModStructurePoolRegistries.register(modEventBus);
    
    // 3. 自定义数据注册
    VillageData.register(modEventBus);
}
```

---

## 7. 配置模式

### 7.1 ModConfigSpec 使用

```java
public class CommonConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villagePrefixes;
    public static final ModConfigSpec.EnumValue<NameGenerationMode> villageNameGenerationMode;
    
    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ConfigUtils.defineCategory(builder, "villageNameGeneration");
        
        villageNameGenerationMode = ConfigUtils.defineEnum(builder, "villageNameGenerationMode",
                NameGenerationMode.class, NameGenerationMode.PREFIX_CORE_SUFFIX);
        
        // ...
        SPEC = builder.build();
    }
}
```

---

## 8. 继承与接口模式

### 8.1 类继承

```java
// 货币物品继承体系
public class BaseCurrencyItem extends Item { ... }
public class CopperCoinItem extends BaseCurrencyItem { ... }
public class BasePaperCurrencyItem extends BaseCurrencyItem { ... }

// SavedData 继承
public class VillageManager extends SavedData { ... }
```

### 8.2 接口实现

```java
// 功能接口
public interface ITradableItem { ... }
public interface ITrader { ... }
```

---

## 9. 资源定位符模式

### 9.1 统一资源路径生成

```java
public static ResourceLocation getIdentifier(String path) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
}
```

**注意:** 存在 TODO 标记，计划将现有代码中的 `ResourceLocation.fromNamespaceAndPath` 替换为此方法。

---

## 10. TODO 注释规范

项目使用 TODO 注释标记待完成功能:

```java
// TODO 把现有的 ResourceLocation.fromNamespaceAndPath 替换为这个方法
public static ResourceLocation getIdentifier(String path) { ... }

// TODO 这个要不要持久化
private static final Map<Integer, Set<ITrader>> commodityMap = new HashMap<>();

// TODO 剔除不必要的村庄优化性能
public void tick() { ... }
```

---

## 11. 关键文件参考

| 文件 | 路径 |
|------|------|
| 主类 | `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\VillageGenesis.java` |
| 方块注册 | `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\init\ModBlocks.java` |
| 物品注册 | `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\init\ModItems.java` |
| 配置示例 | `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\config\CommonConfig.java` |
| 工具类示例 | `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\utils\BoundingBox2D.java` |
| 管理类示例 | `G:\mcModProject\VillageGenesis\src\main\java\cn\ykcryobs\vg\villageSystem\VillageManager.java` |

---

## 12. 总结

### 优点
1. 清晰的包结构和模块化组织
2. 一致的命名约定
3. 完善的 JavaDoc 文档
4. 使用现代 NeoForge API (DeferredRegister)
5. 规范的日志记录

### 改进建议
1. 完成 TODO 标记的重构任务
2. 考虑添加单元测试
3. 统一异常处理策略
4. 考虑使用更严格的空值检查
