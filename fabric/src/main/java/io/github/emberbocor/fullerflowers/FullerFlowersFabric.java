package io.github.emberbocor.fullerflowers;

import io.github.emberbocor.fullerflowers.block.ModBlocks;
import io.github.emberbocor.fullerflowers.config.ServerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FullerFlowersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ModBlocks.register((id, block) -> Registry.register(BuiltInRegistries.BLOCK, id, block));
        ModBlocks.copyFlammability();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ServerConfig.load());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerConfig.unload());
    }
}
