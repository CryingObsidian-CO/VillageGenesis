package cn.ykcryobs.vg.item.currency;

import cn.ykcryobs.vg.villageSystem.economy.CurrencyType;

/**
 * 基础纸币物品类 所有纸币物品的基类
 *
 * @author llykff
 */
public class BasePaperCurrencyItem extends BaseCurrencyItem {

    private final int denomination;

    /**
     * 构造函数 纸币的货币类型固定为PAPER
     *
     * @param properties   物品属性
     * @param denomination 纸币面额
     */
    public BasePaperCurrencyItem(Properties properties, int denomination) {
        super(properties, CurrencyType.PAPER);
        this.denomination = denomination;
    }

    /**
     * 获取纸币面额
     *
     * @return 纸币面额
     */
    public int getDenomination() {
        return this.denomination;
    }
}