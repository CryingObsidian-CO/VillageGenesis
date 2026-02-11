package cn.ykcryobs.vg.mixin;

import cn.ykcryobs.vg.init.ModAttachment;
import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.economy.payment.PaymentMethod;
import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
import cn.ykcryobs.vg.villagerEnhance.IVillagerMixin;
import cn.ykcryobs.vg.villagerEnhance.VillagerData;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author llykff
 */
@Mixin(Villager.class)
@Implements({@Interface(iface = ITrader.class, prefix = "villagerTrader$")})
public abstract class VillagerMixin extends AbstractVillager implements IVillagerMixin, ITrader {

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
        VillagerData villagerData = this.villageGenesis$villager.getData(ModAttachment.VILLAGER_DATA);
        villagerData.initVillagerData(this.villageGenesis$villager);

        // 检查村民属于哪个村庄
        Optional<VillageData> belongingVillage = VillageManager.getVillageIfPosInVillage(villagerPos);

        if (belongingVillage.isPresent()) {
            // 村民属于某个村庄
            villageGenesis$LOGGER.info("村民在位置 {} 属于村庄: {} (ID: {})", villagerPos,
                    belongingVillage.get().getVillageName().getString(),
                    belongingVillage.get().getVillageId());
            belongingVillage.get().addVillager(this.villageGenesis$villager.getUUID());
            // 绑定村民数据到村庄
            villagerData.bindVillage(belongingVillage.get().getVillageId());
        } else {
            // 村民不属于任何村庄
            villageGenesis$LOGGER.debug("村民在位置 {} 不属于任何村庄", villagerPos);
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    public void die(CallbackInfo ci) {

        // 只在服务端处理村民死亡解绑
        if (this.villageGenesis$villager.level().isClientSide()) {
            return;
        }

        // 从所有村庄中移除该村民
        VillageManager.getVillageData(villageGenesis$getVillagerData().getVillageId())
                .ifPresent(villageData -> {
                    villageData.removeVillager(this.villageGenesis$villager.getUUID());
                });

    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        // 只在服务端处理状态更新
        if (this.villageGenesis$villager.level().isClientSide()) {
            return;
        }

        // 获取村民数据并更新状态
        villageGenesis$getVillagerData().getStateManager().update();
    }

    @Override
    public VillagerData villageGenesis$getVillagerData() {
        return this.villageGenesis$villager.getData(ModAttachment.VILLAGER_DATA);
    }

    @Unique
    public UUID villagerTrader$getTraderId() {
        return villageGenesis$villager.getUUID();
    }

    @Unique
    public Optional<UUID> villagerTrader$getVillageIdIfHasVillage() {
        return Optional.ofNullable(villageGenesis$getVillagerData().getVillageId());
    }

    @Unique
    public float villagerTrader$getUnitWorkPoint(ITradableItem item) {
        // NOTE 当村民不属于任何村庄时，村庄经济修正给予 1.2，在当前注册方法下，应不存在此类村民
        float villageFactor = getEconomyData().map(
                villageEconomyData -> villageEconomyData.getFactorFromItem(item)).orElse(1.2f);
        float villagerFactor = villageGenesis$getVillagerData().getPreference(item);
        // TODO 是否将每个村民受到村庄经济数据影响隔离，考虑用 智力，智力越高的村民受自己 物品偏好影响大，反之受村庄经济数据影响大
        return item.getWorkPoint() * villageFactor * villagerFactor;
    }

    @Unique
    public int villagerTrader$getAvailableItemCount(ITradableItem item) {
        SimpleContainer inventory = villageGenesis$villager.getInventory();
        int foundCount = 0;
        for (ItemStack inventoryStack : inventory.getItems()) {
            if (inventoryStack.is((Item) item)) {
                foundCount += inventoryStack.getCount();
            }
        }
        return foundCount;
    }

    @Unique
    public boolean villagerTrader$hasEnough(ItemStack itemStack) {
        SimpleContainer inventory = villageGenesis$villager.getInventory();
        int requiredCount = itemStack.getCount();
        int foundCount = 0;
        for (ItemStack inventoryStack : inventory.getItems()) {
            if (ItemStack.isSameItem(inventoryStack, itemStack)) {
                foundCount += inventoryStack.getCount();
                if (foundCount >= requiredCount) {
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    public void villagerTrader$removeItem(ItemStack itemStack) {
        SimpleContainer inventory = villageGenesis$villager.getInventory();
        ItemStack remainingStack = inventory.removeItemType(itemStack.getItem(), itemStack.getCount());
        // TODO 如果欠了物品，是否需要处理
    }

    @Unique
    public void villagerTrader$addItem(ItemStack itemStack) {
        SimpleContainer inventory = villageGenesis$villager.getInventory();
        ItemStack remainingStack = inventory.addItem(itemStack.copy());
        if (!remainingStack.isEmpty()) {
            villageGenesis$villager.spawnAtLocation(remainingStack);
        }
    }

    @Unique
    public Set<PaymentMethod> villagerTrader$getSupportedPaymentMethods() {
        return Collections.unmodifiableSet(villageGenesis$getVillagerData().getSupportedPaymentMethods());
    }

    @Unique
    public Map<Item, Float> villagerTrader$getPreferenceMultiplier() {
        return Collections.unmodifiableMap(villageGenesis$getVillagerData().getItemPreferencesMap());
    }

    // TODO 交易者协商能力等级，暂时写 0
    @Unique
    public int villagerTrader$getNegotiationLevel() {
        return 0;
    }
}
