package cn.ykcryobs.vg.item.currency;

import cn.ykcryobs.vg.villageSystem.economy.CurrencyType;
import net.minecraft.world.item.Item;

/**
 * 银币物品类 金属货币阶段的中等价值货币
 *
 * @author llykff
 */
public class SilverCoinItem extends BaseCurrencyItem {

    /**
     * 构造函数
     */
    public SilverCoinItem() {
        super(new Item.Properties(), CurrencyType.METAL);
    }
}