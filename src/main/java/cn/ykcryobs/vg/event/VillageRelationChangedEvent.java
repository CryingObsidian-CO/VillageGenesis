package cn.ykcryobs.vg.event;

import cn.ykcryobs.vg.villageSystem.interVillage.VillageRelation;

import java.util.UUID;

public class VillageRelationChangedEvent extends InterVillageEvent {

    private final VillageRelation.RelationStatus oldStatus;
    private final VillageRelation.RelationStatus newStatus;

    public VillageRelationChangedEvent(UUID villageA, UUID villageB,
            VillageRelation.RelationStatus oldStatus, VillageRelation.RelationStatus newStatus) {
        super(villageA, villageB);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public VillageRelation.RelationStatus getOldStatus() {
        return oldStatus;
    }

    public VillageRelation.RelationStatus getNewStatus() {
        return newStatus;
    }
}
