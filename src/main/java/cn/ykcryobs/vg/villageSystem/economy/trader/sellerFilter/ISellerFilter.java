package cn.ykcryobs.vg.villageSystem.economy.trader.sellerFilter;

import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.economy.tax.tradeTax.TradeTaxCalculator;
import cn.ykcryobs.vg.villageSystem.economy.tradeRoute.TradeType;
import cn.ykcryobs.vg.villageSystem.economy.trader.TraderSnapshot;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 卖家筛选器接口，用于可插拔的评分框架。
 *
 * <p><b>线程安全约束：</b>所有实现类必须确保 filter 和 normalizedScores 方法
 * 仅使用传入的快照数据进行计算，不得直接访问任何游戏对象（如 ITrader、Level 等）。 这些方法可能在非主线程上执行，因此必须保证线程安全。</p>
 *
 * @author llykff
 */
public interface ISellerFilter {

    /**
     * 获取筛选器名称
     *
     * @return 筛选器名称
     */
    String getFilterName();

    /**
     * 获取筛选器优先级
     *
     * @return 筛选器优先级
     */
    int getPriority();

    /**
     * 获取筛选器权重
     *
     * @return 筛选器权重
     */
    double getWeight();

    /**
     * 获取筛选器阈值
     *
     * @return 筛选器阈值
     */
    double getThreshold();

    /**
     * 对筛选器评分进行归一化处理，将评分映射到[0, 1]区间，再乘上筛选器权重。
     *
     * <p><b>线程安全：</b>此方法可能在非主线程上执行，实现类必须仅使用快照数据。</p>
     *
     * @param sellers         卖家快照列表
     * @param item            交易物品
     * @param requestedAmount 请求数量
     * @param preferredType   交易类型
     * @param taxCalculator   交易税计算器
     * @return 归一化后的卖家评分映射（卖家ID -> 评分）
     */
    default Map<UUID, Double> normalizedScores(List<TraderSnapshot> sellers, ITradableItem item,
            int requestedAmount, TradeType preferredType, TradeTaxCalculator taxCalculator) {
        Map<UUID, Double> scores = filter(sellers, item, requestedAmount, preferredType, taxCalculator);
        double maxScore = scores.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double minScore = scores.values().stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        if (maxScore == minScore) {
            double score = getWeight() * 0.5d;
            return scores.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> score));
        } else {
            return scores.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                    entry -> getWeight() * (entry.getValue() - minScore) / (maxScore - minScore)));
        }
    }

    /**
     * 筛选卖家并评分。
     *
     * <p><b>线程安全：</b>此方法可能在非主线程上执行，实现类必须仅使用快照数据，
     * 不得直接访问任何游戏对象。</p>
     *
     * @param sellers         卖家快照列表
     * @param item            交易物品
     * @param requestedAmount 请求数量
     * @param preferredType   交易类型
     * @param taxCalculator   交易税计算器
     * @return 筛选后的卖家评分映射（卖家ID -> 评分）
     */
    Map<UUID, Double> filter(List<TraderSnapshot> sellers, ITradableItem item, int requestedAmount,
            TradeType preferredType, TradeTaxCalculator taxCalculator);

    /**
     * 判断筛选器是否启用
     *
     * @param stage VillageEvolutionStage
     * @return 是否启用
     */
    default boolean isEnabled(VillageData.VillageEvolutionStage stage) {
        return true;
    }
}
