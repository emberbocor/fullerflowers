package io.github.emberbocor.fullerflowers.platform;

import java.nio.file.Path;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.fml.loading.FMLPaths;

public final class ForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isPottable(Block block) {
        return ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().containsKey(BuiltInRegistries.BLOCK.getKey(block));
    }
}
