package com.mystic.colorfulsculk.blocks;

import com.google.common.annotations.VisibleForTesting;
import com.mystic.colorfulsculk.init.BlockEntityInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ColoredSculkCatalystBlockEntity extends BlockEntity implements GameEventListener.Holder<ColoredSculkCatalystBlockEntity.ColoredCatalystListener> {
    private final ColoredCatalystListener catalystListener;

    public ColoredSculkCatalystBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.COLORED_SCULK_CATALYST.get(), pos, state);
        this.catalystListener = new ColoredCatalystListener(state, new BlockPositionSource(pos));
    }

    public static void serverTick(Level p_222780_, BlockPos p_222781_, BlockState p_222782_, ColoredSculkCatalystBlockEntity p_222783_) {
        p_222783_.catalystListener.getSpreader().updateCursors(p_222780_, p_222781_, p_222780_.getRandom(), true);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        catalystListener.spreader.load(tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        catalystListener.spreader.save(tag);
        super.saveAdditional(tag);
    }

    @Override
    public @NotNull ColoredCatalystListener getListener() {
        return catalystListener;
    }

    public static class ColoredCatalystListener implements GameEventListener {
        public static final int RADIUS = 8;
        private final ColoredSculkSpreader spreader;
        private final BlockState state;
        private final PositionSource positionSource;

        public ColoredCatalystListener(BlockState state, PositionSource positionSource) {
            this.state = state;
            this.positionSource = positionSource;
            DyeColor color = state.getBlock() instanceof ColoredSculkCatalyst c ? c.getColor() : DyeColor.WHITE;
            this.spreader = ColoredSculkSpreader.createLevelSpreader(color);
        }

        @Override
        public @NotNull PositionSource getListenerSource() {
            return positionSource;
        }

        @Override
        public int getListenerRadius() {
            return RADIUS;
        }

        @Override
        public @NotNull DeliveryMode getDeliveryMode() {
            return DeliveryMode.BY_DISTANCE;
        }

        @Override
        public boolean handleGameEvent(@NotNull ServerLevel level, @NotNull GameEvent gameEvent, @NotNull GameEvent.Context context, @NotNull Vec3 vec3) {
            if (gameEvent == GameEvent.ENTITY_DIE) {
                Entity var6 = context.sourceEntity();
                if (var6 instanceof LivingEntity) {
                    LivingEntity $$4 = (LivingEntity)var6;
                    if (!$$4.wasExperienceConsumed()) {
                        int $$5 = $$4.getExperienceReward();
                        if ($$4.shouldDropExperience() && $$5 > 0) {
                            this.spreader.addCursors(BlockPos.containing(vec3), $$5);
                            this.tryAwardItSpreadsAdvancement(level, $$4);
                        }

                        $$4.skipDropExperience();
                        this.positionSource.getPosition(level).ifPresent((p_289513_) -> this.bloom(level, BlockPos.containing(p_289513_), this.state, level.getRandom()));
                    }

                    return true;
                }
            }

            return false;
        }

        @VisibleForTesting
        public ColoredSculkSpreader getSpreader() {
            return spreader;
        }

        private void bloom(ServerLevel p_281501_, BlockPos p_281448_, BlockState p_281966_, RandomSource p_283606_) {
            p_281501_.setBlock(p_281448_, p_281966_.setValue(ColoredSculkCatalyst.PULSE, true), 3);
            p_281501_.scheduleTick(p_281448_, p_281966_.getBlock(), 8);
            p_281501_.sendParticles(ParticleTypes.SCULK_SOUL, (double)p_281448_.getX() + 0.5D, (double)p_281448_.getY() + 1.15D, (double)p_281448_.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
            p_281501_.playSound(null, p_281448_, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + p_283606_.nextFloat() * 0.4F);
        }

        private void tryAwardItSpreadsAdvancement(Level p_281279_, LivingEntity p_281378_) {
            LivingEntity $$2 = p_281378_.getLastHurtByMob();
            if ($$2 instanceof ServerPlayer $$3) {
                DamageSource $$4 = p_281378_.getLastDamageSource() == null ? p_281279_.damageSources().playerAttack($$3) : p_281378_.getLastDamageSource();
                CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger($$3, p_281378_, $$4);
            }
        }
    }
}
