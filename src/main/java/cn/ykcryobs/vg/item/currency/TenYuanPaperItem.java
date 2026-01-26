package cn.ykcryobs.vg.item.currency;

import net.minecraft.world.item.Item;

/**
 * 10元纸币物品类 商业都市阶段的大面额纸币
 *
 * @author llykff
 */
public class TenYuanPaperItem extends BasePaperCurrencyItem {

    /**
     * 构造函数
     */
    public TenYuanPaperItem() {
        super(new Item.Properties(), 10);
    }
}