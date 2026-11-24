package io.github.emberbocor.fullerflowers.block;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.fullerflowers.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Growth rules shared by the cluster blocks and the hooks on vanilla flowers and flower pots.
 * <p>
 * A client connected to another server does not know the server config ({@link #UNKNOWN}). It then never predicts a
 * change of blocks or items; the server does the change and sends the result. Bone meal validity on a vanilla flower
 * changes nothing on the client and is predicted as valid there.
 */
public final class FlowerGrowth {
    static final int UNKNOWN = 0;

    private FlowerGrowth() {
    }

    /** Most flowers one block of this flower may hold, or {@link #UNKNOWN}. */
    static int maxFlowers(Level level, Block flower) {
        ServerConfig.Settings settings = ServerConfig.settingsFor(level);
        return settings == null ? UNKNOWN : settings.maxFlowers(BuiltInRegistries.BLOCK.getKey(flower));
    }

    /** Most flowers one flower pot of this flower may hold, or {@link #UNKNOWN}. */
    static int maxPotFlowers(Level level, Block flower) {
        ServerConfig.Settings settings = ServerConfig.settingsFor(level);
        return settings == null ? UNKNOWN : settings.maxPotFlowers(BuiltInRegistries.BLOCK.getKey(flower));
    }

    /** True when a block of this flower holding {@code count} flowers may take one more by placing. */
    static boolean canAdd(Level level, Block flower, int count) {
        return count < maxFlowers(level, flower);
    }

    /** True when an item of the same flower is placed into a supported vanilla flower that may grow. */
    public static boolean canAddByPlacing(BlockState state, BlockPlaceContext context) {
        Block flower = state.getBlock();
        return ModBlocks.clusterOf(flower) != null && !context.isSecondaryUseActive()
                && context.getItemInHand().is(flower.asItem()) && canAdd(context.getLevel(), flower, 1);
    }

    /**
     * The state placed by an item of a supported vanilla flower when it goes into that flower or its cluster, like pink
     * petals; null for every other placement.
     */
    @Nullable
    public static BlockState stateForPlacement(Block flower, BlockPlaceContext context) {
        FlowerClusterBlock cluster = ModBlocks.clusterOf(flower);
        if (cluster == null) {
            return null;
        }
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(flower)) {
            return cluster.defaultBlockState();
        }
        if (existing.is(cluster)) {
            return existing.setValue(FlowerClusterBlock.FLOWERS,
                    Math.min(FlowerClusterBlock.MAX_FLOWERS, existing.getValue(FlowerClusterBlock.FLOWERS) + 1));
        }
        return null;
    }

    /**
     * Bone meal on a supported vanilla flower: turns it into a cluster of 2. Returns null when the block is not a
     * supported flower or the flower may not grow, so vanilla goes on as usual.
     */
    @Nullable
    public static Boolean applyBoneMeal(ItemStack stack, Level level, BlockPos pos) {
        Block flower = level.getBlockState(pos).getBlock();
        FlowerClusterBlock cluster = ModBlocks.clusterOf(flower);
        if (cluster == null) {
            return null;
        }
        int max = maxFlowers(level, flower);
        if (max != UNKNOWN && max < FlowerClusterBlock.MIN_FLOWERS) {
            return null;
        }
        if (level instanceof ServerLevel) {
            level.setBlock(pos, cluster.defaultBlockState(), Block.UPDATE_CLIENTS);
            stack.shrink(1);
        }
        return true;
    }

    /** Bone meal on a cluster: one flower more, or one flower dropped when the cluster is full. */
    static void growOrDrop(FlowerClusterBlock cluster, ServerLevel level, BlockPos pos, BlockState state) {
        int count = state.getValue(FlowerClusterBlock.FLOWERS);
        if (count < Math.min(FlowerClusterBlock.MAX_FLOWERS, maxFlowers(level, cluster.flower()))) {
            level.setBlock(pos, state.setValue(FlowerClusterBlock.FLOWERS, count + 1), Block.UPDATE_CLIENTS);
        } else {
            Block.popResource(level, pos, new ItemStack(cluster.flower()));
        }
    }

    /**
     * The same flower used on a vanilla flower pot holding a supported flower: the pot becomes a potted cluster of 2.
     * Returns null when the pot or item does not match or the pot may not grow, so vanilla goes on as usual.
     */
    @Nullable
    public static ItemInteractionResult addToVanillaPot(Block pottedFlower, ItemStack stack, Level level, BlockPos pos,
            Player player) {
        PottedFlowerClusterBlock pot = ModBlocks.potOf(pottedFlower);
        if (pot == null || !stack.is(pot.flower().asItem())
                || maxPotFlowers(level, pot.flower()) < PottedFlowerClusterBlock.MIN_FLOWERS) {
            return null;
        }
        return addToPot(pot.defaultBlockState(), stack, level, pos, player);
    }

    static ItemInteractionResult addToPot(BlockState newState, ItemStack stack, Level level, BlockPos pos, Player player) {
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        player.awardStat(Stats.POT_FLOWER);
        stack.consume(1, player);
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    /** A cluster as its vanilla flower, for vanilla checks that name the flower block (wither rose danger). */
    public static BlockState asVanillaFlower(BlockState state) {
        return state.getBlock() instanceof FlowerClusterBlock cluster ? cluster.flower().defaultBlockState() : state;
    }
}
