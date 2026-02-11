package cn.ykcryobs.vg.mixin;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.npc.AbstractVillager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author llykff
 */
@Mixin(AbstractVillager.class)
public class AbstractVillagerMixin {

    @Final
    @Mutable
    @Shadow
    private SimpleContainer inventory;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void clinit(CallbackInfo ci) {
        inventory = new SimpleContainer(27);
    }
}
