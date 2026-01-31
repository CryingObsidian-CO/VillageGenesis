package cn.ykcryobs.vg.villageSystem.economy;

import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
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
     * 检查是否有足够的物品
     *
     * @param itemStack 物品栈
     * @return 是否有足够的物品
     */
    boolean hasEnough(ItemStack itemStack);

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
     * 获取交易协商等级
     *
     * @return 交易协商等级
     */
    default int getNegotiationLevel() {
        return 1;
    }

    /**
     * 物品的偏好系数列表
     *
     * @return 物品的偏好系数列表
     */
    Map<Item, Float> getPreferenceMultiplier();

    /**
     * 获取支持的支付方式
     *
     * @return 支持的支付方式
     */
    Set<PaymentMethod> getSupportedPaymentMethods();
}
