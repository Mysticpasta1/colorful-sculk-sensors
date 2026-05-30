package com.mystic.colorfulsculk.blocks;

import com.mystic.colorfulsculk.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public interface ColoredSculkBehaviour {
    Map<DyeColor, ColoredSculkBehaviour> DEFAULTS = new EnumMap<>(DyeColor.class);

    static ColoredSculkBehaviour getDefaultForColor(DyeColor color) {
        return DEFAULTS.computeIfAbsent(color, (c) -> new ColoredSculkBehaviour() {
            @Override
            public DyeColor getColor() {
                return c;
            }

            @Override
            public int attemptUseCharge(ColoredSculkSpreader.Cursor cursor, LevelAccessor level, BlockPos catalystPos, RandomSource random, ColoredSculkSpreader spreader, boolean isWorldGen) {
                return cursor.getCharge() - getDecay(cursor.getCharge());
            }

            @Override
            public boolean attemptSpreadVein(LevelAccessor p_222048_, BlockPos p_222049_, BlockState p_222050_, @Nullable Collection<Direction> p_222051_, boolean p_222052_) {
                if (p_222051_ == null) {
                    return ((ColoredSculkVein) BlockInit.COLORED_SCULK_VEIN.get(this.getColor()).get()).getSameSpaceSpreader().spreadAll(p_222048_.getBlockState(p_222049_), p_222048_, p_222049_, p_222052_) > 0L;
                } else if (!p_222051_.isEmpty()) {
                    return !p_222050_.isAir() && !p_222050_.getFluidState().is(Fluids.WATER) ? false : ColoredSculkVein.regrow(p_222048_, p_222049_, p_222050_, p_222051_, this.getColor());
                } else {
                    return ColoredSculkBehaviour.super.attemptSpreadVein(p_222048_, p_222049_, p_222050_, p_222051_, p_222052_);
                }
            }

            @Override
            public int attemptUseCharge(SculkSpreader.ChargeCursor p_222054_, LevelAccessor p_222055_, BlockPos p_222056_, RandomSource p_222057_, SculkSpreader p_222058_, boolean p_222059_) {
                return p_222054_.getDecayDelay() > 0 ? p_222054_.getCharge() : 0;
            }

            @Override
            public int updateDecayDelay(int p_222061_) {
                return Math.max(p_222061_ - 1, 0);
            }
        });
    }

    DyeColor getColor();

    int attemptUseCharge(ColoredSculkSpreader.Cursor cursor, LevelAccessor level, BlockPos catalystPos, RandomSource random, ColoredSculkSpreader spreader, boolean isWorldGen);

    default int getDecay(int charge) {
        return 1;
    }

    default boolean canChangeBlockStateOnSpread() {
        return true;
    }

    default byte getSculkSpreadDelay() {
        return 1;
    }

    default void onDischarged(LevelAccessor p_222026_, BlockState p_222027_, BlockPos p_222028_, RandomSource p_222029_) {
    }

    default boolean depositCharge(LevelAccessor p_222031_, BlockPos p_222032_, RandomSource p_222033_) {
        return false;
    }

    default boolean attemptSpreadVein(LevelAccessor p_222034_, BlockPos p_222035_, BlockState p_222036_, @Nullable Collection<Direction> p_222037_, boolean p_222038_) {
        return ((MultifaceBlock) BlockInit.COLORED_SCULK_VEIN.get(this.getColor()).get()).getSpreader().spreadAll(p_222036_, p_222034_, p_222035_, p_222038_) > 0L;
    }

    default int updateDecayDelay(int p_222045_) {
        return 1;
    }

    int attemptUseCharge(SculkSpreader.ChargeCursor var1, LevelAccessor var2, BlockPos var3, RandomSource var4, SculkSpreader var5, boolean var6);
}
