package cn.ykcryobs.vg.item;

import cn.ykcryobs.vg.client.event.ClientParticleEvents;
import cn.ykcryobs.vg.dataComponents.BoundaryScepterComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 边界权杖物品，用于绑定村庄并在游戏中显示村庄边界
 *
 * @author llykff
 */
public class BoundaryScepterItem extends Item {

    public BoundaryScepterItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player,
            @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (BoundaryScepterComponent.hasBoundedVillage(stack)) {
            if (level.isClientSide() && usedHand == InteractionHand.MAIN_HAND) {
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
        ItemStack itemStack = context.getItemInHand();
        return BoundaryScepterComponent.tryToBindVillage(clickedPos, level, player, itemStack)
                ? InteractionResult.SUCCESS : super.useOn(context);
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
        BoundaryScepterComponent.getToolTips(stack, tooltipComponents);
    }
}
