package com.mystic.colorfulsculk.blocks;

import com.mystic.colorfulsculk.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

public class ColoredSculk extends DropExperienceBlock implements ColoredSculkBehaviour {
    private final DyeColor color;

    public ColoredSculk(DyeColor color) {
        super(BlockBehaviour.Properties.of().strength(0.2F).sound(SoundType.SCULK), UniformInt.of(1, 1));
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public int attemptUseCharge(ColoredSculkSpreader.Cursor cursor, LevelAccessor level, BlockPos catalystPos, RandomSource random, ColoredSculkSpreader spreader, boolean isWorldGen) {
        int charge = cursor.getCharge();
        if (charge != 0 && random.nextInt(spreader.getChargeDecayRate()) == 0) {
            BlockPos cursorPose = cursor.getPos();
            boolean closer = cursorPose.closerThan(catalystPos, spreader.getNoGrowthRadius());
            if (!closer && canPlaceGrowth(level, cursorPose)) {
                int growthCost = spreader.getGrowthSpawnCost();
                if (random.nextInt(growthCost) < charge) {
                    BlockPos above = cursorPose.above();
                    BlockState growthState = this.getRandomGrowthState(level, above, random, isWorldGen);
                    level.setBlock(above, growthState, 3);
                    level.playSound(null, cursorPose, growthState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return Math.max(0, charge - growthCost);
            } else {
                return random.nextInt(spreader.getAdditionalDecayRate()) != 0 ? charge : charge - (closer ? 1 : getDecayPenalty(spreader, cursorPose, catalystPos, charge));
            }
        } else {
            return charge;
        }
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor var1, LevelAccessor var2, BlockPos var3, RandomSource var4, SculkSpreader var5, boolean var6) {
        int charge = var1.getCharge();
        if (charge != 0 && var4.nextInt(var5.chargeDecayRate()) == 0) {
            BlockPos cursorPose = var1.getPos();
            boolean closer = cursorPose.closerThan(var3, var5.noGrowthRadius());
            if (!closer && canPlaceGrowth(var2, cursorPose)) {
                int growthCost = var5.growthSpawnCost();
                if (var4.nextInt(growthCost) < charge) {
                    BlockPos above = cursorPose.above();
                    BlockState growthState = this.getRandomGrowthState(var2, above, var4, var5.isWorldGeneration());
                    var2.setBlock(above, growthState, 3);
                    var2.playSound(null, cursorPose, growthState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return Math.max(0, charge - growthCost);
            } else {
                return var4.nextInt(var5.additionalDecayRate()) != 0 ? charge : charge - (closer ? 1 : getDecayPenalty(var5, cursorPose, var3, charge));
            }
        } else {
            return charge;
        }
    }

    private static int getDecayPenalty(SculkSpreader p_222080_, BlockPos p_222081_, BlockPos p_222082_, int p_222083_) {
        int $$4 = p_222080_.noGrowthRadius();
        float $$5 = Mth.square((float)Math.sqrt(p_222081_.distSqr(p_222082_)) - (float)$$4);
        int $$6 = Mth.square(24 - $$4);
        float $$7 = Math.min(1.0F, $$5 / (float)$$6);
        return Math.max(1, (int)((float)p_222083_ * $$7 * 0.5F));
    }

    private static int getDecayPenalty(ColoredSculkSpreader p_222080_, BlockPos p_222081_, BlockPos p_222082_, int p_222083_) {
        int $$4 = p_222080_.getNoGrowthRadius();
        float $$5 = Mth.square((float)Math.sqrt(p_222081_.distSqr(p_222082_)) - (float)$$4);
        int $$6 = Mth.square(24 - $$4);
        float $$7 = Math.min(1.0F, $$5 / (float)$$6);
        return Math.max(1, (int)((float)p_222083_ * $$7 * 0.5F));
    }

    private BlockState getRandomGrowthState(LevelAccessor p_222068_, BlockPos p_222069_, RandomSource p_222070_, boolean p_222071_) {
        BlockState $$4;
        if (p_222070_.nextInt(11) == 0) {
            $$4 = BlockInit.COLORED_SCULK_SHRIEKER.get(color).get().defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, p_222071_);
        } else {
            $$4 = BlockInit.COLORED_SCULK_SENSOR.get(color).get().defaultBlockState();
        }

        return $$4.hasProperty(BlockStateProperties.WATERLOGGED) && !p_222068_.getFluidState(p_222069_).isEmpty() ? $$4.setValue(BlockStateProperties.WATERLOGGED, true) : $$4;
    }

    private static boolean canPlaceGrowth(LevelAccessor p_222065_, BlockPos p_222066_) {
        BlockState $$2 = p_222065_.getBlockState(p_222066_.above());
        if ($$2.isAir() || $$2.is(net.minecraft.world.level.block.Blocks.WATER) && $$2.getFluidState().is(Fluids.WATER)) {
            int $$3 = 0;

            for(BlockPos $$4 : BlockPos.betweenClosed(p_222066_.offset(-4, 0, -4), p_222066_.offset(4, 2, 4))) {
                BlockState $$5 = p_222065_.getBlockState($$4);
                if ($$5.getBlock() instanceof ColoredSculkSensorBlock || $$5.getBlock() instanceof ColoredSculkShrieker) {
                    ++$$3;
                }

                if ($$3 > 2) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canChangeBlockStateOnSpread() {
        return false;
    }
}
