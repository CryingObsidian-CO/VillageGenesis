package cn.ykcryobs.vg.event;

import java.util.UUID;

public class TradeRouteChangedEvent extends InterVillageEvent {

    private final boolean active;
    private final UUID tradeRouteId;

    public TradeRouteChangedEvent(UUID villageA, UUID villageB, UUID tradeRouteId, boolean active) {
        super(villageA, villageB);
        this.tradeRouteId = tradeRouteId;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public UUID getTradeRouteId() {
        return tradeRouteId;
    }
}
