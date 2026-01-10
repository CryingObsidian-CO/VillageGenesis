package cn.ykcryobs.vg.item;

import cn.ykcryobs.vg.event.client.ClientParticleEvents;
import cn.ykcryobs.vg.init.ModDataComponents;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 边界权杖物品，用于绑定村庄并在游戏中显示村庄边界
 *
 * @author llykff
 */
public class BoundaryScepterItem extends Item {

    public BoundaryScepterItem() {
        super(new Item.Properties().stacksTo(1));
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

    /**
     * 设置绑定到该权杖的村庄UUID
     *
     * @param stack     权杖物品栈
     * @param villageId 要绑定的村庄UUID
     */
    public static void setBoundedVillage(ItemStack stack, UUID villageId) {
        stack.set(ModDataComponents.BOUNDED_VILLAGE, villageId);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player,
            @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (hasBoundedVillage(stack)) {
            if (level.isClientSide() && usedHand == InteractionHand.MAIN_HAND) {
                // NOTE 客户端处理：触发粒子渲染
                ClientParticleEvents.toggleParticleRender();
                return InteractionResultHolder.success(stack);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (context.getLevel().isClientSide() || player == null) {
            return super.useOn(context);
        }

        Optional<VillageData> villageData = VillageManager.getVillageIfPosInVillage(clickedPos);
        ItemStack itemStack = context.getItemInHand();
        if (villageData.isEmpty()) {
            // TODO 改进提示
            this.sendMessage(player, Component.translatable("message.village_genesis.not_in_village"));
            return super.useOn(context);
        }
        if (player.isCrouching() && level.getBlockState(clickedPos).getBlock() == Blocks.BELL) {
            setBoundedVillage(itemStack, villageData.get().getVillageId());
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    /**
     * 向玩家发送消息
     *
     * @param player  玩家
     * @param message 消息内容
     */
    private void sendMessage(Player player, Component message) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(message, true);
        }
    }

    /**
     * 向物品添加提示信息 显示权杖是否已绑定村庄以及绑定的村庄名称
     *
     * @param stack             被Hover的物品栈
     * @param tooltipContext    提示框渲染的上下文
     * @param tooltipComponents 提示框组件列表
     * @param isAdvanced        是否启用了高级提示
     */
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext tooltipContext,
            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        UUID boundedVillage = getBoundedVillage(stack);
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
