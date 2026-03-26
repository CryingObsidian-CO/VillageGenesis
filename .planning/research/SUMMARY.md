# VillageGenesis 项目研究综合摘要

**项目:** VillageGenesis - Minecraft 1.21.1 NeoForge Mod
**研究日期:** 2026-03-26
**研究范围:** 贸易路线与村庄互联系统
**研究维度:** STACK, FEATURES, ARCHITECTURE, PITFALLS

---

## 执行摘要

VillageGenesis 项目旨在 Minecraft 中构建一个尽可能还原现实社会的村庄文明演进系统。当前研究聚焦于**贸易路线与村庄互联系统**的扩展，涵盖外交、经济依赖、人口迁移、政治军事五个维度。

**核心发现：** 现有架构（SavedData + Payload + ServerTickEvent）为扩展提供了良好基础，但存在线程安全、数据持久化缺失等关键技术债务。推荐采用分批处理、异步计算、事件驱动等模式实现新系统，避免每 tick 遍历、静态变量存储世界引用等反模式。

**研究置信度：** 整体 **85%**（技术方案 90%，架构设计 90%，特性规划 80%，风险识别 85%）

---

## 研究维度概览

### 1. 技术栈研究 (STACK.md)

**研究焦点：** NeoForge 1.21.1 技术方案与实现模式

**关键发现：**

| 领域 | 推荐方案 | 置信度 |
|------|---------|--------|
| 数据持久化 | SavedData + StreamCodec 组合 | 95% |
| 定时任务 | 分批处理 + ServerTickEvent | 90% |
| 复杂计算 | 异步计算 + CompletableFuture | 75% |
| 村庄通信 | NeoForge EventBus + 自定义事件 | 90% |
| 路径计算 | 增强欧几里得距离 + 地形修正 | 70% |
| 数据结构 | 关系矩阵 + 图结构 | 90% |

**核心反模式：**
- 每 tick 遍历所有村庄关系（O(n²) 复杂度）
- 静态变量存储世界引用（多世界问题）
- 主线程执行复杂计算（阻塞游戏循环）
- 实时 A* 路径查找（性能灾难）

**详细内容：** [STACK.md](./STACK.md)

---

### 2. 特性研究 (FEATURES.md)

**研究焦点：** 贸易路线系统与村庄互联机制的功能规划

**特性分层：**

```
Table Stakes (基础必备) ─────────────────────────────────────
├── 村庄发现机制
├── 距离计算
├── 路径定义
├── 基础关系定义
└── 资源运输

Differentiators (差异化特性) ────────────────────────────────
├── 动态定价系统 ★
├── 多节点贸易网络 ★
├── 玩家干预点 ★ (符合项目定位)
├── 经济依赖链
└── 事件驱动中断

Anti-Features (明确不做) ────────────────────────────────────
├── 玩家直接控制商队
├── 实时物理运输模拟
├── 复杂贸易条约系统
└── 跨维度贸易
```

**实现路线：**
- MVP：基础贸易路线功能（预计复杂度：中）
- V1.0：核心差异化特性（预计复杂度：高）
- V2.0：深度模拟（预计复杂度：高）

**详细内容：** [FEATURES.md](./FEATURES.md)

---

### 3. 架构研究 (ARCHITECTURE.md)

**研究焦点：** 村庄经济模拟与互联架构设计

**核心架构：**

```
┌─────────────────────────────────────────────────────────┐
│                InterVillageManager (协调层)              │
│  ┌─────────────┬─────────────┬─────────────────────┐   │
│  │ TradeRoute  │ Diplomacy   │ Economic            │   │
│  │ Manager     │ Manager     │ Interdependence     │   │
│  │ (现有增强)   │ (新增)      │ Manager (新增)       │   │
│  └─────────────┴─────────────┴─────────────────────┘   │
│  ┌─────────────┬─────────────┐                          │
│  │ Migration   │ Political   │                          │
│  │ Manager     │ Manager     │                          │
│  │ (新增)      │ (新增)      │                          │
│  └─────────────┴─────────────┘                          │
└─────────────────────────────────────────────────────────┘
                          │
                          │ 事件/接口
                          ▼
┌─────────────────────────────────────────────────────────┐
│              VillageManager (现有 - 不修改)              │
│              VillageData (现有 - 不修改)                 │
└─────────────────────────────────────────────────────────┘
```

**设计原则：**
1. **最小侵入** - 不修改 VillageManager/VillageData 核心逻辑
2. **事件驱动** - 通过事件系统解耦村庄间系统与单村庄系统
3. **分层协调** - 新增协调层管理村庄间关系
4. **性能优先** - 分片更新 + 异步计算
5. **玩家影响者** - 玩家可影响但非决定性因素（最大 30% 影响力）

**详细内容：** [ARCHITECTURE.md](./ARCHITECTURE.md)

---

### 4. 陷阱研究 (PITFALLS.md)

**研究焦点：** 经济模拟与贸易路线系统的常见陷阱

**陷阱统计：**

| 类别 | 数量 | 最高优先级 |
|------|------|-----------|
| 经济系统核心 | 4 | P0 (通货膨胀) |
| 贸易路线 | 8 | P2 (最短路径) |
| 村庄间经济依赖 | 4 | P2 (过度依赖) |
| Minecraft 特有 | 5 | P0 (村民交易刷钱) |
| VillageGenesis 特定 | 5 | P0 (线程安全) |
| 系统架构 | 5 | P0 (异步同步混合) |

**P0 级陷阱（立即处理）：**

| 陷阱 | 位置 | 问题 |
|------|------|------|
| 静态 Map 线程安全 | VillageManager.java:32,37 | HashMap 无同步机制 |
| 通货膨胀/紧缩 | 经济系统核心 | 货币生成与销毁不平衡 |
| 村民交易刷钱 | 经济系统核心 | 交易可无限刷新 |
| 异步与同步混合 | TransactionManager | 跨线程访问无同步 |

**健康监控指标：**

| 指标 | 健康范围 | 警告阈值 |
|------|---------|---------|
| 货币总量增长率 | < 5%/天 | 5-10%/天 |
| 价格波动率 | < 10% | 10-30% |
| 村庄财富基尼系数 | < 0.4 | 0.4-0.6 |
| TPS | > 19 | 15-19 |

**详细内容：** [PITFALLS.md](./PITFALLS.md)

---

## 建议阶段规划

基于研究结果，建议将项目分为 **6 个阶段**：

### Phase 1: 基础设施层 (2-3 天)

**目标：** 建立村庄间系统的数据存储和协调框架

**任务：**
- [ ] 创建 InterVillageManager SavedData 类
- [ ] 创建 VillageRelation 基类
- [ ] 创建 RelationKey 工具类
- [ ] 注册 InterVillageManager 到世界存储
- [ ] 创建 IInterVillageContext 接口

**验收标准：**
- InterVillageManager 可持久化到世界数据
- 可通过 RelationKey 唯一标识村庄对

**关键风险：** 线程安全问题（P0）

---

### Phase 2: 贸易系统增强 (3-4 天)

**目标：** 增强现有 TradeRouteManager，实现自动化贸易

**任务：**
- [ ] 扩展 TradeRouteManager 添加自动路线发现
- [ ] 实现批量贸易执行逻辑
- [ ] 添加贸易流量统计和分析
- [ ] 创建 TradeCompletedEvent 事件
- [ ] 集成到 InterVillageManager

**验收标准：**
- 村庄可自动建立贸易路线
- 贸易执行正确更新双方经济数据

**关键风险：** 通货膨胀（P0）、距离计算陷阱（P2）

---

### Phase 3: 外交系统 (3-4 天)

**目标：** 实现村庄间外交关系管理

**任务：**
- [ ] 创建 DiplomacyManager 类
- [ ] 创建 DiplomacyState 枚举
- [ ] 创建 DiplomacyRelation 数据类
- [ ] 实现外交评分计算算法
- [ ] 创建 DiplomacyStateChangedEvent 事件
- [ ] 与 TradeRouteManager 联动

**验收标准：**
- 外交状态可正确计算和更新
- 外交状态影响贸易条件

**关键风险：** 反馈循环陷阱（P3）

---

### Phase 4: 经济依赖系统 (2-3 天)

**目标：** 实现村庄间经济依赖度计算

**任务：**
- [ ] 创建 EconomicInterdependenceManager 类
- [ ] 创建 ResourceDependency 数据类
- [ ] 创建 EconomicRelation 数据类
- [ ] 实现依赖度计算算法
- [ ] 集成到 InterVillageManager

**验收标准：**
- 可计算村庄间资源依赖度
- 依赖度影响外交和迁移

**关键风险：** 过度依赖陷阱（P2）、垄断形成陷阱（P2）

---

### Phase 5: 人口迁移系统 (3-4 天)

**目标：** 实现村民自动迁移机制

**任务：**
- [ ] 创建 MigrationManager 类
- [ ] 创建 MigrationReason 枚举
- [ ] 创建 MigrationDecision 数据类
- [ ] 实现迁移意愿计算算法（推拉理论）
- [ ] 实现迁移执行逻辑
- [ ] 创建 MigrationEvent 事件

**验收标准：**
- 村民可因经济、安全等因素迁移
- 迁移正确更新村庄人口和村民数据

**关键风险：** 区块加载陷阱（P2）

---

### Phase 6: 政治军事系统 (4-5 天)

**目标：** 实现政治互动和军事冲突模拟

**任务：**
- [ ] 创建 PoliticalManager 类
- [ ] 创建 PoliticalEventType 枚举
- [ ] 创建 MilitaryConflict 模拟器
- [ ] 实现政治事件处理
- [ ] 实现军事冲突模拟算法（Lanchester 方程）
- [ ] 创建 PoliticalEvent 事件

**验收标准：**
- 村庄可结盟、宣战、和谈
- 军事冲突模拟结果合理

**关键风险：** 性能与深度权衡陷阱

---

## 研究标志

### 高优先级标志

| 标志 | 含义 | 来源 |
|------|------|------|
| `P0-THREAD-SAFETY` | 线程安全问题需立即修复 | PITFALLS.md |
| `P0-INFLATION` | 通货膨胀风险需核心设计 | PITFALLS.md |
| `TD-LEVEL-STATIC` | 静态变量存储世界引用的技术债务 | ARCHITECTURE.md |
| `TD-COMMODITYMAP` | commodityMap 未持久化的技术债务 | PITFALLS.md |

### 设计决策标志

| 标志 | 含义 | 来源 |
|------|------|------|
| `PLAYER-INFLUENCER` | 玩家角色为影响者（最大 30% 影响力） | PROJECT.md |
| `EVENT-DRIVEN` | 使用事件驱动架构解耦系统 | ARCHITECTURE.md |
| `BATCH-PROCESSING` | 使用分批处理优化性能 | STACK.md |
| `ASYNC-COMPUTE` | 复杂计算使用异步处理 | STACK.md |

### 风险标志

| 标志 | 含义 | 来源 |
|------|------|------|
| `RISK-PERFORMANCE` | 性能风险需关注 | ARCHITECTURE.md |
| `RISK-BALANCE` | 游戏平衡性风险 | PITFALLS.md |
| `RISK-DATA-CONSISTENCY` | 数据一致性风险 | PITFALLS.md |

---

## 关键技术债务

### 现有技术债务

| 问题 | 优先级 | 解决方案 | 阶段 |
|------|--------|---------|------|
| VillageGenesis.level 静态变量 | 高 | 使用 Attachment 或参数传递 | Phase 1 |
| VillageManager.commodityMap 未持久化 | 中 | 移入 VillageManager 持久化 | Phase 2 |
| TransactionManager 回调嵌套 | 中 | 改为循环或限制递归深度 | Phase 2 |
| 村民状态系统空实现 | 中 | 按优先级实现状态逻辑 | Phase 5 |
| TradeRoute 返回 null | 低 | 使用 Optional<T> 包装 | Phase 2 |

---

## 置信度评估

### 各维度置信度

| 维度 | 置信度 | 说明 |
|------|--------|------|
| 技术方案 (STACK) | 90% | NeoForge 官方推荐模式，有现成参考 |
| 架构设计 (ARCHITECTURE) | 90% | 基于现有架构扩展，边界清晰 |
| 特性规划 (FEATURES) | 80% | 参考多个成功案例，但需验证差异化特性 |
| 风险识别 (PITFALLS) | 85% | 全面识别 35 个陷阱，但实际发生概率需验证 |

### 整体置信度：85%

**置信度高的原因：**
- 现有架构为扩展提供了良好基础
- NeoForge 1.21.1 文档完善
- 有多个成功案例可参考（Anno、Civilization、Millénaire）

**不确定性来源：**
- 动态定价系统的平衡性
- 军事冲突模拟的真实性
- 区块加载机制对远距离贸易的影响
- 玩家影响力的量化

---

## 下一步行动

### 立即行动 (Phase 0)

1. **修复线程安全问题** - 将 HashMap 替换为 ConcurrentHashMap
2. **设计货币双向流动机制** - 防止通货膨胀
3. **实现 commodityMap 持久化** - 防止数据丢失

### 规划行动

1. 创建 ROADMAP.md - 基于研究结果制定详细路线图
2. 创建 Phase 1 详细计划 - 使用 `/gsd:plan-phase` 指令
3. 建立监控体系 - 实现经济健康指标监控

---

## 参考资源

### NeoForge 官方文档
- [SavedData](https://docs.neoforged.net/docs/1.21.1/datastorage/saveddata)
- [Networking Payload](https://docs.neoforged.net/docs/1.21.1/networking/payload)
- [Event System](https://docs.neoforged.net/docs/1.21.1/concepts/events)

### 学术参考
- **推拉理论（Push-Pull Theory）**: Everett Lee, "A Theory of Migration", 1966
- **Lanchester 方程**: Frederick Lanchester, "Aircraft in Warfare", 1916
- **PageRank 算法**: Page et al., "The PageRank Citation Ranking", 1999

### 类似模组参考
- **Millénaire**: 村庄经济与贸易系统
- **TerraFirmaCraft**: 复杂经济模拟
- **MineColonies**: 村庄管理与外交

---

## 研究文件清单

| 文件 | 内容 | 状态 |
|------|------|------|
| [STACK.md](./STACK.md) | 技术栈研究 | 完成 |
| [FEATURES.md](./FEATURES.md) | 特性研究 | 完成 |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | 架构研究 | 完成 |
| [PITFALLS.md](./PITFALLS.md) | 陷阱研究 | 完成 |
| [SUMMARY.md](./SUMMARY.md) | 综合摘要 | 完成 |

---

**文档版本:** 1.0
**最后更新:** 2026-03-26
**研究团队:** GSD Project Researcher
**下一步:** 创建 ROADMAP.md 或使用 `/gsd:plan-phase` 开始阶段规划
