package io.github.emberbocor.fullerflowers;

import io.github.emberbocor.fullerflowers.block.ModBlocks;
import io.github.emberbocor.fullerflowers.config.ServerConfig;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(FullerFlowers.MODID)
public class FullerFlowersForge {
    public FullerFlowersForge(FMLJavaModLoadingContext context) {
        BusGroup modBus = context.getModBusGroup();
        RegisterEvent.getBus(modBus).addListener(event -> event.register(Registries.BLOCK,
                helper -> ModBlocks.register(helper::register)));
        FMLCommonSetupEvent.getBus(modBus).addListener(event -> event.enqueueWork(ModBlocks::copyFlammability));
        ServerAboutToStartEvent.BUS.addListener(event -> ServerConfig.load());
        ServerStoppedEvent.BUS.addListener(event -> ServerConfig.unload());
    }
}
