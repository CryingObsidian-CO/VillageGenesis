package cn.ykcryobs.vg.event;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.UUID;

public abstract class InterVillageEvent extends Event implements IModBusEvent {

    private final UUID villageA;
    private final UUID villageB;

    public InterVillageEvent(UUID villageA, UUID villageB) {
        this.villageA = villageA;
        this.villageB = villageB;
    }

    public UUID getVillageA() {
        return villageA;
    }

    public UUID getVillageB() {
        return villageB;
    }
}
