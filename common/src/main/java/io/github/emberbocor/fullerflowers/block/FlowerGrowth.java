package io.github.emberbocor.fullerflowers.block;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.fullerflowers.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Growth rules shared by the cluster blocks and the hooks on vanilla flowers and flower pots.
 * <p>
 * A client connected to another server does not know the server config ({@link #UNKNOWN}). It never predicts a change
 * that depends on the config; the server does the change and sends the result. Adding a flower by placing is never
 * predicted on any client: the client aims at the flower like the server does but places nothing, and the server sends
 * the place sound to the placing player. Bone meal validity on a vanilla flower changes nothing on the client and is
 * predicted as valid there.
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

    /**
     * True when a block of this flower holding {@code count} flowers is a target for one more flower placed by hand.
     * A client without the config aims as if the block can take one more; it predicts nothing either way.
     */
    static boolean canAddByPlacing(Level level, Block flower, int count) {
        int max = maxFlowers(level, flower);
        return count < (max == UNKNOWN ? FlowerClusterBlock.MAX_FLOWERS : max);
    }

    /** True when an item of the same flower is placed into a supported vanilla flower that may grow. */
    public static boolean canAddByPlacing(BlockState state, BlockPlaceContext context) {
        Block flower = state.getBlock();
        return ModBlocks.clusterOf(flower) != null && !context.isSecondaryUseActive()
                && context.getItemInHand().is(flower.asItem()) && canAddByPlacing(context.getLevel(), flower, 1);
    }

    /** True when an item of this supported vanilla flower is being placed into that flower or its cluster. */
    public static boolean isAddedByPlacing(Block flower, BlockPlaceContext context) {
        FlowerClusterBlock cluster = ModBlocks.clusterOf(flower);
        if (cluster == null) {
            return false;
        }
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        return existing.is(flower) || existing.is(cluster);
    }

    /**
     * The state placed when {@link #isAddedByPlacing} holds, like pink petals: the cluster with one flower more.
     * Null on the client, so the client places nothing and waits for the server.
     */
    @Nullable
    public static BlockState stateAddedByPlacing(Block flower, BlockPlaceContext context) {
        FlowerClusterBlock cluster = ModBlocks.clusterOf(flower);
        if (cluster == null || context.getLevel().isClientSide()) {
            return null;
        }
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(cluster)) {
            return existing.setValue(FlowerClusterBlock.FLOWERS,
                    Math.min(FlowerClusterBlock.MAX_FLOWERS, existing.getValue(FlowerClusterBlock.FLOWERS) + 1));
        }
        return cluster.defaultBlockState();
    }

    /**
     * After a successful placement on the server: when the placed flower went into a cluster, sends the vanilla place
     * sound to the placing player, who predicted nothing. Others already hear it from BlockItem, which skips the placer.
     * NeoForge deprecates BlockState.getSoundType() for its positional variant, which gives the same for clusters.
     */
    @SuppressWarnings("deprecation")
    public static void playAddedSound(Block flower, BlockPlaceContext context) {
        if (!(context.getPlayer() instanceof ServerPlayer player) || !(context.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof FlowerClusterBlock cluster) || cluster.flower() != flower) {
            return;
        }
        SoundType sound = state.getSoundType();
        player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound.getPlaceSound()),
                SoundSource.BLOCKS, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, (sound.getVolume() + 1.0F) / 2.0F,
                sound.getPitch() * 0.8F, level.getRandom().nextLong()));
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
    public static InteractionResult addToVanillaPot(Block pottedFlower, ItemStack stack, Level level, BlockPos pos,
            Player player) {
        PottedFlowerClusterBlock pot = ModBlocks.potOf(pottedFlower);
        if (pot == null || !stack.is(pot.flower().asItem())) {
            return null;
        }
        int max = maxPotFlowers(level, pot.flower());
        if (max == UNKNOWN) {
            // A client without the config predicts nothing: the vanilla CONSUME of a pot that already holds a plant
            // (NeoForge 26.2+ would otherwise predict taking the flower out).
            return InteractionResult.CONSUME;
        }
        if (max < PottedFlowerClusterBlock.MIN_FLOWERS) {
            return null;
        }
        return addToPot(pot.defaultBlockState(), stack, level, pos, player);
    }

    static InteractionResult addToPot(BlockState newState, ItemStack stack, Level level, BlockPos pos, Player player) {
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        player.awardStat(Stats.POT_FLOWER);
        stack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

    /** A cluster as its vanilla flower, for vanilla checks that name the flower block (wither rose danger). */
    public static BlockState asVanillaFlower(BlockState state) {
        return state.getBlock() instanceof FlowerClusterBlock cluster ? cluster.flower().defaultBlockState() : state;
    }
}
