package io.github.emberbocor.fullerflowers.block;

import io.github.emberbocor.fullerflowers.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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

// Vanilla 1.20.1 deprecates most BlockBehaviour methods for callers only; overriding them, and calling super from an
// override, is how blocks implement them.
/** A flower pot holding two to four flowers of one vanilla small flower. One flower is the vanilla potted flower. */
@SuppressWarnings("deprecation")
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return pottedFlower.defaultBlockState().getShape(level, pos, context);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return pottedFlower.defaultBlockState().canSurvive(level, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return pottedFlower.defaultBlockState().isPathfindable(level, pos, type);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
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
        ItemStack taken = new ItemStack(flower);
        if (stack.isEmpty()) {
            player.setItemInHand(hand, taken);
        } else if (!player.addItem(taken)) {
            player.drop(taken, false);
        }
        int count = state.getValue(FLOWERS);
        level.setBlock(pos, count > MIN_FLOWERS ? state.setValue(FLOWERS, count - 1) : pottedFlower.defaultBlockState(),
                Block.UPDATE_ALL);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    // Forge deprecates this vanilla method; its pick block extension still calls it by default.
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(flower);
    }
}
