package cn.ykcryobs.vg.villagerEnhance;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * @author llykff
 */
public class VillagerData implements INBTSerializable<CompoundTag> {

    private int happiness = 20; // 幸福度：影响村民的幸福感
    private int loyalty = 20; // 忠诚度：影响村民迁移/叛逃的概率
    private int adaptability = 20; // 适应度：影响村民适应新工作的能力
    private int curiosity = 20; // 好奇心：影响村民探索的欲望

    private int fatigue = 0; // 疲劳：过高会降低其他属性表现
    private int stress = 0; // 压力：过高会降低其他属性表现


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("happiness", happiness);
        tag.putInt("loyalty", loyalty);
        tag.putInt("adaptability", adaptability);
        tag.putInt("curiosity", curiosity);

        tag.putInt("fatigue", fatigue);
        tag.putInt("stress", stress);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        happiness = nbt.getInt("happiness");
        loyalty = nbt.getInt("loyalty");
        adaptability = nbt.getInt("adaptability");
        curiosity = nbt.getInt("curiosity");

        fatigue = nbt.getInt("fatigue");
        stress = nbt.getInt("stress");
    }
}