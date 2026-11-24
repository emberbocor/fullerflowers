package io.github.emberbocor.fullerflowers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.emberbocor.fullerflowers.block.FlowerGrowth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin {
    // Spawning and teleport checks treat a wither rose cluster as dangerous like a wither rose (wither immune mobs excepted).
    @ModifyVariable(method = "isBlockDangerous", at = @At("HEAD"), argsOnly = true)
    private BlockState fullerflowers$asVanillaFlower(BlockState state) {
        return FlowerGrowth.asVanillaFlower(state);
    }
}
