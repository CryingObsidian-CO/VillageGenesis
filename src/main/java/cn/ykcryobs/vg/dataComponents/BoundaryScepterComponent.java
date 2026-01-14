package cn.ykcryobs.vg.dataComponents;

import cn.ykcryobs.vg.init.ModDataComponents;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 边界权杖组件
 *
 * @author llykff
 */
public class BoundaryScepterComponent {

    public static final Codec<UUID> UUID_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.STRING.fieldOf("village_id").forGetter(UUID::toString))
                    .apply(instance, UUID::fromString));

    public static final StreamCodec<ByteBuf, UUID> UUID_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UUID::toString, UUID::fromString);

    /**
     * 尝试将物品绑定到村庄
     *
     * @param clickedPos 点击位置
     * @param level      等级
     * @param player     玩家
     * @param itemStack  物品栈
     * @return 是否绑定成功
     */
    public static boolean tryToBindVillage(BlockPos clickedPos, Level level, Player player,
            ItemStack itemStack) {

        Optional<VillageData> villageData = VillageManager.getVillageIfPosInVillage(clickedPos);
        if (villageData.isEmpty()) {
            sendMessage(player, Component.translatable("message.village_genesis.not_in_village"));
            return false;
        }
        if (player.isCrouching() && level.getBlockState(clickedPos).getBlock() == Blocks.BELL) {
            BoundaryScepterComponent.setBoundedVillage(itemStack, villageData.get().getVillageId());
            return true;
        }

        return false;
    }

    /**
     * 设置绑定到该物品的村庄UUID
     *
     * @param stack     物品栈
     * @param villageId 要绑定的村庄UUID
     */
    public static void setBoundedVillage(ItemStack stack, UUID villageId) {
        stack.set(ModDataComponents.BOUNDED_VILLAGE, villageId);
    }

    /**
     * 向玩家发送消息
     *
     * @param player  玩家
     * @param message 消息内容
     */
    private static void sendMessage(Player player, Component message) {
        // TODO 改进提示
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(message, true);
        }
    }


    /**
     * 检查该权杖是否已绑定村庄
     *
     * @param stack 权杖物品栈
     * @return 如果已绑定村庄返回true，否则返回false
     */
    public static boolean hasBoundedVillage(ItemStack stack) {
        return getBoundedVillage(stack) != null;
    }

    /**
     * 获取绑定到该权杖的村庄UUID
     *
     * @param stack 权杖物品栈
     * @return 绑定的村庄UUID，如果未绑定则返回null
     */
    @Nullable
    public static UUID getBoundedVillage(ItemStack stack) {
        return stack.get(ModDataComponents.BOUNDED_VILLAGE);
    }

    public static void getToolTips(ItemStack stack, List<Component> tooltipComponents) {
        UUID boundedVillage = BoundaryScepterComponent.getBoundedVillage(stack);
        if (boundedVillage != null) {
            Optional<VillageData> villageData = VillageManager.getVillageData(boundedVillage);
            if (villageData.isPresent()) {
                Component villageName = villageData.get().getVillageName();
                tooltipComponents.add(
                        Component.translatable("tooltip.village_genesis.boundary_scepter.bound_village"));
                tooltipComponents.add(villageName);
            } else {
                tooltipComponents.add(
                        Component.translatable("tooltip.village_genesis.boundary_scepter.bound_unknown"));
            }
        } else {
            tooltipComponents.add(
                    Component.translatable("tooltip.village_genesis.boundary_scepter.not_bound"));
        }
    }
}

