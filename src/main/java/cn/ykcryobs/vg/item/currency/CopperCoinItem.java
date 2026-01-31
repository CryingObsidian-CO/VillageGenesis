package cn.ykcryobs.vg.item.currency;

import cn.ykcryobs.vg.villageSystem.economy.CurrencyType;
import net.minecraft.world.item.Item;

/**
 * 铜币物品类 金属货币阶段的基础货币
 *
 * @author llykff
 */
public class CopperCoinItem extends BaseCurrencyItem {

    /**
     * 构造函数
     */
    public CopperCoinItem() {
        super(new Item.Properties(), CurrencyType.METAL);
    }

}