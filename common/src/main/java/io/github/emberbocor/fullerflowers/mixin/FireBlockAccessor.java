package io.github.emberbocor.fullerflowers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {
    @Accessor("igniteOdds")
    Object2IntMap<Block> fullerflowers$getIgniteOdds();

    @Accessor("burnOdds")
    Object2IntMap<Block> fullerflowers$getBurnOdds();

    @Invoker("setFlammable")
    void fullerflowers$setFlammable(Block block, int igniteOdds, int burnOdds);
}
