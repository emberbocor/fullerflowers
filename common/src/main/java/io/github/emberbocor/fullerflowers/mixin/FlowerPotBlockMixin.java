package io.github.emberbocor.fullerflowers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.fullerflowers.block.FlowerGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(FlowerPotBlock.class)
public abstract class FlowerPotBlockMixin {
    // A vanilla pot holding a supported flower takes a second flower of the same kind.
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void fullerflowers$addFlower(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = FlowerGrowth.addToVanillaPot((Block) (Object) this, player.getItemInHand(hand), level,
                pos, player);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
