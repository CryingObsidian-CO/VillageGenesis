package cn.ykcryobs.vg.block.entity;

import cn.ykcryobs.vg.init.ModBlockEntities;
import cn.ykcryobs.vg.init.ModDataComponents;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * 村庄信息面板方块实体 处理村庄绑定逻辑和存储村庄信息
 *
 * @author llykff
 */
public class VillageInfoPanelBlockEntity extends BlockEntity {

    private UUID boundedVillageId = null;

    public VillageInfoPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VILLAGE_INFO_PANEL.get(), pos, state);
    }

    /**
     * 检查是否已绑定村庄
     *
     * @return 如果已绑定村庄返回true，否则返回false
     */
    public boolean hasBoundedVillage() {
        return boundedVillageId != null;
    }

    /**
     * 获取绑定的村庄数据
     *
     * @return 绑定的村庄数据，如果未绑定或村庄不存在则返回Optional.empty()
     */
    public Optional<VillageData> getBoundedVillageData() {
        if (boundedVillageId == null) {
            return Optional.empty();
        }
        return VillageManager.getVillageData(boundedVillageId);
    }

    /**
     * 设置绑定的村庄ID
     *
     * @param villageId 要绑定的村庄ID
     */
    public void setBoundedVillage(UUID villageId) {
        this.boundedVillageId = villageId;
        setChanged();
    }

    /**
     * 获取绑定的村庄ID
     *
     * @return 绑定的村庄ID，如果未绑定则返回null
     */
    public UUID getBoundedVillageId() {
        return boundedVillageId;
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("has_bounded_village", hasBoundedVillage());
        if (hasBoundedVillage()) {
            tag.putUUID("bounded_village_id", boundedVillageId);
        }
    }


    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("has_bounded_village") && tag.getBoolean("has_bounded_village")) {
            this.boundedVillageId = tag.getUUID("bounded_village_id");
        }
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentInput componentInput) {
        this.setBoundedVillage(componentInput.get(ModDataComponents.BOUNDED_VILLAGE));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}