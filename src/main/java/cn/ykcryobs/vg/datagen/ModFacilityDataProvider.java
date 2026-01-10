package cn.ykcryobs.vg.datagen;

import cn.ykcryobs.vg.datagen.provider.FacilityDataProvider;
import net.minecraft.data.PackOutput;

/**
 * 设施数据提供器，用于生成村庄设施的相关数据
 *
 * @author llykff
 */
public class ModFacilityDataProvider extends FacilityDataProvider {

    public ModFacilityDataProvider(PackOutput output, String modid) {
        super(output, modid);
    }

    @Override
    protected void addFacilityType() {
        add(createVillageCenter("village_center", "village_center", 0, 5, 0, 200));
    }
}
