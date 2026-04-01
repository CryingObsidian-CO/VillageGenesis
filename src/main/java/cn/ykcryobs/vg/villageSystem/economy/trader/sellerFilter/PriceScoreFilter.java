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
import java.util.stream.Collectors;

/**
 * 价格过滤器
 *
 * @author llykff
 */
public class PriceScoreFilter implements ISellerFilter {

    @Override
    public String getFilterName() {
        return "PriceScoreFilter";
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public double getWeight() {
        return ServerConfig.priceScoreWeight.get();
    }

    @Override
    public double getThreshold() {
        return ServerConfig.priceScoreThreshold.get();
    }

    @Override
    public Map<UUID, Double> filter(List<TraderSnapshot> sellers, ITradableItem item, int requestedAmount,
            TradeType preferredType, TradeTaxCalculator taxCalculator) {
        double threshold = getThreshold();

        double minUnitWorkPoint =
                sellers.stream().mapToDouble(TraderSnapshot::getUnitWorkPoint).min().orElse(0.0);
        double maxUnitWorkPoint =
                sellers.stream().mapToDouble(TraderSnapshot::getUnitWorkPoint).max().orElse(0.0);

        Map<UUID, Double> filtered = new HashMap<>(sellers.size());
        if (maxUnitWorkPoint == minUnitWorkPoint) {
            filtered.putAll(sellers.stream()
                    .collect(Collectors.toMap(TraderSnapshot::getTraderId, seller -> 0.5)));
            return filtered;
        }

        for (TraderSnapshot seller : sellers) {
            double unitWorkPoint = seller.getUnitWorkPoint();
            double normalizedUnitWorkPoint =
                    (unitWorkPoint - minUnitWorkPoint) / (maxUnitWorkPoint - minUnitWorkPoint);
            if (normalizedUnitWorkPoint * 100 <= threshold) {
                continue;
            }
            filtered.put(seller.getTraderId(), normalizedUnitWorkPoint);
        }

        return filtered;
    }

    @Override
    public Map<UUID, Double> normalizedScores(List<TraderSnapshot> sellers, ITradableItem item,
            int requestedAmount, TradeType preferredType, TradeTaxCalculator taxCalculator) {
        return this.filter(sellers, item, requestedAmount, preferredType, taxCalculator);
    }
}
