package cn.ykcryobs.vg.mixin;

import cn.ykcryobs.vg.init.ModAttachment;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villagerEnhance.VillagerData;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * @author llykff
 */
@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    @Unique
    private static final Logger villageGenesis$LOGGER = LogUtils.getLogger();

    @Unique
    private final Villager villageGenesis$villager = (Villager) (Object) this;

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/npc/Villager;assignProfessionWhenSpawned:Z", opcode = Opcodes.PUTFIELD))
    public void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType spawnType, SpawnGroupData spawnGroupData,
            CallbackInfoReturnable<SpawnGroupData> cir) {

        // 只在服务端处理村民村庄归属检测
        if (level.getLevel().isClientSide()) {
            return;
        }

        BlockPos villagerPos = this.villageGenesis$villager.blockPosition();

        // 检查村民属于哪个村庄
        Optional<VillageData> belongingVillage = VillageManager.getVillageIfPosInVillage(villagerPos);

        if (belongingVillage.isPresent()) {
            // 村民属于某个村庄
            villageGenesis$LOGGER.info("村民在位置 {} 属于村庄: {} (ID: {})", villagerPos.toString(),
                    belongingVillage.get().getVillageName().getString(),
                    belongingVillage.get().getVillageId());
            belongingVillage.get().addVillager(this.villageGenesis$villager.getUUID());
            // 绑定村民数据到村庄
            VillagerData villagerData = this.villageGenesis$villager.getData(ModAttachment.VILLAGER_DATA);
            villagerData.bindVillage(this.villageGenesis$villager, belongingVillage.get().getVillageId());
        } else {
            // 村民不属于任何村庄
            villageGenesis$LOGGER.debug("村民在位置 {} 不属于任何村庄", villagerPos.toString());
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    public void die(CallbackInfo ci) {

        // 只在服务端处理村民死亡解绑
        if (this.villageGenesis$villager.level().isClientSide()) {
            return;
        }

        // 从所有村庄中移除该村民
        Optional<VillagerData> villagerDataOptional = this.villageGenesis$getVillagerData();
        if (villagerDataOptional.isEmpty()) {
            return;
        }
        VillageManager.getVillageData(villagerDataOptional.get().getVillageId()).ifPresent(villageData -> {
            villageData.removeVillager(this.villageGenesis$villager.getUUID());
        });

    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void reestablishVillagerReference(CallbackInfo ci) {
        if (this.villageGenesis$villager.level().isClientSide()) {
            return;
        }

        // 重建村民引用
        villageGenesis$getVillagerData().ifPresent(villagerData -> {
            villagerData.reestablishVillagerReference(this.villageGenesis$villager.level());
        });
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        // 只在服务端处理状态更新
        if (this.villageGenesis$villager.level().isClientSide()) {
            return;
        }

        // 获取村民数据并更新状态
        villageGenesis$getVillagerData().ifPresent(villagerData1 -> {
            villagerData1.getStateManager().update();
        });
    }

    @Unique
    public Optional<VillagerData> villageGenesis$getVillagerData() {
        if (this.villageGenesis$villager.hasData(ModAttachment.VILLAGER_DATA)) {
            return Optional.of(this.villageGenesis$villager.getData(ModAttachment.VILLAGER_DATA));
        }
        return Optional.empty();
    }
}
