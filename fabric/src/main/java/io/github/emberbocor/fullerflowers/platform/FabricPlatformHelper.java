package io.github.emberbocor.fullerflowers.platform;

import java.nio.file.Path;

import io.github.emberbocor.fullerflowers.mixin.fabric.FlowerPotBlockAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.Block;

public final class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isPottable(Block block) {
        return FlowerPotBlockAccessor.fullerflowers$getPottedByContent().containsKey(block);
    }
}
