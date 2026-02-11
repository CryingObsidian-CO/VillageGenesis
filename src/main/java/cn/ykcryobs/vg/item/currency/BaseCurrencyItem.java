package cn.ykcryobs.vg.item.currency;

import cn.ykcryobs.vg.dataComponents.CurrencyDataComponent;
import cn.ykcryobs.vg.init.ModDataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * 基础货币物品类 所有货币物品的基类
 *
 * @author llykff
 */
public class BaseCurrencyItem extends Item {

    private final CurrencyType currencyType;

    /**
     * 构造函数
     *
     * @param properties   物品属性
     * @param currencyType 货币类型
     */
    public BaseCurrencyItem(Properties properties, CurrencyType currencyType) {
        super(properties);
        this.currencyType = currencyType;
    }

    /**
     * 获取货币类型
     *
     * @return 货币类型
     */
    public CurrencyType getCurrencyType() {
        return this.currencyType;
    }

    /**
     * 获取货币数据组件
     *
     * @param stack 物品栈
     * @return 货币数据组件，如果不存在则返回null
     */
    public CurrencyDataComponent getCurrencyData(ItemStack stack) {
        return stack.get(ModDataComponents.CURRENCY_DATA.get());
    }

    /**
     * 设置货币数据组件
     *
     * @param stack        物品栈
     * @param currencyData 货币数据组件
     */
    public void setCurrencyData(ItemStack stack, CurrencyDataComponent currencyData) {
        stack.set(ModDataComponents.CURRENCY_DATA.get(), currencyData);
    }

    /**
     * 获取发行村庄ID
     *
     * @param stack 物品栈
     * @return 发行村庄ID，如果不存在则返回null
     */
    public UUID getIssuingVillageId(ItemStack stack) {
        CurrencyDataComponent currencyData = this.getCurrencyData(stack);
        return currencyData != null ? currencyData.issuingVillageId() : null;
    }

    /**
     * 获取发行建筑ID
     *
     * @param stack 物品栈
     * @return 发行建筑ID，如果不存在则返回null
     */
    public UUID getIssuingBuildingId(ItemStack stack) {
        CurrencyDataComponent currencyData = this.getCurrencyData(stack);
        return currencyData != null ? currencyData.issuingBuildingId() : null;
    }

    /**
     * 设置发行信息
     *
     * @param stack             物品栈
     * @param issuingVillageId  发行村庄ID
     * @param issuingBuildingId 发行建筑ID
     */
    public void setIssuingInfo(ItemStack stack, UUID issuingVillageId, UUID issuingBuildingId) {
        this.setCurrencyData(stack, new CurrencyDataComponent(issuingVillageId, issuingBuildingId));
    }
}