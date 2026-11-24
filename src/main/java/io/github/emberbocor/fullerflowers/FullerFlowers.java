package io.github.emberbocor.fullerflowers;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(FullerFlowers.MODID)
public class FullerFlowers {
    public static final String MODID = "fullerflowers";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FullerFlowers(IEventBus modEventBus, ModContainer modContainer) {
    }
}
