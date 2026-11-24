package io.github.emberbocor.fullerflowers;

import io.github.emberbocor.fullerflowers.block.ModBlocks;
import io.github.emberbocor.fullerflowers.config.ServerConfig;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(FullerFlowers.MODID)
public class FullerFlowersNeoForge {
    public FullerFlowersNeoForge(IEventBus modEventBus) {
        modEventBus.addListener((RegisterEvent event) -> event.register(Registries.BLOCK,
                helper -> ModBlocks.register(helper::register)));
        modEventBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(ModBlocks::copyFlammability));
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load());
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> ServerConfig.unload());
    }
}
