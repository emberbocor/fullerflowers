package io.github.emberbocor.fullerflowers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.fullerflowers.block.FlowerGrowth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Shadow
    public abstract Block getBlock();

    // The placer predicted nothing when a flower went into a cluster, so the server sends it the place sound.
    @Inject(method = "place", at = @At("RETURN"))
    private void fullerflowers$addedSound(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue().consumesAction()) {
            FlowerGrowth.playAddedSound(getBlock(), context);
        }
    }
}
