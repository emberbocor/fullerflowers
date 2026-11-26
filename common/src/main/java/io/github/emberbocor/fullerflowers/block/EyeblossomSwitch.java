package io.github.emberbocor.fullerflowers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Opening and closing of eyeblossom clusters and potted eyeblossom clusters, like the vanilla eyeblossom and potted
 * eyeblossom: same trigger (the eyeblossom_open environment attribute), sounds and particles, and the same scheduled
 * ticks for identical blocks around a switching cluster. The flower count stays.
 */
final class EyeblossomSwitch {
    private EyeblossomSwitch() {
    }

    /** True when the vanilla flower is an open eyeblossom; null when it is no eyeblossom. */
    private static Boolean open(Block flower) {
        return flower == Blocks.OPEN_EYEBLOSSOM ? Boolean.TRUE : flower == Blocks.CLOSED_EYEBLOSSOM ? Boolean.FALSE : null;
    }

    private static boolean wantsOpen(ServerLevel level, BlockPos pos, boolean open) {
        return level.environmentAttributes().getValue(EnvironmentAttributes.EYEBLOSSOM_OPEN, pos).toBoolean(open);
    }

    /** EyeblossomBlock.randomTick (long sound) and tick (short sound) for a cluster. */
    static void clusterTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, boolean randomTick) {
        FlowerClusterBlock cluster = (FlowerClusterBlock) state.getBlock();
        Boolean open = open(cluster.flower());
        if (open == null || wantsOpen(level, pos, open) == open) {
            return;
        }
        FlowerClusterBlock other = ModBlocks.clusterOf(EyeblossomBlock.Type.fromBoolean(!open).block());
        level.setBlock(pos, other.defaultBlockState().setValue(FlowerClusterBlock.FLOWERS, state.getValue(FlowerClusterBlock.FLOWERS)),
                Block.UPDATE_ALL);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
        EyeblossomBlock.Type type = EyeblossomBlock.Type.fromBoolean(!open);
        type.spawnTransformParticle(level, pos, random);
        BlockPos.betweenClosed(pos.offset(-3, -2, -3), pos.offset(3, 2, 3)).forEach(near -> {
            if (level.getBlockState(near) == state) {
                double distance = Math.sqrt(pos.distSqr(near));
                level.scheduleTick(near, state.getBlock(), random.nextIntBetweenInclusive((int) (distance * 5.0), (int) (distance * 10.0)));
            }
        });
        level.playSound(null, pos, randomTick ? type.longSwitchSound() : shortSwitchSound(!open), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /** FlowerPotBlock.randomTick for a potted eyeblossom cluster. */
    static void potRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        PottedFlowerClusterBlock pot = (PottedFlowerClusterBlock) state.getBlock();
        Boolean open = open(pot.flower());
        if (open == null || wantsOpen(level, pos, open) == open) {
            return;
        }
        PottedFlowerClusterBlock other = ModBlocks.potOf(open ? Blocks.POTTED_CLOSED_EYEBLOSSOM : Blocks.POTTED_OPEN_EYEBLOSSOM);
        level.setBlock(pos, other.defaultBlockState().setValue(PottedFlowerClusterBlock.FLOWERS,
                state.getValue(PottedFlowerClusterBlock.FLOWERS)), Block.UPDATE_ALL);
        EyeblossomBlock.Type type = EyeblossomBlock.Type.fromBoolean(!open);
        type.spawnTransformParticle(level, pos, random);
        level.playSound(null, pos, type.longSwitchSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    // The vanilla short switch sounds of EyeblossomBlock.Type, whose field has no public getter.
    private static SoundEvent shortSwitchSound(boolean toOpen) {
        return toOpen ? SoundEvents.EYEBLOSSOM_OPEN : SoundEvents.EYEBLOSSOM_CLOSE;
    }
}
