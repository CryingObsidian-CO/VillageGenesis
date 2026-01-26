package cn.ykcryobs.vg.villageSystem.currency.transaction;

import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.currency.ITrader;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;


/**
 * 询价单
 *
 * @author llykff
 */
public record Inquiry(ITrader buyer, Item targetItem, int targetAmount) {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 创建询价单
     *
     * @param buyer        买家
     * @param targetItem   目标物品
     * @param targetAmount 目标数量
     */
    public Inquiry(ITrader buyer, Item targetItem, int targetAmount) {
        if (!(targetItem instanceof ITradableItem)) {
            LOGGER.error("Target item {} is not tradable", targetItem);
        }
        this.buyer = buyer;
        this.targetItem = targetItem;
        this.targetAmount = targetAmount;
        LOGGER.debug("Created inquiry: buyer={}, targetItem={}, targetAmount={}", buyer.getTraderId(),
                targetItem, targetAmount);
    }

    @Override
    public @NotNull String toString() {
        return "Inquiry{buyer=" + buyer.getTraderId() + ", targetItem=" + targetItem + ", targetAmount="
                + targetAmount + "}";
    }
}
