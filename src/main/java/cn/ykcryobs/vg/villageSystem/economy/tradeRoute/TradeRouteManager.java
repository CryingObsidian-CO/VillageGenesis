package cn.ykcryobs.vg.villageSystem.economy.tradeRoute;

import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Trade route manager, manages all village trade relations and trade routes. Provides creation, query
 * interfaces, and comprehensive tax and shipping cost calculations.
 *
 * @author llykff
 */
public class TradeRouteManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<String, VillageTradeRelation> relations;
    private final Map<String, TradeRoute> routes;

    public TradeRouteManager() {
        this.relations = new HashMap<>();
        this.routes = new HashMap<>();
    }

    private String createRelationKey(UUID villageA, UUID villageB) {
        UUID first = villageA.compareTo(villageB) < 0 ? villageA : villageB;
        UUID second = villageA.compareTo(villageB) < 0 ? villageB : villageA;
        return first.toString() + "_" + second.toString();
    }

    public VillageTradeRelation getOrCreateRelation(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return null;
        }

        String key = this.createRelationKey(villageA, villageB);
        VillageTradeRelation relation = this.relations.get(key);

        if (relation == null) {
            relation = new VillageTradeRelation(villageA, villageB);
            this.relations.put(key, relation);
            LOGGER.info("Created new trade relation between {} and {} (First Contact)", villageA, villageB);
        }

        return relation;
    }

    public VillageTradeRelation getRelation(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return null;
        }
        String key = this.createRelationKey(villageA, villageB);
        return this.relations.get(key);
    }

    public boolean hasRelation(UUID villageA, UUID villageB) {
        return this.getRelation(villageA, villageB) != null;
    }

    public TradeRoute getRoute(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return null;
        }
        String key = this.createRelationKey(villageA, villageB);
        return this.routes.get(key);
    }

    public boolean hasRoute(UUID villageA, UUID villageB) {
        TradeRoute route = this.getRoute(villageA, villageB);
        return route != null && route.isActive();
    }

    public TradeRoute createRoute(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return null;
        }

        String key = this.createRelationKey(villageA, villageB);

        if (this.routes.containsKey(key)) {
            LOGGER.debug("Trade route already exists between {} and {}", villageA, villageB);
            return this.routes.get(key);
        }

        VillageTradeRelation relation = this.getOrCreateRelation(villageA, villageB);
        if (relation == null) {
            return null;
        }

        float distance = this.calculateDistance(villageA, villageB);
        TradeRoute route = new TradeRoute(relation.getRelationId(), villageA, villageB, distance);
        this.routes.put(key, route);

        LOGGER.info("Created trade route between {} and {} with distance {}", villageA, villageB, distance);
        return route;
    }

    public TradeRoute createRoute(UUID villageA, UUID villageB, float pathLength, int baseTransportTime,
            float baseUnitShippingCost, RouteType routeType) {
        if (villageA.equals(villageB)) {
            return null;
        }

        String key = this.createRelationKey(villageA, villageB);

        VillageTradeRelation relation = this.getOrCreateRelation(villageA, villageB);
        if (relation == null) {
            return null;
        }

        TradeRoute route = new TradeRoute(relation.getRelationId(), villageA, villageB, pathLength,
                baseTransportTime,
                baseUnitShippingCost, routeType);
        this.routes.put(key, route);

        LOGGER.info("Created custom trade route between {} and {}", villageA, villageB);
        return route;
    }

    public boolean removeRoute(UUID villageA, UUID villageB) {
        String key = this.createRelationKey(villageA, villageB);
        TradeRoute removed = this.routes.remove(key);
        if (removed != null) {
            LOGGER.info("Removed trade route between {} and {}", villageA, villageB);
            return true;
        }
        return false;
    }

    public float calculateDistance(UUID villageA, UUID villageB) {
        Optional<VillageData> dataA = VillageManager.getVillageData(villageA);
        Optional<VillageData> dataB = VillageManager.getVillageData(villageB);

        if (dataA.isEmpty() || dataB.isEmpty()) {
            return 0f;
        }

        BlockPos posA = dataA.get().getCenterPos();
        BlockPos posB = dataB.get().getCenterPos();

        double dx = posA.getX() - posB.getX();
        double dz = posA.getZ() - posB.getZ();

        return (float) Math.sqrt(dx * dx + dz * dz);
    }

    public float calculateTotalTaxRate(UUID buyerVillageId, UUID sellerVillageId, Item item,
            float basePrice) {
        if (buyerVillageId.equals(sellerVillageId)) {
            return 0f;
        }

        VillageTradeRelation relation = this.getRelation(buyerVillageId, sellerVillageId);

        if (relation != null && !relation.canTrade()) {
            return 1.0f;
        }

        float baseTaxRate = (float) ServerConfig.interVillageTaxRate.get().doubleValue();

        if (relation != null) {
            baseTaxRate += relation.calculateTaxModifier();
        }

        if (this.hasRoute(buyerVillageId, sellerVillageId)) {
            TradeRoute route = this.getRoute(buyerVillageId, sellerVillageId);
            float distance = route.getPathLength();
            float distanceFactor = Math.min(distance / 1000f, 0.05f);
            baseTaxRate += distanceFactor;
        }

        Optional<VillageData> buyerData = VillageManager.getVillageData(buyerVillageId);
        Optional<VillageData> sellerData = VillageManager.getVillageData(sellerVillageId);

        if (buyerData.isPresent() && sellerData.isPresent()) {
            int buyerLevel = buyerData.get().getVillageLevel();
            int sellerLevel = sellerData.get().getVillageLevel();
            float levelDiff = Math.abs(buyerLevel - sellerLevel);
            float levelFactor = levelDiff * 0.01f;
            baseTaxRate += levelFactor;
        }

        return Math.max(0f, Math.min(baseTaxRate, 0.5f));
    }

    public float calculateShippingCost(UUID villageA, UUID villageB, int itemCount) {
        TradeRoute route = this.getRoute(villageA, villageB);
        if (route == null) {
            float distance = this.calculateDistance(villageA, villageB);
            return distance * 0.01f * itemCount;
        }
        return route.calculateTotalShippingCost(itemCount);
    }

    public int calculateTransportTime(UUID villageA, UUID villageB) {
        TradeRoute route = this.getRoute(villageA, villageB);
        if (route == null) {
            float distance = this.calculateDistance(villageA, villageB);
            return (int) (distance * 2);
        }
        return route.calculateTransportTime();
    }

    public void recordTrade(UUID villageA, UUID villageB, long volume) {
        VillageTradeRelation relation = this.getOrCreateRelation(villageA, villageB);
        if (relation != null) {
            relation.recordTrade(volume);
        }
    }

    public void improveRelation(UUID villageA, UUID villageB, int amount) {
        VillageTradeRelation relation = this.getRelation(villageA, villageB);
        if (relation != null) {
            relation.improveRelation(amount);
        }
    }

    public void worsenRelation(UUID villageA, UUID villageB, int amount) {
        VillageTradeRelation relation = this.getRelation(villageA, villageB);
        if (relation != null) {
            relation.worsenRelation(amount);
        }
    }

    public void tick() {
        for (VillageTradeRelation relation : this.relations.values()) {
            relation.tickDecay();
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag relationsList = new ListTag();
        for (VillageTradeRelation relation : this.relations.values()) {
            relationsList.add(relation.serializeNBT());
        }
        tag.put("relations", relationsList);

        ListTag routesList = new ListTag();
        for (TradeRoute route : this.routes.values()) {
            routesList.add(route.serializeNBT());
        }
        tag.put("routes", routesList);

        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.relations.clear();
        this.routes.clear();

        if (tag.contains("relations", Tag.TAG_LIST)) {
            ListTag relationsList = tag.getList("relations", Tag.TAG_COMPOUND);
            for (Tag t : relationsList) {
                CompoundTag relationTag = (CompoundTag) t;
                VillageTradeRelation relation = VillageTradeRelation.deserializeNBT(relationTag);
                String key = this.createRelationKey(relation.getVillageA(), relation.getVillageB());
                this.relations.put(key, relation);
            }
        }

        if (tag.contains("routes", Tag.TAG_LIST)) {
            ListTag routesList = tag.getList("routes", Tag.TAG_COMPOUND);
            for (Tag t : routesList) {
                CompoundTag routeTag = (CompoundTag) t;
                TradeRoute route = TradeRoute.deserializeNBT(routeTag);
                String key = this.createRelationKey(route.getFromVillage(), route.getToVillage());
                this.routes.put(key, route);
            }
        }

        LOGGER.info("Loaded {} trade relations and {} trade routes", this.relations.size(),
                this.routes.size());
    }

    public int getRelationCount() {
        return this.relations.size();
    }

    public int getRouteCount() {
        return this.routes.size();
    }
}
