package cn.ykcryobs.vg.villageSystem.economy.trader;

import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.economy.VillageEconomyData;
import cn.ykcryobs.vg.villageSystem.economy.payment.PaymentMethod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 交易者接口 用于表示交易中的买方或卖方，包含唯一标识和所属村庄信息
 *
 * @author llykff
 */
public interface ITrader {

    /**
     * 获取唯一标识（比如玩家UUID、村民ID、村庄ID、建筑ID）
     *
     * @return 唯一标识（比如玩家UUID、村民ID、村庄ID、建筑ID）
     */
    UUID getTraderId();

    /**
     * 所属村庄ID
     *
     * @return 所属村庄ID（如果有）
     */
    Optional<UUID> getVillageIdIfHasVillage();

    /**
     * 获取村庄经济数据
     *
     * @return 所属村庄的经济数据（如果有）
     */
    default Optional<VillageEconomyData> getEconomyData() {
        return getVillageIdIfHasVillage().flatMap(VillageManager::getVillageData)
                .map(VillageData::getVillageEconomyData);
    }

    /**
     * 获取物品的单价工时
     *
     * @param item 物品
     * @return 物品的单价工时
     */
    float getUnitWorkPoint(ITradableItem item);


    /**
     * 获取指定物品的可用数量
     *
     * @param item 物品
     * @return 可用数量
     */
    int getAvailableItemCount(ITradableItem item);

    /**
     * 检查是否有足够的物品
     *
     * @param itemStack 物品栈
     * @return 是否有足够的物品
     */
    default boolean hasEnough(ItemStack itemStack) {
        ITradableItem tradableItem = (ITradableItem) itemStack.getItem();
        if (tradableItem.isTradable()) {
            return getAvailableItemCount(tradableItem) >= itemStack.getCount();
        }
        return false;
    }

    /**
     * 扣除物品
     *
     * @param itemStack 物品栈
     */
    void removeItem(ItemStack itemStack);

    /**
     * 增加物品
     *
     * @param itemStack 物品栈
     */
    void addItem(ItemStack itemStack);

    /**
     * 获取支持的支付方式
     *
     * @return 支持的支付方式
     */
    Set<PaymentMethod> getSupportedPaymentMethods();

    /**
     * 获取物品偏好修正系数
     *
     * @return 物品偏好修正系数
     */
    Map<Item, Float> getPreferenceMultiplier();

    /**
     * 获取交易者的协商能力等级
     *
     * @return 协商能力等级
     */
    default int getNegotiationLevel() {
        return 0;
    }

    /**
     * 创建交易者快照
     *
     * @param item 交易物品
     * @return 交易者快照
     */
    default TraderSnapshot createSnapshot(ITradableItem item) {
        return TraderSnapshot.fromTrader(this, item);
    }

}