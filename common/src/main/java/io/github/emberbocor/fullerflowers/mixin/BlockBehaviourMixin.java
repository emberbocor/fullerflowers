package io.github.emberbocor.fullerflowers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.fullerflowers.block.FlowerGrowth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    // Pink petals pattern: a supported vanilla flower can be replaced by the same flower item, which then adds a flower.
    @Inject(method = "canBeReplaced(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/context/BlockPlaceContext;)Z",
            at = @At("HEAD"), cancellable = true)
    private void fullerflowers$addFlower(BlockState state, BlockPlaceContext context, CallbackInfoReturnable<Boolean> cir) {
        if (FlowerGrowth.canAddByPlacing(state, context)) {
            cir.setReturnValue(true);
        }
    }
}
