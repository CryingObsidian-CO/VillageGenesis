package cn.ykcryobs.vg.event;

import cn.ykcryobs.vg.villageSystem.interVillage.VillageRelation;

public class VillageRelationCreatedEvent extends InterVillageEvent {

    private final VillageRelation relation;

    public VillageRelationCreatedEvent(VillageRelation relation) {
        super(relation.getVillageA(), relation.getVillageB());
        this.relation = relation;
    }

    public VillageRelation getRelation() {
        return relation;
    }
}
