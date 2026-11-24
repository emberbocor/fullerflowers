package io.github.emberbocor.fullerflowers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.fullerflowers.block.FlowerGrowth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public abstract class BlockMixin {
    // A supported vanilla flower item placed into that flower or its cluster places the cluster with one flower more
    // on the server, and nothing on the client.
    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void fullerflowers$placeInCluster(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        Block flower = (Block) (Object) this;
        if (FlowerGrowth.isAddedByPlacing(flower, context)) {
            cir.setReturnValue(FlowerGrowth.stateAddedByPlacing(flower, context));
        }
    }
}
