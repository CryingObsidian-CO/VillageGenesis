package cn.ykcryobs.vg.villagerEnhance;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

/**
 * 村民数据
 *
 * @author llykff
 */
public class VillagerData implements INBTSerializable<CompoundTag> {

    private UUID villageId;

    private int happiness = 20; // 幸福度：影响村民的幸福感
    private int loyalty = 20; // 忠诚度：影响村民迁移/叛逃的概率
    private int adaptability = 20; // 适应度：影响村民适应新工作的能力
    private int curiosity = 20; // 好奇心：影响村民探索的欲望

    private int fatigue = 0; // 疲劳：过高会降低其他属性表现
    private int stress = 0; // 压力：过高会降低其他属性表现


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("villageID", villageId);
        tag.putInt("happiness", happiness);
        tag.putInt("loyalty", loyalty);
        tag.putInt("adaptability", adaptability);
        tag.putInt("curiosity", curiosity);

        tag.putInt("fatigue", fatigue);
        tag.putInt("stress", stress);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        villageId = nbt.getUUID("villageID");
        happiness = nbt.getInt("happiness");
        loyalty = nbt.getInt("loyalty");
        adaptability = nbt.getInt("adaptability");
        curiosity = nbt.getInt("curiosity");

        fatigue = nbt.getInt("fatigue");
        stress = nbt.getInt("stress");
    }

    /**
     * 绑定村民到村庄
     *
     * @param villageId 村庄ID
     */
    public void bindVillage(UUID villageId) {
        this.villageId = villageId;
    }

    /**
     * 获取绑定的村庄ID
     *
     * @return 村庄ID
     */
    public UUID getVillageId() {
        return villageId;
    }
}