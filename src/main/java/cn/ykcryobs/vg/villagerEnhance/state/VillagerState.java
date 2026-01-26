package cn.ykcryobs.vg.villagerEnhance.state;

import net.minecraft.network.chat.Component;

/**
 * @author llykff
 */
public interface VillagerState {

    /**
     * 获取状态唯一名称
     *
     * @return 状态名称
     */
    Component getName();

    /**
     * 获取状态优先级 优先级越高，在状态列表中排序越靠前
     *
     * @return 优先级值
     */
    int getPriority();

    /**
     * 状态激活时调用
     */
    void onActivate();

    /**
     * 是否需要每刻更新
     *
     * @return 是否需要每刻更新
     */
    boolean hasTick();

    /**
     * 状态每刻更新
     */
    default void tick() {
    }

    ;

    /**
     * 状态结束时调用
     */
    void onDeactivate();
}
