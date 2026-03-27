package cn.ykcryobs.vg.villageSystem.economy.tradeRoute;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/**
 * 贸易路线类，代表村庄之间的特定贸易路径。包含路径长度、基础运输时间、基础单位运输成本等。
 *
 * @author llykff
 */
// TODO 贸易路线后续考虑增加安全等级，容量等属性
public class TradeRoute {

    private final UUID routeId;
    private final UUID fromVillage;
    private final UUID toVillage;
    private float pathLength;
    private float baseTransportTime;
    private float baseUnitShippingCost;
    private RouteType routeType;
    private boolean active;

    public TradeRoute(UUID routeId, UUID fromVillage, UUID toVillage, float pathLength,
            float baseTransportTime, float baseUnitShippingCost, RouteType routeType, boolean active) {
        this.routeId = routeId;
        this.fromVillage = fromVillage;
        this.toVillage = toVillage;
        this.pathLength = pathLength;
        this.baseTransportTime = baseTransportTime;
        this.baseUnitShippingCost = baseUnitShippingCost;
        this.routeType = routeType;
        this.active = active;
    }

    public TradeRoute(UUID fromVillage, UUID toVillage, float pathLength, float baseTransportTime,
            float baseUnitShippingCost, RouteType routeType) {
        this(UUID.randomUUID(), fromVillage, toVillage, pathLength, baseTransportTime, baseUnitShippingCost,
                routeType, true);
    }

    /**
     * 简化构造函数，用于自动创建基础贸易路线
     *
     * @param routeId     路线ID
     * @param fromVillage 起始村庄
     * @param toVillage   目标村庄
     * @param distance    路径长度
     */
    public TradeRoute(UUID routeId, UUID fromVillage, UUID toVillage, float distance) {
        this(routeId, fromVillage, toVillage, distance, distance * 2, 0.01f, RouteType.LAND, true);
    }

    /**
     * 自定义贸易路线构造函数
     *
     * @param routeId             路线ID
     * @param fromVillage         起始村庄
     * @param toVillage           目标村庄
     * @param pathLength          路径长度
     * @param baseTransportTime   基础运输时间
     * @param baseUnitShippingCost 基础单位运输成本
     * @param routeType           路线类型
     */
    public TradeRoute(UUID routeId, UUID fromVillage, UUID toVillage, float pathLength, int baseTransportTime,
            float baseUnitShippingCost, RouteType routeType) {
        this(routeId, fromVillage, toVillage, pathLength, (float) baseTransportTime, baseUnitShippingCost,
                routeType, true);
    }


    public static TradeRoute deserializeNBT(CompoundTag tag) {
        UUID routeId = tag.getUUID("routeId");
        UUID fromVillage = tag.getUUID("fromVillage");
        UUID toVillage = tag.getUUID("toVillage");
        float pathLength = tag.getFloat("pathLength");
        int baseTransportTime = tag.getInt("baseTransportTime");
        float baseUnitShippingCost = tag.getFloat("baseUnitShippingCost");
        RouteType routeType = RouteType.valueOf(tag.getString("routeType"));
        boolean active = tag.getBoolean("active");

        return new TradeRoute(routeId, fromVillage, toVillage, pathLength, baseTransportTime,
                baseUnitShippingCost, routeType, active);
    }

    public UUID getRouteId() {
        return this.routeId;
    }

    public UUID getFromVillage() {
        return this.fromVillage;
    }

    public UUID getToVillage() {
        return this.toVillage;
    }

    public float getPathLength() {
        return this.pathLength;
    }

    public void setPathLength(float pathLength) {
        this.pathLength = pathLength;
    }

    public void setBaseTransportTime(int baseTransportTime) {
        this.baseTransportTime = baseTransportTime;
    }

    public float getBaseUnitShippingCost() {
        return this.baseUnitShippingCost;
    }

    public void setBaseUnitShippingCost(float baseUnitShippingCost) {
        this.baseUnitShippingCost = baseUnitShippingCost;
    }

    public RouteType getRouteType() {
        return this.routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setInactive() {
        this.active = false;
    }

    public void setActive() {
        this.active = true;
    }

    public boolean connectsVillage(UUID villageId) {
        return this.fromVillage.equals(villageId) || this.toVillage.equals(villageId);
    }

    public boolean connectsBoth(UUID villageA, UUID villageB) {
        return (this.fromVillage.equals(villageA) && this.toVillage.equals(villageB)) || (
                this.fromVillage.equals(villageB) && this.toVillage.equals(villageA));
    }

    public float calculateTotalShippingCost(int itemCount) {
        float distanceCost = this.pathLength * this.baseUnitShippingCost;
        float itemCost = distanceCost * itemCount;
        return this.routeType.calculateShippingCost(itemCost);
    }

    public int calculateTransportTime() {
        return (int) this.routeType.calculateTransportTime(this.baseTransportTime);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("routeId", this.routeId);
        tag.putUUID("fromVillage", this.fromVillage);
        tag.putUUID("toVillage", this.toVillage);
        tag.putFloat("pathLength", this.pathLength);
        tag.putFloat("baseTransportTime", this.baseTransportTime);
        tag.putFloat("baseUnitShippingCost", this.baseUnitShippingCost);
        tag.putString("routeType", this.routeType.name());
        tag.putBoolean("active", this.active);
        return tag;
    }
}
