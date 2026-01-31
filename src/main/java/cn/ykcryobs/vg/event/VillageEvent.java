package cn.ykcryobs.vg.event;

import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.UUID;

/**
 * 村庄事件基类
 *
 * @author llykff
 */
public abstract class VillageEvent extends Event implements IModBusEvent {

    private final UUID villageID;

    public VillageEvent(UUID villageID) {
        this.villageID = villageID;
    }

    public UUID getVillageID() {
        return villageID;
    }

    // 事件有具体的村庄数据触发，所以能触发，则 villageData 一定不为 null
    public VillageData getVillageData() {
        return VillageManager.getVillageData(villageID).orElse(null);
    }

}
