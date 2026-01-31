package cn.ykcryobs.vg.villagerEnhance.profession;

import cn.ykcryobs.vg.villagerEnhance.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;

/**
 * 职业增强接口
 *
 * @author llykff
 */
public interface IProfessionEnhancement {

    /**
     * 获取关联的原版职业
     *
     * @return VillagerProfession 原版职业实例
     */
    VillagerProfession getProfession();

    /**
     * 添加默认偏好 使用 VillageData#addPreference()
     *
     * @see VillagerData#addPreference(Item, float)
     */
    void addDefaultPreference();

}
