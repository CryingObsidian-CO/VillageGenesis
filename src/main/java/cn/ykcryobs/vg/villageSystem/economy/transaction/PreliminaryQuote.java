package cn.ykcryobs.vg.villageSystem.economy.transaction;

import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.economy.ITrader;
import cn.ykcryobs.vg.villageSystem.economy.VillageEconomyData;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

/**
 * 报价单
 *
 * @author llykff
 */
public class PreliminaryQuote {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final ITrader seller;
    private final int availableAmount;
    private final float unitWorkPoints; // 单价（工分）

    /**
     * 创建初步报价单
     *
     * @param seller          卖家
     * @param targetItem      目标物品
     * @param availableAmount 可用数量
     * @param profitFactor    利润系数
     * @param economy         经济数据
     */
    public PreliminaryQuote(ITrader seller, Item targetItem, int availableAmount, float profitFactor,
            VillageEconomyData economy) {
        if (!(targetItem instanceof ITradableItem tradableItem)) {
            LOGGER.error("Item {} is not tradable!", targetItem);
            throw new IllegalArgumentException("Item " + targetItem + " is not tradable!");
        } else {
            this.seller = seller;
            this.availableAmount = availableAmount;
            float baseWorkPoint = tradableItem.getWorkPoint();
            float supplyDemandFactor = economy.getFactorForItem(targetItem);
            this.unitWorkPoints = baseWorkPoint * supplyDemandFactor * profitFactor;
            LOGGER.debug(
                    "Created preliminary quote: seller={}, targetItem={}, availableAmount={}, profitFactor={}, unitWorkPoints={}",
                    seller.getTraderId(), targetItem, availableAmount, profitFactor, unitWorkPoints);
        }
    }

    /**
     * 获取卖家
     *
     * @return 卖家
     */
    public ITrader getSeller() {
        return seller;
    }

    /**
     * 获取可用数量
     *
     * @return 可用数量
     */
    public int getAvailableAmount() {
        return availableAmount;
    }

    /**
     * 获取单价（工分）
     *
     * @return 单价（工分）
     */
    public float getUnitWorkPoints() {
        return unitWorkPoints;
    }

    @Override
    public String toString() {
        return "PreliminaryQuote{seller=" + seller.getTraderId() + ", availableAmount=" + availableAmount
                + ", unitWorkPoints=" + unitWorkPoints + "}";
    }
}

