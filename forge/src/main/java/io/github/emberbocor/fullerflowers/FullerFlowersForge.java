package io.github.emberbocor.fullerflowers;

import io.github.emberbocor.fullerflowers.block.ModBlocks;
import io.github.emberbocor.fullerflowers.config.ServerConfig;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegisterEvent;

@Mod(FullerFlowers.MODID)
public class FullerFlowersForge {
    public FullerFlowersForge() {
        MinecraftForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load());
        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> ServerConfig.unload());
    }

    // Mod bus through the annotation: Forge 52.0.0 passes no loading context to the constructor,
    // and Forge 52.1 deprecates FMLJavaModLoadingContext.get() for removal.
    @Mod.EventBusSubscriber(modid = FullerFlowers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModEvents {
        private ModEvents() {
        }

        @SubscribeEvent
        public static void register(RegisterEvent event) {
            event.register(Registries.BLOCK, helper -> ModBlocks.register(helper::register));
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(ModBlocks::copyFlammability);
        }
    }
}
