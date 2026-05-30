package com.mystic.colorfulsculk;

import com.mystic.colorfulsculk.init.BlockEntityInit;
import com.mystic.colorfulsculk.init.BlockInit;
import com.mystic.colorfulsculk.init.ItemInit;
import com.mystic.colorfulsculk.init.SculkTabs;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

import net.minecraftforge.registries.RegistryObject;

import static com.mystic.colorfulsculk.init.BlockInit.*;

@Mod(ColorfulSculk.MODID)
public class ColorfulSculk {

    public static final String MODID = "colorfulsculk";

    public ColorfulSculk(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        BlockInit.init(bus);
        ItemInit.init(bus);
        BlockEntityInit.init(bus);
        SculkTabs.init(bus);
        bus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityType.SCULK_SHRIEKER.validBlocks = new HashSet<>(BlockEntityType.SCULK_SHRIEKER.validBlocks);
            BlockEntityType.SCULK_SHRIEKER.validBlocks.addAll(Arrays.asList(getShriekerBlocks()));
            BlockEntityType.SCULK_SENSOR.validBlocks = new HashSet<>(BlockEntityType.SCULK_SENSOR.validBlocks);
            BlockEntityType.SCULK_SENSOR.validBlocks.addAll(Arrays.asList(getSensorBlocks()));
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            registerBlockRenderLayers(RenderType.cutout(),
                    SCULK_TORCH.get(),
                    SCULK_WALL_TORCH.get());

            for (RegistryObject<Block> block : COLORED_SCULK_VEIN.values()) {
                registerBlockRenderLayers(RenderType.cutout(), block.get());
            }

            for (RegistryObject<Block> block : COLORED_SCULK_SHRIEKER.values()) {
                registerBlockRenderLayers(RenderType.cutout(), block.get());
            }

            for (RegistryObject<Block> block : COLORED_SCULK_SENSOR.values()) {
                registerBlockRenderLayers(RenderType.cutout(), block.get());
            }

            for (RegistryObject<Block> block : COLORED_SCULK_CATALYST.values()) {
                registerBlockRenderLayers(RenderType.cutout(), block.get());
            }
        }

        private static void registerBlockRenderLayers(RenderType layer, Block... blocks) {
            Stream.of(blocks).forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, layer));
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerItemColor(RegisterColorHandlersEvent.Item event) {
            ItemColors colors = event.getItemColors();
            ItemColor[] itemColors = getItemColors();
            DyeColor[] dyeColors = DyeColor.values();
            for (int i = 0; i < dyeColors.length; i++) {
                colors.register(itemColors[i],
                        COLORED_SCULK.get(dyeColors[i]).get(),
                        COLORED_SCULK_VEIN.get(dyeColors[i]).get(),
                        COLORED_SCULK_CATALYST.get(dyeColors[i]).get(),
                        COLORED_SCULK_SHRIEKER.get(dyeColors[i]).get(),
                        COLORED_SCULK_SENSOR.get(dyeColors[i]).get());
            }
        }

        private static ItemColor @NotNull [] getItemColors() {
            ItemColor WHITE = (arg, i) -> 0xf9fffe;
            ItemColor ORANGE = (arg, i) -> 0xf9801d;
            ItemColor MAGENTA = (arg, i) -> 0xc74ebd;
            ItemColor LIGHT_BLUE = (arg, i) -> 0x3ab3da;
            ItemColor YELLOW = (arg, i) -> 0xfed83d;
            ItemColor LIME = (arg, i) -> 0x80c71f;
            ItemColor PINK = (arg, i) -> 0xf38baa;
            ItemColor GRAY = (arg, i) -> 0x474f52;
            ItemColor LIGHT_GRAY = (arg, i) -> 0x9d9d97;
            ItemColor CYAN = (arg, i) -> 0x169c9c;
            ItemColor PURPLE = (arg, i) -> 0x8932b8;
            ItemColor BLUE = (arg, i) -> 0x3c44aa;
            ItemColor BROWN = (arg, i) -> 0x835432;
            ItemColor GREEN = (arg, i) -> 0x5e7c16;
            ItemColor RED = (arg, i) -> 0xb02e26;
            ItemColor BLACK = (arg, i) -> 0x1d1d21;

            return new ItemColor[]{WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK};
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerBlockColor(RegisterColorHandlersEvent.Block event) {
            BlockColors blockColors = event.getBlockColors();
            BlockColor[] blockColorsArr = getBlockColors();
            DyeColor[] dyeColors = DyeColor.values();
            for (int i = 0; i < dyeColors.length; i++) {
                blockColors.register(blockColorsArr[i],
                        COLORED_SCULK.get(dyeColors[i]).get(),
                        COLORED_SCULK_VEIN.get(dyeColors[i]).get(),
                        COLORED_SCULK_CATALYST.get(dyeColors[i]).get(),
                        COLORED_SCULK_SHRIEKER.get(dyeColors[i]).get(),
                        COLORED_SCULK_SENSOR.get(dyeColors[i]).get());
            }
        }

        private static BlockColor @NotNull [] getBlockColors() {
            BlockColor WHITE = (arg, arg2, arg3, i) -> 0xf9fffe;
            BlockColor ORANGE = (arg, arg2, arg3, i) -> 0xf9801d;
            BlockColor MAGENTA = (arg, arg2, arg3, i) -> 0xc74ebd;
            BlockColor LIGHT_BLUE = (arg, arg2, arg3, i) -> 0x3ab3da;
            BlockColor YELLOW = (arg, arg2, arg3, i) -> 0xfed83d;
            BlockColor LIME = (arg, arg2, arg3, i) -> 0x80c71f;
            BlockColor PINK = (arg, arg2, arg3, i) -> 0xf38baa;
            BlockColor GRAY = (arg, arg2, arg3, i) -> 0x474f52;
            BlockColor LIGHT_GRAY = (arg, arg2, arg3, i) -> 0x9d9d97;
            BlockColor CYAN = (arg, arg2, arg3, i) -> 0x169c9c;
            BlockColor PURPLE = (arg, arg2, arg3, i) -> 0x8932b8;
            BlockColor BLUE = (arg, arg2, arg3, i) -> 0x3c44aa;
            BlockColor BROWN = (arg, arg2, arg3, i) -> 0x835432;
            BlockColor GREEN = (arg, arg2, arg3, i) -> 0x5e7c16;
            BlockColor RED = (arg, arg2, arg3, i) -> 0xb02e26;
            BlockColor BLACK = (arg, arg2, arg3, i) -> 0x1d1d21;

            return new BlockColor[]{WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK};
        }
    }
}
