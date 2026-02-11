package cn.ykcryobs.vg.item.currency;

import net.minecraft.world.item.Item;

/**
 * 金币物品类 金属货币阶段的高价值货币
 *
 * @author llykff
 */
public class GoldCoinItem extends BaseCurrencyItem {

    /**
     * 构造函数
     */
    public GoldCoinItem() {
        super(new Item.Properties(), CurrencyType.METAL);
    }
}