package io.github.emberbocor.fullerflowers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.emberbocor.fullerflowers.block.FlowerGrowth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin {
    // Mobs judge a cluster like its vanilla flower, so they avoid wither rose clusters like wither roses.
    @ModifyVariable(method = "getPathTypeFromState", at = @At("STORE"), ordinal = 0)
    private static BlockState fullerflowers$asVanillaFlower(BlockState state) {
        return FlowerGrowth.asVanillaFlower(state);
    }
}
