package com.mystic.colorfulsculk.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

public class ColoredSculkSensorBlockEntity extends SculkSensorBlockEntity {

    public ColoredSculkSensorBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public VibrationSystem.User createVibrationUser() {
        return new ColoredVibrationUser(this.getBlockPos());
    }

    protected class ColoredVibrationUser extends VibrationUser {
        public ColoredVibrationUser(BlockPos pos) {
            super(pos);
        }

        @Override
        public void onReceiveVibration(ServerLevel level, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity projectileOwner, float distance) {
            BlockState blockstate = getBlockState();
            if (ColoredSculkSensorBlock.canActivate(blockstate)) {
                Block block = blockstate.getBlock();
                if (!(block instanceof ColoredSculkSensorBlock coloredBlock)) {
                    return;
                }
                DyeColor myColor = coloredBlock.getColor();
                if (!isMatchingColorSource(level, pos, myColor)) {
                    return;
                }

                setLastVibrationFrequency(VibrationSystem.getGameEventFrequency(event));
                int i = VibrationSystem.getRedstoneStrengthForDistance(distance, this.getListenerRadius());
                coloredBlock.activate(entity, level, this.blockPos, blockstate, i, getLastVibrationFrequency());
            }
        }

        private static boolean isMatchingColorSource(Level level, BlockPos pos, DyeColor color) {
            BlockState sourceState = level.getBlockState(pos);
            Block sourceBlock = sourceState.getBlock();
            if (sourceBlock instanceof ColoredSculkSensorBlock sourceSensor) {
                return sourceSensor.getColor() == color;
            }
            if (sourceBlock instanceof SculkSensorBlock) {
                return true;
            }
            for (Direction dir : Direction.values()) {
                BlockPos adjacentPos = pos.relative(dir);
                BlockState adjacentState = level.getBlockState(adjacentPos);
                Block adjacentBlock = adjacentState.getBlock();
                if (adjacentBlock instanceof SculkSensorBlock) {
                    return true;
                }
                if (adjacentBlock instanceof ColoredSculkSensorBlock sensor && sensor.getColor() == color) {
                    return true;
                }
            }
            return false;
        }
    }
}
