package cn.ykcryobs.vg.villagerEnhance.profession;

import cn.ykcryobs.vg.VillageGenesis;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

/**
 * @author llykff
 */
public class WarehouseManagerProfession {

    public static final ResourceKey<PoiType> WAREHOUSE_POI_KEY = ResourceKey.create(
            Registries.POINT_OF_INTEREST_TYPE,
            ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID, "warehouse"));

    public static final ResourceKey<VillagerProfession> WAREHOUSE_MANAGER_KEY = ResourceKey.create(
            Registries.VILLAGER_PROFESSION,
            ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID, "warehouse_manager")
    );

    public static PoiType createWarehousePoi() {
        // 获取所有匹配的方块状态
        Set<BlockState> matchingStates = ImmutableSet.<BlockState>builder()
                .addAll(Blocks.CHEST.getStateDefinition().getPossibleStates())
                .addAll(Blocks.TRAPPED_CHEST.getStateDefinition().getPossibleStates())
                .addAll(Blocks.BARREL.getStateDefinition().getPossibleStates())
                .build();

        // 创建并返回POI类型
        return new PoiType(matchingStates, 1, 1);
    }

}
