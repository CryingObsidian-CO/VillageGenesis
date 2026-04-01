package cn.ykcryobs.vg.villageSystem.economy.tax.tradeTax;

import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
import net.minecraft.world.item.Item;

import java.util.Optional;
import java.util.UUID;

/**
 * 交易税计算器接口，用于计算两个村庄之间的交易税
 *
 * @author llykff
 */
public interface ITradeTaxCalculator {

    /**
     * 计算两个村庄之间的交易税
     *
     * @param buyerVillageId  购买者村庄ID
     * @param sellerVillageId 销售者村庄ID
     * @param item            交易物品
     * @param basePrice       交易前的基础价格
     * @return 交易税率（0.0到1.0，例如0.05表示5%）
     */
    float calculateTaxRate(UUID buyerVillageId, UUID sellerVillageId, Item item, float basePrice);

    /**
     * 计算两个村庄之间的交易距离惩罚
     *
     * @param villageA 购买者村庄ID
     * @param villageB 销售者村庄ID
     * @return 交易距离惩罚分（0.0到1.0，例如0.05表示5%的距离惩罚）
     */
    float calculateDistancePenalty(UUID villageA, UUID villageB);

    /**
     * 计算交易税金额
     *
     * @param basePrice 交易前的基础价格
     * @param taxRate   交易税率
     * @return 交易税金额
     */
    default float calculateTaxAmount(float basePrice, float taxRate) {
        return basePrice * taxRate;
    }

    /**
     * 检查两个村庄之间是否存在交易路线
     *
     * @param villageA 购买者村庄ID
     * @param villageB 销售者村庄ID
     * @return 如果存在交易路线则返回true
     */
    boolean hasTradeRoute(UUID villageA, UUID villageB);

    /**
     * 获取两个村庄之间的交易路线距离
     *
     * @param villageA 购买者村庄ID
     * @param villageB 销售者村庄ID
     * @return 交易路线距离（单位：方块），如果不存在交易路线则返回-1
     */
    float getTradeRouteDistance(UUID villageA, UUID villageB);

    /**
     * 获取基于交易者的交易税率
     *
     * @param buyer  购买者交易者
     * @param seller 销售者交易者
     * @param item   交易物品
     * @return 基于交易者的交易税率（0.0到1.0，例如0.05表示5%）
     */
    default float getTaxRate(ITrader buyer, ITrader seller, Item item) {
        Optional<UUID> buyerVillage = buyer.getVillageIdIfHasVillage();
        Optional<UUID> sellerVillage = seller.getVillageIdIfHasVillage();

        if (buyerVillage.isEmpty() || sellerVillage.isEmpty()) {
            return 0f;
        }

        if (buyerVillage.get().equals(sellerVillage.get())) {
            return 0f;
        }

        float basePrice = seller.getUnitWorkPoint((cn.ykcryobs.vg.item.ITradableItem) item);
        return calculateTaxRate(buyerVillage.get(), sellerVillage.get(), item, basePrice);
    }

    /**
     * 获取基于交易者的交易距离惩罚
     *
     * @param buyer  购买者交易者
     * @param seller 销售者交易者
     * @return 基于交易者的交易距离惩罚分（0.0到1.0，例如0.05表示5%的距离惩罚）
     */
    default float getDistancePenalty(ITrader buyer, ITrader seller) {
        Optional<UUID> buyerVillage = buyer.getVillageIdIfHasVillage();
        Optional<UUID> sellerVillage = seller.getVillageIdIfHasVillage();

        if (buyerVillage.isEmpty() || sellerVillage.isEmpty()) {
            return 0f;
        }

        return calculateDistancePenalty(buyerVillage.get(), sellerVillage.get());
    }
}
