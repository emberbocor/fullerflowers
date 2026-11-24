package io.github.emberbocor.fullerflowers.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Two to eight flowers of one vanilla small flower in one block. Shape, soil, entity contact and particles come from
 * the vanilla flower; the rest of its behavior comes from the copied block properties.
 */
public final class FlowerClusterBlock extends Block implements BonemealableBlock {
    public static final MapCodec<FlowerClusterBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("flower").forGetter(FlowerClusterBlock::flower),
            propertiesCodec()).apply(instance, FlowerClusterBlock::new));
    public static final int MIN_FLOWERS = 2;
    public static final int MAX_FLOWERS = 8;
    public static final IntegerProperty FLOWERS = IntegerProperty.create("flower_amount", MIN_FLOWERS, MAX_FLOWERS);

    private final Block flower;

    public FlowerClusterBlock(Block flower, Properties properties) {
        super(properties);
        this.flower = flower;
        registerDefaultState(stateDefinition.any().setValue(FLOWERS, MIN_FLOWERS));
    }

    @Override
    protected MapCodec<? extends FlowerClusterBlock> codec() {
        return CODEC;
    }

    /** The vanilla flower this cluster is made of. */
    public Block flower() {
        return flower;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLOWERS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return flower.defaultBlockState().getShape(level, pos, context);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return flower.defaultBlockState().canSurvive(level, pos);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return flower.defaultBlockState().propagatesSkylightDown(level, pos);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return flower.defaultBlockState().isPathfindable(pathComputationType);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        flower.defaultBlockState().entityInside(level, pos, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        flower.animateTick(flower.defaultBlockState(), level, pos, random);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(flower.asItem())
                && FlowerGrowth.canAddByPlacing(context.getLevel(), flower, state.getValue(FLOWERS))
                || super.canBeReplaced(state, context);
    }

    // NeoForge and Forge deprecate this vanilla method; their pick block extension still calls it by default.
    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(flower);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        FlowerGrowth.growOrDrop(this, level, pos, state);
    }
}
