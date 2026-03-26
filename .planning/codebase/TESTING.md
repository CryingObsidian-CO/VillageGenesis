# 测试模式分析

**项目:** VillageGenesis (Minecraft 1.21.1 NeoForge Mod)
**分析日期:** 2026-03-26
**分析范围:** 测试框架、测试目录、测试配置

---

## 1. 测试现状

### 1.1 测试目录检查结果

**状态:** 项目当前 **没有测试目录和测试文件**

```
检查命令: if (Test-Path "src/test") { ... }
结果: No test directory found

检查命令: find . -name "*Test*.java" -o -name "*test*.java"
结果: No file found
```

### 1.2 build.gradle 测试配置

**当前状态:** build.gradle 中没有显式的测试依赖配置

```groovy
dependencies {
  // 仅包含注释掉的示例依赖
  // 没有测试框架依赖 (JUnit, TestNG 等)
}
```

**存在的测试相关配置:**
- GameTest Server 运行配置 (用于游戏内测试)
- 但没有对应的测试代码

```groovy
gameTestServer {
  type = "gameTestServer"
  systemProperty 'neoforged.enabledGameTestNamespaces', project.mod_id
}
```

---

## 2. NeoForge/Minecraft 模组测试建议

### 2.1 推荐测试框架

对于 Minecraft 1.21.1 NeoForge 模组，推荐以下测试方案：

#### 方案 A: GameTest 系统 (官方推荐)

NeoForge 提供内置的 GameTest 系统，适合测试游戏内功能：

```java
// 示例 GameTest 类
@GameTest(template = "empty_template")
public class VillageTest {
    
    @GameTest(template = "empty_template")
    public static void testVillageRegistration(GameTestHelper helper) {
        BlockPos pos = new BlockPos(0, 64, 0);
        BoundingBox2D box = new BoundingBox2D(-10, -10, 10, 10);
        
        VillageManager.registerVillage(pos, box, null);
        
        helper.assertTrue(
            VillageManager.isPosInVillage(pos),
            "Village should be registered at position"
        );
        helper.succeed();
    }
}
```

**GameTest 配置要求:**
1. 创建测试结构模板: `src/main/resources/data/modid/structures/`
2. 注册 GameTest: `src/main/java/.../GameTests.java`

#### 方案 B: JUnit 5 单元测试

适合测试纯逻辑代码（不依赖 Minecraft 运行时）：

```groovy
// build.gradle 添加依赖
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.10.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.10.0'
    testImplementation 'org.mockito:mockito-core:5.5.0'
}

test {
    useJUnitPlatform()
}
```

**建议的测试目录结构:**
```
src/test/java/cn/ykcryobs/vg/
├── utils/
│   └── BoundingBox2DTest.java
├── villageSystem/
│   └── VillageManagerTest.java
└── item/
    └── currency/
        └── CurrencyTypeTest.java
```

---

## 3. 推荐测试策略

### 3.1 分层测试策略

| 测试类型 | 适用场景 | 推荐框架 |
|----------|----------|----------|
| 单元测试 | 纯逻辑类、工具类 | JUnit 5 |
| 集成测试 | 注册系统、配置系统 | JUnit 5 + Mockito |
| 游戏测试 | 方块行为、物品交互 | GameTest |
| 端到端测试 | 完整游戏流程 | GameTest |

### 3.2 优先测试目标

根据项目代码分析，建议优先为以下类添加测试：

**高优先级 (纯逻辑，易于测试):**
1. `BoundingBox2D` - 几何计算，无依赖
2. `CurrencyType` - 枚举类
3. `NameGenerationMode` - 枚举类

**中优先级 (需要 Mock):**
1. `VillageManager` - 需要模拟 SavedData
2. `BaseCurrencyItem` - 需要模拟 ItemStack

**低优先级 (需要完整游戏环境):**
1. 方块和物品注册
2. 命令系统
3. 网络通信

---

## 4. 测试代码示例

### 4.1 BoundingBox2D 单元测试示例

```java
package cn.ykcryobs.vg.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BoundingBox2D 测试")
class BoundingBox2DTest {
    
    @Test
    @DisplayName("应正确创建边界框")
    void testCreation() {
        BoundingBox2D box = new BoundingBox2D(0, 0, 10, 10);
        
        assertEquals(0, box.getMinX());
        assertEquals(0, box.getMinZ());
        assertEquals(10, box.getMaxX());
        assertEquals(10, box.getMaxZ());
    }
    
    @Test
    @DisplayName("应正确计算中心点")
    void testCenter() {
        BoundingBox2D box = new BoundingBox2D(0, 0, 10, 10);
        
        assertEquals(5, box.getCenterX());
        assertEquals(5, box.getCenterZ());
    }
    
    @Test
    @DisplayName("应正确检测点是否在边界内")
    void testContains() {
        BoundingBox2D box = new BoundingBox2D(0, 0, 10, 10);
        
        assertTrue(box.inSide(5, 5));
        assertTrue(box.inSide(0, 0));
        assertTrue(box.inSide(10, 10));
        assertFalse(box.inSide(11, 5));
        assertFalse(box.inSide(5, 11));
    }
    
    @Test
    @DisplayName("应自动修正无效边界")
    void testInvalidBoundsCorrection() {
        BoundingBox2D box = new BoundingBox2D(10, 10, 0, 0);
        
        assertEquals(0, box.getMinX());
        assertEquals(0, box.getMinZ());
        assertEquals(10, box.getMaxX());
        assertEquals(10, box.getMaxZ());
    }
    
    @Test
    @DisplayName("应正确序列化和反序列化 NBT")
    void testNBTSerialization() {
        BoundingBox2D original = new BoundingBox2D(5, 10, 15, 20);
        
        CompoundTag tag = original.serializeNBT();
        BoundingBox2D deserialized = BoundingBox2D.deserializeNBT(tag);
        
        assertEquals(original, deserialized);
    }
}
```

### 4.2 GameTest 示例

```java
package cn.ykcryobs.vg.gametest;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder(VillageGenesis.MOD_ID)
public class VillageGameTests {
    
    @GameTest(template = "empty_template")
    public static void testVillageRegistration(GameTestHelper helper) {
        BlockPos center = new BlockPos(0, 64, 0);
        
        // 测试村庄注册
        helper.succeed();
    }
}
```

---

## 5. 测试配置建议

### 5.1 build.gradle 更新

```groovy
// 添加测试依赖
dependencies {
    // JUnit 5
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.10.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.10.0'
    
    // Mockito (可选，用于 Mock)
    testImplementation 'org.mockito:mockito-core:5.5.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.5.0'
    
    // AssertJ (可选，更流畅的断言)
    testImplementation 'org.assertj:assertj-core:3.24.2'
}

// 启用 JUnit 5
test {
    useJUnitPlatform()
    
    // 测试日志
    testLogging {
        events "passed", "skipped", "failed"
    }
}

// 测试任务依赖
tasks.named('test') {
    dependsOn 'generateModMetadata'
}
```

### 5.2 测试资源目录

```
src/test/
├── java/
│   └── cn/ykcryobs/vg/
│       ├── utils/
│       ├── villageSystem/
│       └── item/
└── resources/
    └── (测试配置文件)
```

---

## 6. CI/CD 集成建议

### 6.1 GitHub Actions 示例

```yaml
name: Test

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Run tests
        run: ./gradlew test
      
      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-results
          path: build/reports/tests/
```

---

## 7. 测试命名约定

根据 `.editorconfig` 配置，测试类命名约定：

```properties
ij_java_test_name_prefix = 
ij_java_test_name_suffix = Test
```

**示例:**
- 测试类: `BoundingBox2DTest.java`
- 测试方法: `testCreation()`, `testContains()`

---

## 8. 测试覆盖率目标

| 模块 | 目标覆盖率 | 说明 |
|------|-----------|------|
| utils/ | 80%+ | 纯逻辑，易于测试 |
| item/currency/ | 70%+ | 较少依赖 |
| villageSystem/ | 50%+ | 需要更多 Mock |
| init/ | 30%+ | 注册代码难以单元测试 |

---

## 9. 总结

### 当前状态
- **测试目录:** 不存在
- **测试文件:** 无
- **测试依赖:** 未配置
- **测试配置:** 仅有 GameTest 运行配置，无测试代码

### 建议行动
1. **立即:** 添加 JUnit 5 依赖和基础测试配置
2. **短期:** 为工具类（如 `BoundingBox2D`）添加单元测试
3. **中期:** 为核心业务逻辑添加测试覆盖
4. **长期:** 建立 GameTest 测试套件用于游戏内功能测试

### 预期收益
- 提高代码质量和稳定性
- 便于重构和维护
- 支持 CI/CD 自动化
- 文档化预期行为
