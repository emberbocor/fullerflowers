package io.github.emberbocor.fullerflowers.mixin.fabric;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;

@Mixin(FlowerPotBlock.class)
public interface FlowerPotBlockAccessor {
    @Accessor("POTTED_BY_CONTENT")
    static Map<Block, Block> fullerflowers$getPottedByContent() {
        throw new AssertionError();
    }
}
