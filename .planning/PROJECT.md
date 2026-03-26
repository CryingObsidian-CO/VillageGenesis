# VillageGenesis

## What This Is

VillageGenesis 是一个 Minecraft 1.21.1 NeoForge 模组，致力于在游戏中构建一个尽可能还原现实社会的村庄文明演进系统。玩家作为"影响者"角色，观察并引导村庄从原始部落演变为现代都市，体验完整的社会动态模拟。

## Core Value

**在可能的范围内，尽可能还原现实社会。**

这是驱动所有设计决策的核心原则。当面临权衡时，选择更贴近现实社会运作方式的方案。

## Requirements

### Validated

(从现有代码库推断的已实现功能)

- ✓ 村庄生命周期管理（创建、演进、衰退）— 现有
- ✓ 村庄等级与经验系统 — 现有
- ✓ 六阶段演进系统（原始部落 → 现代都市）— 现有
- ✓ 设施系统（建筑类型、等级、耐久）— 现有
- ✓ 基础经济系统（货币、交易、支付）— 现有
- ✓ 村民数据扩展（幸福度、忠诚度、适应度等）— 现有
- ✓ 村民状态机系统 — 现有
- ✓ 异步交易处理 — 现有
- ✓ 村庄信息面板（客户端UI）— 现有
- ✓ 命令系统（村庄、经济、村民命令）— 现有

### Active

(当前开发目标)

- [ ] TradeRoute 贸易路线系统（首要任务）
- [ ] 村庄间外交关系系统
- [ ] 村庄间经济依赖系统
- [ ] 人口迁移系统
- [ ] 政治互动系统（联盟、战争、吞并）
- [ ] 军事冲突系统（纯模拟）

### Out of Scope

(明确排除的范围)

- 玩家直接参与战斗 — 保持纯模拟，玩家作为观察者/影响者
- 玩家作为村庄领袖角色 — 玩家是影响者而非决策者
- 简化的模拟逻辑 — 核心价值是真实模拟，不牺牲深度

## Context

**技术环境：**
- Minecraft 1.21.1 + NeoForge 21.1.215
- Java 21 + Gradle 8.x + ModDevGradle 2.0.120
- 无第三方模组依赖

**现有架构：**
- 分层架构：入口层 → 注册层 → 核心系统层 → 扩展层
- 核心系统：VillageManager、EconomySystem、FacilitySystem
- 数据持久化：SavedData 世界级存储
- 异步处理：TransactionManager 线程池

**设计决策：**
- TradeRoute 采用混合模式：村庄自动建立基础贸易，玩家可干预和优化
- 村庄间关系包含：外交、经济、人口、政治、军事五个维度
- 村庄演进：完整的六阶段演进链（原始 → 现代）
- 玩家角色：影响者（可影响但非决定性因素）
- 性能平衡：模拟深度与游戏性能平衡考虑

## Constraints

- **技术栈**: Minecraft 1.21.1 + NeoForge — 已确定的技术平台
- **性能**: 需要平衡模拟深度与游戏流畅度 — 不能严重影响游戏体验
- **兼容性**: 无第三方模组依赖 — 保持独立性
- **版本**: Java 21 — 平台要求

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| TradeRoute 混合模式 | 村庄自动贸易 + 玩家可干预，平衡自动化与玩家参与感 | — Pending |
| 军事冲突纯模拟 | 符合"还原现实社会"核心价值，玩家作为观察者 | — Pending |
| 人口迁移模拟驱动 | 村民因经济、安全等因素自动迁移，更真实 | — Pending |
| 玩家角色为影响者 | 可影响但非决定性因素，保持模拟的真实性 | — Pending |
| TradeRoute 优先实现 | 是其他村庄间系统的基础，需要先完善 | — Pending |

---

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd:transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd:complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state

---

*Last updated: 2026-03-26 after initialization*
