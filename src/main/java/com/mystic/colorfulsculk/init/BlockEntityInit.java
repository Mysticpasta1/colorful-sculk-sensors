package com.mystic.colorfulsculk.init;

import com.mystic.colorfulsculk.ColorfulSculk;
import com.mystic.colorfulsculk.blocks.ColoredSculkCatalystBlockEntity;
import com.mystic.colorfulsculk.blocks.ColoredSculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ColorfulSculk.MODID);

    public static final RegistryObject<BlockEntityType<ColoredSculkCatalystBlockEntity>> COLORED_SCULK_CATALYST =
            BLOCK_ENTITIES.register("colored_sculk_catalyst",
                    () -> BlockEntityType.Builder.of(ColoredSculkCatalystBlockEntity::new, BlockInit.getCatalystBlocks()).build(null));

    public static final RegistryObject<BlockEntityType<ColoredSculkSensorBlockEntity>> COLORED_SCULK_SENSOR =
            BLOCK_ENTITIES.register("colored_sculk_sensor",
                    () -> BlockEntityType.Builder.of(ColoredSculkSensorBlockEntity::new, BlockInit.getSensorBlocks()).build(null));

    public static void init(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
