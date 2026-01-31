package cn.ykcryobs.vg.event;

import cn.ykcryobs.vg.villageSystem.VillageData;

import java.util.UUID;

/**
 * 村庄进化阶段变更事件
 *
 * @author llykff
 */
public class VillageNewStageEvent extends VillageEvent {

    private final VillageData.VillageEvolutionStage stage;

    public VillageNewStageEvent(UUID villageID, VillageData.VillageEvolutionStage stage) {
        super(villageID);
        this.stage = stage;
    }

    public VillageData.VillageEvolutionStage getStage() {
        return stage;
    }
}
