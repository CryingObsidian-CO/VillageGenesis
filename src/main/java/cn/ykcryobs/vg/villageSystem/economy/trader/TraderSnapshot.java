package cn.ykcryobs.vg.villageSystem.economy.trader;

import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.economy.payment.PaymentMethod;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 交易者快照类，用于存储交易者数据的不可变快照，确保线程安全
 *
 * @author llykff
 */
public final class TraderSnapshot {

    private final UUID traderId;
    private final Optional<UUID> villageId;
    private final int availableItemCount;
    private final float unitWorkPoint;
    private final Set<PaymentMethod> supportedPaymentMethods;
    private final Map<Item, Float> preferenceMultiplier;
    private final int negotiationLevel;

    /**
     * 构造函数
     *
     * @param traderId                交易者ID
     * @param villageId               村庄ID
     * @param availableItemCount      可用物品数量
     * @param unitWorkPoint           工时单价
     * @param supportedPaymentMethods 支持的支付方式
     * @param preferenceMultiplier    物品偏好修正系数
     * @param negotiationLevel        协商能力等级
     */
    private TraderSnapshot(UUID traderId, Optional<UUID> villageId, int availableItemCount,
            float unitWorkPoint, Set<PaymentMethod> supportedPaymentMethods,
            Map<Item, Float> preferenceMultiplier, int negotiationLevel) {
        this.traderId = Objects.requireNonNull(traderId, "traderId must not be null");
        this.villageId = Objects.requireNonNull(villageId, "villageId must not be null");
        this.availableItemCount = availableItemCount;
        this.unitWorkPoint = unitWorkPoint;
        this.supportedPaymentMethods =
                Collections.unmodifiableSet(Objects.requireNonNull(supportedPaymentMethods));
        this.preferenceMultiplier =
                Collections.unmodifiableMap(Objects.requireNonNull(preferenceMultiplier));
        this.negotiationLevel = negotiationLevel;
    }

    /**
     * 从 ITrader 创建快照
     *
     * @param trader 交易者
     * @param item   交易物品
     * @return 交易者快照
     */
    public static TraderSnapshot fromTrader(ITrader trader, ITradableItem item) {
        Objects.requireNonNull(trader, "trader must not be null");
        Objects.requireNonNull(item, "item must not be null");

        return new TraderSnapshot(trader.getTraderId(), trader.getVillageIdIfHasVillage(),
                trader.getAvailableItemCount(item), trader.getUnitWorkPoint(item),
                trader.getSupportedPaymentMethods(), trader.getPreferenceMultiplier(),
                trader.getNegotiationLevel());
    }

    /**
     * 获取交易者ID
     *
     * @return 交易者ID
     */
    public UUID getTraderId() {
        return this.traderId;
    }

    /**
     * 获取村庄ID
     *
     * @return 村庄ID（如果有）
     */
    public Optional<UUID> getVillageId() {
        return this.villageId;
    }

    /**
     * 获取可用物品数量
     *
     * @return 可用物品数量
     */
    public int getAvailableItemCount() {
        return this.availableItemCount;
    }

    /**
     * 获取工时单价
     *
     * @return 工时单价
     */
    public float getUnitWorkPoint() {
        return this.unitWorkPoint;
    }

    /**
     * 获取支持的支付方式
     *
     * @return 支持的支付方式
     */
    public Set<PaymentMethod> getSupportedPaymentMethods() {
        return this.supportedPaymentMethods;
    }

    /**
     * 获取物品偏好修正系数
     *
     * @return 物品偏好修正系数
     */
    public Map<Item, Float> getPreferenceMultiplier() {
        return this.preferenceMultiplier;
    }

    /**
     * 获取协商能力等级
     *
     * @return 协商能力等级
     */
    public int getNegotiationLevel() {
        return this.negotiationLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TraderSnapshot that = (TraderSnapshot) o;
        return this.traderId.equals(that.traderId);
    }

    @Override
    public int hashCode() {
        return this.traderId.hashCode();
    }

    @Override
    public String toString() {
        return "TraderSnapshot{" + "traderId=" + this.traderId + ", villageId=" + this.villageId
                + ", availableItemCount=" + this.availableItemCount + ", unitWorkPoint="
                + this.unitWorkPoint + '}';
    }
}
