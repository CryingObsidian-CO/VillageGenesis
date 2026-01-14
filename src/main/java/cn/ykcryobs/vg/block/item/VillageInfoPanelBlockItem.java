package cn.ykcryobs.vg.block.item;

import cn.ykcryobs.vg.dataComponents.BoundaryScepterComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author llykff
 */
public class VillageInfoPanelBlockItem extends BlockItem {

    public VillageInfoPanelBlockItem(Block block) {
        super(block, new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (context.getLevel().isClientSide() || player == null) {
            return InteractionResult.PASS;
        }
        ItemStack itemStack = context.getItemInHand();
        if (player.isCrouching()) {
            boolean isSuccess = BoundaryScepterComponent.tryToBindVillage(clickedPos, level, player,
                    itemStack);
            return isSuccess ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (BoundaryScepterComponent.hasBoundedVillage(itemStack)) {
            return super.useOn(context);


        }
        return InteractionResult.PASS;
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
