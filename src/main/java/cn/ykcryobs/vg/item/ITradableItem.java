package cn.ykcryobs.vg.item;

import cn.ykcryobs.vg.villageSystem.economy.market.ResourceType;

/**
 * 可交易物品接口
 *
 * @author llykff
 */
// TODO 可以利用数据包更改
public interface ITradableItem {

    /**
     * 是否可交易
     *
     * @return 是否可交易
     */
    boolean isTradable();

    /**
     * 获取物品的工分 工分用于计算物品的价值和交易效率
     *
     * @return 物品的工分
     */
    float getWorkPoint();

    /**
     * 获取物品的资源类型
     *
     * @return 物品的资源类型
     */
    default ResourceType getResourceType() {
        return ResourceType.NONE;
    }
}