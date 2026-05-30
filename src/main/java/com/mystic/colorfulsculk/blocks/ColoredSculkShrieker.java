package com.mystic.colorfulsculk.blocks;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ColoredSculkShrieker extends Block {
    private final DyeColor color;

    public ColoredSculkShrieker(DyeColor color) {
        super(BlockBehaviour.Properties.of().strength(0.2F).sound(SoundType.SCULK));
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }
}
