package cn.ykcryobs.vg.villageSystem.economy.trader.sellerFilter;

import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.economy.tax.tradeTax.TradeTaxCalculator;
import cn.ykcryobs.vg.villageSystem.economy.tradeRoute.TradeType;
import cn.ykcryobs.vg.villageSystem.economy.trader.TraderSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 数量过滤器
 *
 * @author llykff
 */
public class QuantityScoreFilter implements ISellerFilter {

    @Override
    public String getFilterName() {
        return "QuantityScoreFilter";
    }

    @Override
    public int getPriority() {
        return 30;
    }

    @Override
    public double getWeight() {
        return ServerConfig.quantityScoreWeight.get();
    }

    @Override
    public double getThreshold() {
        return ServerConfig.quantityScoreThreshold.get();
    }

    @Override
    public Map<UUID, Double> filter(List<TraderSnapshot> sellers, ITradableItem item, int requestedAmount,
            TradeType preferredType, TradeTaxCalculator taxCalculator) {
        Map<UUID, Double> filtered = new HashMap<>();
        for (TraderSnapshot seller : sellers) {
            int availableCount = seller.getAvailableItemCount();
            if (availableCount <= 0) {
                continue;
            }
            double normalizedScore =
                    availableCount >= requestedAmount ? 1d : (double) availableCount / requestedAmount;
            if (normalizedScore * 100 >= getThreshold()) {
                filtered.put(seller.getTraderId(), normalizedScore);
            }
        }
        return filtered;
    }

    @Override
    public Map<UUID, Double> normalizedScores(List<TraderSnapshot> sellers, ITradableItem item,
            int requestedAmount, TradeType preferredType, TradeTaxCalculator taxCalculator) {
        return this.filter(sellers, item, requestedAmount, preferredType, taxCalculator);
    }
}
