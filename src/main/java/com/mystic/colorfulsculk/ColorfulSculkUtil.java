package com.mystic.colorfulsculk;

import com.mystic.colorfulsculk.blocks.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.mystic.colorfulsculk.init.BlockInit.*;

public class ColorfulSculkUtil {

    public static Block getColoredVariant(Block block, DyeColor color) {
        if (block == Blocks.SCULK) return COLORED_SCULK.get(color).get();
        if (block == Blocks.SCULK_VEIN) return COLORED_SCULK_VEIN.get(color).get();
        if (block == Blocks.SCULK_CATALYST) return COLORED_SCULK_CATALYST.get(color).get();
        if (block == Blocks.SCULK_SENSOR) return COLORED_SCULK_SENSOR.get(color).get();
        if (block == Blocks.SCULK_SHRIEKER) return COLORED_SCULK_SHRIEKER.get(color).get();
        return null;
    }

    public static boolean isColoredSculk(Block block) {
        return block instanceof ColoredSculk || block instanceof ColoredSculkVein || block instanceof ColoredSculkSensorBlock ||
               block instanceof ColoredSculkShrieker || block instanceof ColoredSculkCatalyst;
    }

    public static boolean isColoredSculkShriekerOrSensor(Block block) {
        return block instanceof ColoredSculk ||
                block instanceof ColoredSculkShrieker;
    }

    public static DyeColor getColoredSculkColor(Block block) {
        if (block instanceof ColoredSculkBehaviour coloured) {
            return coloured.getColor();
        }
        if (block instanceof ColoredSculkCatalyst colored) {
            return colored.getColor();
        }
        return null;
    }

    public static boolean isColoredSculkTheSameColor(Block block, DyeColor color) {
        if (block instanceof ColoredSculkBehaviour coloured) {
            return coloured.getColor() == color;
        }
        if (block instanceof ColoredSculkCatalyst colored) {
            return colored.getColor() == color;
        }
        return false;
    }
}