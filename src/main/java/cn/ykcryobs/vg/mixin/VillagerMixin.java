package cn.ykcryobs.vg.mixin;

import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ServerLevelAccessor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * @author llykff
 */
@Mixin(Villager.class)
public abstract class VillagerMixin {

    @Unique
    private static final Logger villageGenesis$LOGGER = LogUtils.getLogger();

    @Inject(method = "finalizeSpawn", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/npc/Villager;assignProfessionWhenSpawned:Z", opcode = Opcodes.PUTFIELD))
    public void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType spawnType, SpawnGroupData spawnGroupData,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        Villager villager = (Villager) (Object) this;

        // 只在服务端处理村民村庄归属检测
        if (level.getLevel().isClientSide()) {
            return;
        }

        BlockPos villagerPos = villager.blockPosition();

        // 检查村民属于哪个村庄
        Optional<VillageData> belongingVillage = VillageManager.getVillageIfPosInVillage(
                villagerPos);

        if (belongingVillage.isPresent()) {
            // 村民属于某个村庄
            villageGenesis$LOGGER.info("村民在位置 {} 属于村庄: {} (ID: {})",
                    villagerPos.toString(), belongingVillage.get().getVillageName(),
                    belongingVillage.get().getVillageId());
            belongingVillage.get().addVillager(villager.getUUID());
        } else {
            // 村民不属于任何村庄
            villageGenesis$LOGGER.debug("村民在位置 {} 不属于任何村庄", villagerPos.toString());
        }
    }
}
