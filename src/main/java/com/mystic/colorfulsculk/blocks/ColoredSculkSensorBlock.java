package com.mystic.colorfulsculk.blocks;

import net.minecraft.Util;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;

public class ColoredSculkSensorBlock extends BaseEntityBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<SculkSensorPhase> PHASE = EnumProperty.create("sculk_sensor_phase", SculkSensorPhase.class);

    private static final float[] RESONANCE_PITCH_BEND = Util.make(new float[16], (p_277301_) -> {
        int[] aint = new int[]{0, 0, 2, 4, 6, 7, 9, 10, 12, 14, 15, 18, 19, 21, 22, 24};
        for (int i = 0; i < 16; ++i) {
            p_277301_[i] = NoteBlock.getPitchFromNote(aint[i]);
        }
    });
    private final DyeColor color;

    public ColoredSculkSensorBlock(BlockBehaviour.Properties p_277588_, DyeColor color) {
        super(p_277588_);
        this.registerDefaultState(this.stateDefinition.any().setValue(PHASE, SculkSensorPhase.INACTIVE).setValue(POWER, 0).setValue(WATERLOGGED, false));
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    public static boolean canActivate(BlockState state) {
        return state.getValue(PHASE) != SculkSensorPhase.ACTIVE;
    }

    public int getActiveTicks() {
        return 10;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ColoredSculkSensorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return createTickerHelper(type, com.mystic.colorfulsculk.init.BlockEntityInit.COLORED_SCULK_SENSOR.get(),
                    (level1, pos, state1, blockEntity) -> VibrationSystem.Ticker.tick(level1, blockEntity.getVibrationData(), blockEntity.getVibrationUser()));
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PHASE, WATERLOGGED, POWER);
    }

    public void activate(@Nullable Entity p_277529_, Level p_277340_, @NotNull BlockPos p_277386_, BlockState p_277799_, int p_277993_, int p_278003_) {
        p_277340_.setBlock(p_277386_, p_277799_.setValue(PHASE, SculkSensorPhase.ACTIVE).setValue(POWER, p_277993_), 3);
        p_277340_.scheduleTick(p_277386_, p_277799_.getBlock(), this.getActiveTicks());
        updateNeighbours(p_277340_, p_277386_, p_277799_);
        tryResonateVibration(p_277529_, p_277340_, p_277386_, p_278003_);
        p_277340_.gameEvent(p_277529_, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, p_277386_);
        if (!(Boolean) p_277799_.getValue(WATERLOGGED)) {
            p_277340_.playSound(null, (double) p_277386_.getX() + 0.5, (double) p_277386_.getY() + 0.5, (double) p_277386_.getZ() + 0.5, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0F, p_277340_.random.nextFloat() * 0.2F + 0.8F);
        }
    }

    public static void tryResonateVibration(@Nullable Entity p_279315_, Level p_277804_, @NotNull BlockPos p_277458_, int p_277347_) {
        BlockState sensorState = p_277804_.getBlockState(p_277458_);
        Block sensorBlock = sensorState.getBlock();

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = p_277458_.relative(direction);
            BlockState blockstate = p_277804_.getBlockState(blockpos);
            if (blockstate.is(BlockTags.VIBRATION_RESONATORS)) {
                if (sensorBlock instanceof ColoredSculkSensorBlock coloredSensor) {
                    activateSameColorSculk(p_277804_, p_277458_, coloredSensor.getColor(), p_277347_, p_279315_);
                }
            }
        }
    }

    private static void updateNeighbours(Level level, BlockPos origin, BlockState state) {
        Block block = state.getBlock();
        int range = 8;
        if (block instanceof ColoredSculkSensorBlock sensor) {
            BlockPos.betweenClosedStream(origin.offset(-range, -range, -range), origin.offset(range, range, range)).forEach(p -> {
                BlockState state2 = level.getBlockState(p);
                Block block1 = state2.getBlock();
                if (block1 instanceof ColoredSculkSensorBlock sensor1 && sensor1.getColor() == sensor.getColor() || block1 instanceof ColoredSculkShrieker shrieker && shrieker.getColor() == sensor.getColor()) {
                    level.updateNeighborsAt(p, block1);
                    level.updateNeighborsAt(p.below(), block1);
                }
            });
        }
    }

    private static void activateSameColorSculk(Level level, BlockPos origin, DyeColor color, int frequency1, @Nullable Entity entity) {
        int range = 8;
        BlockPos.betweenClosedStream(origin.offset(-range, -range, -range), origin.offset(range, range, range)).forEach(p -> {
            BlockState state = level.getBlockState(p);
            Block block = state.getBlock();
            if (block instanceof ColoredSculkSensorBlock sensor && sensor.getColor() == color && canActivate(state)) {
                int redstoneStrength = VibrationSystem.getRedstoneStrengthForDistance(0.0F, 8);
                sensor.activate(entity, level, p, state, redstoneStrength, frequency1);
                float f = RESONANCE_PITCH_BEND[frequency1];
                level.playSound(null, p, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, f);
            }
        });
    }
}
