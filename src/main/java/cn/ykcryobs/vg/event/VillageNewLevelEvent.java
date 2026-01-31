package cn.ykcryobs.vg.event;

import java.util.UUID;

/**
 * 村庄等级变更事件
 *
 * @author llykff
 */
public class VillageNewLevelEvent extends VillageEvent {

    private final int level;

    public VillageNewLevelEvent(UUID villageID, int level) {
        super(villageID);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
