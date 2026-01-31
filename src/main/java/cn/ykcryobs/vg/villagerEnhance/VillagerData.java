package cn.ykcryobs.vg.villagerEnhance;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.event.VillageNewStageEvent;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.economy.ITrader;
import cn.ykcryobs.vg.villageSystem.economy.payment.PaymentMethod;
import cn.ykcryobs.vg.villagerEnhance.state.StateManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 村民数据
 *
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class VillagerData implements INBTSerializable<CompoundTag>, ITrader {

    private final Map<Item, Float> itemPreferences;
    private final Set<PaymentMethod> supportedPaymentMethods;
    private UUID villagerId;
    private UUID villageId;

    private int happiness = 20; // 幸福度：影响村民的幸福感
    private int loyalty = 20; // 忠诚度：影响村民迁移/叛逃的概率
    private int adaptability = 20; // 适应度：影响村民适应新工作的能力
    private int curiosity = 20; // 好奇心：影响村民探索的欲望
    private int fatigue = 0; // 疲劳：过高会降低其他属性表现
    private int stress = 0; // 压力：过高会降低其他属性表现
    private StateManager stateManager; // 状态管理器

    private transient WeakReference<Villager> attachedVillager;

    /**
     * 构造函数
     */
    public VillagerData() {
        this.itemPreferences = new HashMap<>();
        this.stateManager = new StateManager();
        this.supportedPaymentMethods = new HashSet<>();
    }

    /**
     * 更新根据村庄演进阶段支持的支付方法
     *
     * @param event 村庄更新事件
     */
    // NOTE 在村庄演进阶段变化时调用，暂时固定这些支付方式
    @SubscribeEvent
    private static void updatePaymentMethodsForEvolutionStage(VillageNewStageEvent event) {
        ServerLevel level = VillageGenesis.getLevel();
        VillageData villageData = event.getVillageData();
        Set<UUID> villagers = villageData.getVillagers();
        villagers.forEach(villagerId -> {
            IVillageMixin villager = (IVillageMixin) level.getEntity(villagerId);
            if (villager == null) {
                return;
            }
            Optional<VillagerData> villagerDataOptional = villager.villageGenesis$getVillagerData();
            if (villagerDataOptional.isPresent()) {
                VillagerData villagerData = villagerDataOptional.get();
                villagerData.supportedPaymentMethods.clear();
                switch (event.getStage()) {
                    case PRIMITIVE -> {
                        villagerData.supportedPaymentMethods.add(PaymentMethod.BARTER);
                    }
                    case AGRICULTURAL -> {
                        villagerData.supportedPaymentMethods.add(PaymentMethod.BARTER);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.COMMODITY_PAYMENT);
                    }
                    case HANDICRAFT -> {
                        villagerData.supportedPaymentMethods.add(PaymentMethod.BARTER);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.COMMODITY_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.METAL_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.MIXED_PAYMENT);
                    }
                    case COMMERCIAL -> {
                        villagerData.supportedPaymentMethods.add(PaymentMethod.BARTER);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.COMMODITY_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.METAL_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.PAPER_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.MIXED_PAYMENT);
                    }
                    case INDUSTRIAL -> {
                        villagerData.supportedPaymentMethods.add(PaymentMethod.BARTER);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.COMMODITY_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.METAL_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.PAPER_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.ELECTRONIC_PAYMENT);
                        villagerData.supportedPaymentMethods.add(PaymentMethod.MIXED_PAYMENT);
                    }
                }
            }
        });


    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("villagerID", villagerId);
        tag.putUUID("villageID", villageId);
        tag.putInt("happiness", happiness);
        tag.putInt("loyalty", loyalty);
        tag.putInt("adaptability", adaptability);
        tag.putInt("curiosity", curiosity);

        tag.putInt("fatigue", fatigue);
        tag.putInt("stress", stress);

        // 序列化状态管理器
        tag.put("activeStates", stateManager.serializeNBT());

        // 序列化物品偏好
        ListTag itemPrefList = new ListTag();
        itemPreferences.forEach((item, preference) -> {
            CompoundTag itemPrefTag = new CompoundTag();
            itemPrefTag.putInt("item", Item.getId(item));
            itemPrefTag.putFloat("preference", preference);
            itemPrefList.add(itemPrefTag);
        });
        tag.put("itemPreferences", itemPrefList);

        ListTag supportedPaymentMethodsList = new ListTag();
        for (PaymentMethod paymentMethod : supportedPaymentMethods) {
            supportedPaymentMethodsList.add(StringTag.valueOf(paymentMethod.toString()));
        }
        tag.put("supportedPaymentMethods", supportedPaymentMethodsList);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        villagerId = nbt.getUUID("villagerID");
        villageId = nbt.getUUID("villageID");
        happiness = nbt.getInt("happiness");
        loyalty = nbt.getInt("loyalty");
        adaptability = nbt.getInt("adaptability");
        curiosity = nbt.getInt("curiosity");

        fatigue = nbt.getInt("fatigue");
        stress = nbt.getInt("stress");

        // 初始化状态管理器
        this.stateManager = new StateManager();
        stateManager.deserializeNBT(nbt.getCompound("activeStates"));

        // 反序列化物品偏好
        if (nbt.contains("itemPreferences", Tag.TAG_LIST)) {
            ListTag itemPrefList = nbt.getList("itemPreferences", Tag.TAG_COMPOUND);
            itemPreferences.clear();
            for (Tag itemPrefTag : itemPrefList) {
                if (itemPrefTag instanceof CompoundTag compoundTag) {
                    Item item = Item.byId(compoundTag.getInt("item"));
                    float preference = compoundTag.getFloat("preference");
                    itemPreferences.put(item, preference);
                }
            }
        }

        // 反序列化支持的支付方法
        if (nbt.contains("supportedPaymentMethods", Tag.TAG_LIST)) {
            ListTag supportedPaymentMethodsList = nbt.getList("supportedPaymentMethods", Tag.TAG_STRING);
            supportedPaymentMethods.clear();
            for (Tag paymentMethodTag : supportedPaymentMethodsList) {
                if (paymentMethodTag instanceof StringTag stringTag) {
                    supportedPaymentMethods.add(PaymentMethod.valueOf(stringTag.getAsString()));
                }
            }
        }
    }

    /**
     * 绑定村民到村庄
     *
     * @param villager  村民
     * @param villageId 村庄ID
     */
    public void bindVillage(Villager villager, UUID villageId) {
        this.villagerId = villager.getUUID();
        this.attachedVillager = new WeakReference<>(villager);
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

    /**
     * 重新建立村民引用
     *
     * @param level 世界实例
     */
    public void reestablishVillagerReference(Level level) {
        if (this.villagerId == null || attachedVillager != null) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.villagerId);
            if (entity instanceof Villager villager) {
                attachedVillager = new WeakReference<>(villager);
            }
        }
    }

    /**
     * 获取绑定的村民
     *
     * @return 绑定的村民实例
     */
    public Optional<Villager> getAttachedVillager() {
        return Optional.ofNullable(attachedVillager.get());
    }

    /**
     * 添加物品偏好
     *
     * @param item       物品
     * @param preference 偏好增量
     */
    // TODO 怎么修改这个偏好值啊，复杂
    public void addPreference(Item item, float preference) {
        float currentPreference = this.itemPreferences.getOrDefault(item, 1f);
        float newPreference = currentPreference + preference;

        newPreference = Math.max(ServerConfig.minFactor.getAsInt(),
                Math.min(ServerConfig.maxFactor.getAsInt(), newPreference));
        this.itemPreferences.put(item, newPreference);
    }

    /**
     * 获取物品偏好
     *
     * @param item 物品
     * @return 偏好值
     */
    public float getPreference(Item item) {
        return itemPreferences.getOrDefault(item, 1f);
    }

    /**
     * 获取物品偏好映射
     *
     * @return 物品偏好映射（不可修改）
     */
    public Map<Item, Float> getItemPreferencesMap() {
        return Collections.unmodifiableMap(itemPreferences);
    }

    /**
     * 获取状态管理器
     *
     * @return 状态管理器实例
     */
    public StateManager getStateManager() {
        return stateManager;
    }

    /**
     * 添加支持的支付方法
     *
     * @param paymentMethod 支付方法
     */
    public void addSupportedPaymentMethod(PaymentMethod paymentMethod) {
        supportedPaymentMethods.add(paymentMethod);
    }

    /**
     * 移除支持的支付方法
     *
     * @param paymentMethod 支付方法
     */
    public void removeSupportedPaymentMethod(PaymentMethod paymentMethod) {
        supportedPaymentMethods.remove(paymentMethod);
    }

    @Override
    public UUID getTraderId() {
        return this.villagerId;
    }

    @Override
    public Optional<UUID> getVillageIdIfHasVillage() {
        return Optional.of(villageId);
    }

    @Override
    public boolean hasEnough(ItemStack itemStack) {
        Optional<Villager> villagerOptional = this.getAttachedVillager();
        if (villagerOptional.isEmpty()) {
            return false;
        }

        SimpleContainer inventory = villagerOptional.get().getInventory();
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

    @Override
    public void removeItem(ItemStack itemStack) {
        Optional<Villager> villagerOptional = this.getAttachedVillager();
        if (villagerOptional.isEmpty()) {
            return;
        }

        SimpleContainer inventory = villagerOptional.get().getInventory();
        int remainingCount = itemStack.getCount();
        for (ItemStack inventoryStack : inventory.getItems()) {
            if (ItemStack.isSameItem(inventoryStack, itemStack)) {
                int removeCount = Math.min(inventoryStack.getCount(), remainingCount);
                inventoryStack.shrink(removeCount);
                remainingCount -= removeCount;
                if (remainingCount <= 0) {
                    break;
                }
            }
        }
    }

    @Override
    public void addItem(ItemStack itemStack) {
        Optional<Villager> villagerOptional = this.getAttachedVillager();
        if (villagerOptional.isEmpty()) {
            return;
        }

        SimpleContainer inventory = villagerOptional.get().getInventory();
        inventory.addItem(itemStack.copy());
    }

    @Override
    public Map<Item, Float> getPreferenceMultiplier() {
        return getItemPreferencesMap();
    }

    @Override
    public Set<PaymentMethod> getSupportedPaymentMethods() {
        return Collections.unmodifiableSet(this.supportedPaymentMethods);
    }

}