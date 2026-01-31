package cn.ykcryobs.vg.villagerEnhance;

import java.util.Optional;

/**
 * @author llykff
 */
public interface IVillageMixin {

    /**
     * 获取村庄数据
     *
     * @return 村庄数据
     */
    Optional<VillagerData> villageGenesis$getVillagerData();
}
