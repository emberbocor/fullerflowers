package io.github.emberbocor.fullerflowers.platform;

import java.nio.file.Path;

import net.minecraft.world.level.block.Block;

public interface PlatformHelper {
    Path getConfigDirectory();

    /** True when the empty vanilla flower pot accepts this block, i.e. a vanilla pot holding something else ignores it. */
    boolean isPottable(Block block);
}
