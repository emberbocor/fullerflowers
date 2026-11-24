package io.github.emberbocor.fullerflowers.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.fullerflowers.FullerFlowers;
import io.github.emberbocor.fullerflowers.mixin.FireBlockAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** The cluster block and the potted cluster block of each supported vanilla small flower. */
public final class ModBlocks {
    private static final Map<Block, FlowerClusterBlock> CLUSTER_BY_FLOWER = new HashMap<>();
    private static final Map<Block, PottedFlowerClusterBlock> POT_BY_POTTED_FLOWER = new HashMap<>();
    private static final Set<ResourceLocation> SUPPORTED_FLOWERS = new HashSet<>();
    private static final List<Block> BLOCKS = new ArrayList<>();

    private ModBlocks() {
    }

    /** Creates and registers the blocks. Call once, while the block registry accepts entries. */
    public static void register(BiConsumer<ResourceLocation, Block> registry) {
        Block[][] flowers = {
                {Blocks.DANDELION, Blocks.POTTED_DANDELION},
                {Blocks.POPPY, Blocks.POTTED_POPPY},
                {Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID},
                {Blocks.ALLIUM, Blocks.POTTED_ALLIUM},
                {Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET},
                {Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP},
                {Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP},
                {Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP},
                {Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP},
                {Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY},
                {Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER},
                {Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY},
                {Blocks.TORCHFLOWER, Blocks.POTTED_TORCHFLOWER},
                {Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE},
        };
        for (Block[] pair : flowers) {
            Block flower = pair[0];
            Block pottedFlower = pair[1];
            ResourceLocation flowerId = BuiltInRegistries.BLOCK.getKey(flower);
            FlowerClusterBlock cluster = new FlowerClusterBlock(flower, BlockBehaviour.Properties.ofFullCopy(flower));
            PottedFlowerClusterBlock pot = new PottedFlowerClusterBlock(flower, pottedFlower,
                    BlockBehaviour.Properties.ofFullCopy(pottedFlower));
            registry.accept(id(flowerId.getPath() + "_cluster"), cluster);
            registry.accept(id("potted_" + flowerId.getPath() + "_cluster"), pot);
            CLUSTER_BY_FLOWER.put(flower, cluster);
            POT_BY_POTTED_FLOWER.put(pottedFlower, pot);
            SUPPORTED_FLOWERS.add(flowerId);
            BLOCKS.add(cluster);
            BLOCKS.add(pot);
        }
    }

    /** Gives each cluster the fire behavior of its vanilla flower. Call after vanilla bootstrap and registration. */
    public static void copyFlammability() {
        FireBlock fire = (FireBlock) Blocks.FIRE;
        FireBlockAccessor odds = (FireBlockAccessor) fire;
        for (FlowerClusterBlock cluster : CLUSTER_BY_FLOWER.values()) {
            int ignite = odds.fullerflowers$getIgniteOdds().getInt(cluster.flower());
            int burn = odds.fullerflowers$getBurnOdds().getInt(cluster.flower());
            if (ignite > 0 || burn > 0) {
                fire.setFlammable(cluster, ignite, burn);
            }
        }
    }

    /** The cluster block of a supported vanilla flower block, or null. */
    @Nullable
    public static FlowerClusterBlock clusterOf(Block flower) {
        return CLUSTER_BY_FLOWER.get(flower);
    }

    /** The potted cluster block of a vanilla flower pot holding a supported flower, or null. */
    @Nullable
    public static PottedFlowerClusterBlock potOf(Block pottedFlower) {
        return POT_BY_POTTED_FLOWER.get(pottedFlower);
    }

    public static boolean isSupportedFlower(ResourceLocation flower) {
        return SUPPORTED_FLOWERS.contains(flower);
    }

    /** All blocks of the mod, clusters and potted clusters. */
    public static List<Block> blocks() {
        return List.copyOf(BLOCKS);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(FullerFlowers.MODID, path);
    }
}
