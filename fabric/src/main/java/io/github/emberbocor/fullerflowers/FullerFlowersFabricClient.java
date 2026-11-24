package io.github.emberbocor.fullerflowers;

import io.github.emberbocor.fullerflowers.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class FullerFlowersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // NeoForge and Forge take render_type from the block models; on Fabric the layer is set here.
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.blocks().toArray(new Block[0]));
    }
}
