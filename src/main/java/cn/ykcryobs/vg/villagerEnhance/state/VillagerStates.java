package cn.ykcryobs.vg.villagerEnhance.state;

import net.minecraft.network.chat.Component;

/**
 * 村民状态枚举类
 *
 * @author llykff
 */
public enum VillagerStates implements VillagerState {
    /**
     * 生病状态
     */
    SICK("sick", 50) {
        @Override
        public void onActivate() {
            // TODO 生病状态激活逻辑
        }

        @Override
        public boolean hasTick() {
            return true;
        }

        @Override
        public void tick() {
            // TODO 生病状态每刻更新
        }

        @Override
        public void onDeactivate() {
            // TODO 生病状态结束逻辑
        }

    },

    /**
     * 未成年状态
     */
    CHILD("child", 40) {
        @Override
        public void onActivate() {
            // TODO 未成年状态激活逻辑
        }

        @Override
        public boolean hasTick() {
            return true;
        }

        @Override
        public void tick() {
            // TODO 未成年状态每刻更新
        }

        @Override
        public void onDeactivate() {
            // TODO 未成年状态结束逻辑
        }
    },

    /**
     * 无家可归状态
     */
    HOMELESS("homeless", 30) {
        @Override
        public void onActivate() {
            // TODO 无家可归状态激活逻辑
        }

        @Override
        public boolean hasTick() {
            return true;
        }

        @Override
        public void tick() {
            // TODO 无家可归状态每刻更新
        }

        @Override
        public void onDeactivate() {
            // TODO 无家可归状态结束逻辑
        }

    },

    /**
     * 失业状态
     */
    UNEMPLOYED("unemployed", 20) {
        @Override
        public void onActivate() {
            // TODO 失业状态激活逻辑
        }

        @Override
        public boolean hasTick() {
            return true;
        }

        @Override
        public void tick() {
            // TODO 失业状态每刻更新
        }

        @Override
        public void onDeactivate() {
            // TODO 失业状态结束逻辑
        }
    };

    private final String name;
    private final int priority;

    /**
     * 构造函数
     *
     * @param name     状态名称
     * @param priority 状态优先级
     */
    VillagerStates(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    /**
     * 获取状态唯一名称
     *
     * @return 状态名称
     */
    @Override
    public Component getName() {
        return Component.translatable("villager.village_genesis." + name);
    }

    /**
     * 获取状态优先级
     *
     * @return 优先级值
     */
    @Override
    public int getPriority() {
        return priority;
    }
}