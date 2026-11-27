package io.github.emberbocor.fullerflowers.block;

import io.github.emberbocor.fullerflowers.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Prediction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A flower pot holding two to four flowers of one vanilla small flower. One flower is the vanilla potted flower. */
public final class PottedFlowerClusterBlock extends Block {
    public static final int MIN_FLOWERS = 2;
    public static final int MAX_FLOWERS = 4;
    public static final IntegerProperty FLOWERS = IntegerProperty.create("flower_amount", MIN_FLOWERS, MAX_FLOWERS);

    private final Block flower;
    private final Block pottedFlower;

    public PottedFlowerClusterBlock(Block flower, Block pottedFlower, Properties properties) {
        super(properties);
        this.flower = flower;
        this.pottedFlower = pottedFlower;
        registerDefaultState(stateDefinition.any().setValue(FLOWERS, MIN_FLOWERS));
    }

    /** The vanilla flower in this pot. */
    public Block flower() {
        return flower;
    }

    /** The vanilla flower pot holding one of this flower. */
    public Block pottedFlower() {
        return pottedFlower;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLOWERS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return pottedFlower.defaultBlockState().getShape(level, pos, context);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return pottedFlower.defaultBlockState().canSurvive(level, pos);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
            BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return direction == Direction.DOWN && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return pottedFlower.defaultBlockState().isPathfindable(pathComputationType);
    }

    // Only potted eyeblossom clusters tick (randomTicks copied from the vanilla potted eyeblossom).
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        EyeblossomSwitch.potRandomTick(state, level, pos, random);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(flower.asItem())) {
            int count = state.getValue(FLOWERS);
            if (count < Math.min(MAX_FLOWERS, FlowerGrowth.maxPotFlowers(level, flower))) {
                return FlowerGrowth.addToPot(state.setValue(FLOWERS, count + 1), stack, level, pos, player);
            }
            return InteractionResult.CONSUME;
        }
        // Like a vanilla pot that already holds a plant: other pottable plants do nothing, anything else takes a flower out.
        if (stack.getItem() instanceof BlockItem blockItem && Services.PLATFORM.isPottable(blockItem.getBlock())) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        ItemStack taken = new ItemStack(flower);
        if (!player.addItem(taken)) {
            player.drop(taken, false, Prediction.PREDICTED);
        }
        int count = state.getValue(FLOWERS);
        level.setBlock(pos, count > MIN_FLOWERS ? state.setValue(FLOWERS, count - 1) : pottedFlower.defaultBlockState(),
                Block.UPDATE_ALL);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return InteractionResult.SUCCESS;
    }

    // NeoForge deprecates this vanilla method; its pick block extension still calls it by default.
    @SuppressWarnings("deprecation")
    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(flower);
    }
}
