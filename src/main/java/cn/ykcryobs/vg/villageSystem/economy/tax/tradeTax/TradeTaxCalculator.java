package cn.ykcryobs.vg.villageSystem.economy.tax.tradeTax;

import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.villageSystem.economy.tradeRoute.TradeRoute;
import cn.ykcryobs.vg.villageSystem.economy.tradeRoute.TradeRouteManager;
import cn.ykcryobs.vg.villageSystem.economy.tradeRoute.VillageTradeRelation;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import java.util.UUID;

/**
 * Trade tax calculator, calculates tax rates and distance penalties for inter-village trades. Now integrates
 * with TradeRouteManager for comprehensive tax and shipping calculations.
 *
 * @author llykff
 */
public class TradeTaxCalculator implements ITradeTaxCalculator {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final TradeRouteManager tradeRouteManager;

    public TradeTaxCalculator() {
        this.tradeRouteManager = new TradeRouteManager();
    }

    public TradeRouteManager getTradeRouteManager() {
        return this.tradeRouteManager;
    }

    @Override
    public float calculateTaxRate(UUID buyerVillageId, UUID sellerVillageId, Item item, float basePrice) {
        if (buyerVillageId.equals(sellerVillageId)) {
            return 0f;
        }

        return this.tradeRouteManager.calculateTotalTaxRate(buyerVillageId, sellerVillageId, item, basePrice);
    }

    @Override
    public float calculateDistancePenalty(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return 0f;
        }

        float distance = this.getTradeRouteDistance(villageA, villageB);
        if (distance < 0) {
            distance = this.calculateStraightLineDistance(villageA, villageB);
        }

        float penaltyPerBlock = (float) ServerConfig.distancePenaltyPerBlock.get().doubleValue();
        return distance * penaltyPerBlock;
    }

    @Override
    public boolean hasTradeRoute(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return true;
        }

        return this.tradeRouteManager.hasRoute(villageA, villageB);
    }

    @Override
    public float getTradeRouteDistance(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return 0f;
        }

        TradeRoute route = this.tradeRouteManager.getRoute(villageA, villageB);
        if (route != null && route.isActive()) {
            return route.getPathLength();
        }

        return -1f;
    }

    private float calculateStraightLineDistance(UUID villageA, UUID villageB) {
        return this.tradeRouteManager.calculateDistance(villageA, villageB);
    }

    public boolean createTradeRoute(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return false;
        }

        if (this.hasTradeRoute(villageA, villageB)) {
            LOGGER.debug("Trade route already exists between {} and {}", villageA, villageB);
            return false;
        }

        TradeRoute route = this.tradeRouteManager.createRoute(villageA, villageB);
        return route != null;
    }

    public boolean removeTradeRoute(UUID villageA, UUID villageB) {
        return this.tradeRouteManager.removeRoute(villageA, villageB);
    }

    public VillageTradeRelation getOrCreateRelation(UUID villageA, UUID villageB) {
        return this.tradeRouteManager.getOrCreateRelation(villageA, villageB);
    }

    public VillageTradeRelation getRelation(UUID villageA, UUID villageB) {
        return this.tradeRouteManager.getRelation(villageA, villageB);
    }

    public float calculateShippingCost(UUID villageA, UUID villageB, int itemCount) {
        return this.tradeRouteManager.calculateShippingCost(villageA, villageB, itemCount);
    }

    public int calculateTransportTime(UUID villageA, UUID villageB) {
        return this.tradeRouteManager.calculateTransportTime(villageA, villageB);
    }

    public void recordTrade(UUID villageA, UUID villageB, long volume) {
        this.tradeRouteManager.recordTrade(villageA, villageB, volume);
    }

    public void improveRelation(UUID villageA, UUID villageB, int amount) {
        this.tradeRouteManager.improveRelation(villageA, villageB, amount);
    }

    public void worsenRelation(UUID villageA, UUID villageB, int amount) {
        this.tradeRouteManager.worsenRelation(villageA, villageB, amount);
    }

    public void tick() {
        this.tradeRouteManager.tick();
    }

    public CompoundTag serializeNBT() {
        return this.tradeRouteManager.serializeNBT();
    }

    public void deserializeNBT(CompoundTag tag) {
        this.tradeRouteManager.deserializeNBT(tag);
    }

    @Deprecated
    public CompoundTag serializeNBTLegacy() {
        CompoundTag tag = new CompoundTag();
        ListTag routesList = new ListTag();
        tag.put("tradeRoutes", routesList);
        return tag;
    }

    @Deprecated
    public void deserializeNBTLegacy(CompoundTag tag) {
        if (tag.contains("tradeRoutes", Tag.TAG_LIST)) {
            LOGGER.info("Migrating legacy trade routes to new system...");
            ListTag routesList = tag.getList("tradeRoutes", Tag.TAG_COMPOUND);

            for (Tag t : routesList) {
                CompoundTag routeTag = (CompoundTag) t;
                UUID fromVillage = routeTag.getUUID("fromVillage");
                UUID toVillage = routeTag.getUUID("toVillage");

                this.tradeRouteManager.getOrCreateRelation(fromVillage, toVillage);
                this.tradeRouteManager.createRoute(fromVillage, toVillage);
            }
        }
    }
}
