package cn.ykcryobs.vg.event;

import java.util.UUID;

public class OrderChangedEvent extends InterVillageEvent {

    private final UUID orderId;
    private final OrderStatus status;

    public OrderChangedEvent(UUID villageA, UUID villageB, UUID orderId, OrderStatus status) {
        super(villageA, villageB);
        this.orderId = orderId;
        this.status = status;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCELLED,
        FAILED
    }
}
