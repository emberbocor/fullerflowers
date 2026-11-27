package io.github.emberbocor.fullerflowers.platform;

import java.nio.file.Path;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.fml.loading.FMLPaths;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    // NeoForge 26.2.0.93 deprecates getFullPotsView for removal in 26.3; its replacement getFullPot is missing in the
    // minimum 26.2.0.0-beta.
    @SuppressWarnings("removal")
    @Override
    public boolean isPottable(Block block) {
        return ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().containsKey(BuiltInRegistries.BLOCK.getKey(block));
    }
}
