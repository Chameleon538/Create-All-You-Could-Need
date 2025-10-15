package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "examplemod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // A smithing template item so players can apply our custom trim (registered so the recipe and assets match)
    // The smithing template must reference a trim pattern resource (the template item shows pattern info),
    // so use the pattern id (examplemod:simple_stripe) here.
    public static final RegistryObject<Item> COPPER_LIKE_SMITHING_TEMPLATE = ITEMS.register("copper_like_smithing_template", () -> net.minecraft.world.item.SmithingTemplateItem.createArmorTrimTemplate(new net.minecraft.resources.ResourceLocation(MODID, "simple_stripe")));

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(COPPER_LIKE_SMITHING_TEMPLATE.get());
            }).build());

    public ExampleMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");

        // Diagnostic: check whether our trim pattern and material are present in the server registries
        try {
            net.minecraft.server.MinecraftServer server = event.getServer();
            net.minecraft.core.RegistryAccess registryAccess = server.registryAccess();

            net.minecraft.core.Registry<net.minecraft.world.item.armortrim.TrimPattern> patternRegistry = registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.TRIM_PATTERN);
            net.minecraft.core.Registry<net.minecraft.world.item.armortrim.TrimMaterial> materialRegistry = registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.TRIM_MATERIAL);

            net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimPattern> patternKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.TRIM_PATTERN, new net.minecraft.resources.ResourceLocation(MODID, "simple_stripe"));
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimMaterial> materialKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.TRIM_MATERIAL, new net.minecraft.resources.ResourceLocation(MODID, "copper_like"));

            boolean patternPresent = patternRegistry.getHolder(patternKey).isPresent();
            boolean materialPresent = materialRegistry.getHolder(materialKey).isPresent();

            LOGGER.info("Trim pattern '{}' present: {}", patternKey.location(), patternPresent);
            LOGGER.info("Trim material '{}' present: {}", materialKey.location(), materialPresent);
        } catch (Exception e) {
            LOGGER.warn("Failed to check trim registries at server start", e);
        }
    }

    // Additional lifecycle hook: runs after the server has fully started and data packs are loaded
    @SubscribeEvent
    public void onServerStarted(net.minecraftforge.event.server.ServerStartedEvent event) {
        try {
            net.minecraft.server.MinecraftServer server = event.getServer();
            net.minecraft.core.RegistryAccess registryAccess = server.registryAccess();

            net.minecraft.core.Registry<net.minecraft.world.item.armortrim.TrimPattern> patternRegistry = registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.TRIM_PATTERN);
            net.minecraft.core.Registry<net.minecraft.world.item.armortrim.TrimMaterial> materialRegistry = registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.TRIM_MATERIAL);

            net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimPattern> patternKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.TRIM_PATTERN, new net.minecraft.resources.ResourceLocation(MODID, "simple_stripe"));
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimMaterial> materialKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.TRIM_MATERIAL, new net.minecraft.resources.ResourceLocation(MODID, "copper_like"));

            boolean patternPresent = patternRegistry.getHolder(patternKey).isPresent();
            boolean materialPresent = materialRegistry.getHolder(materialKey).isPresent();

            LOGGER.info("[ServerStarted] Trim pattern '{}' present: {}", patternKey.location(), patternPresent);
            LOGGER.info("[ServerStarted] Trim material '{}' present: {}", materialKey.location(), materialPresent);
            // Dump all registered trim patterns and materials for diagnosis
            try {
                LOGGER.info("[ServerStarted] Dumping all trim patterns:");
                for (net.minecraft.resources.ResourceLocation loc : patternRegistry.keySet()) {
                    LOGGER.info("  pattern: {}", loc);
                }

                LOGGER.info("[ServerStarted] Dumping all trim materials:");
                for (net.minecraft.resources.ResourceLocation loc : materialRegistry.keySet()) {
                    LOGGER.info("  material: {}", loc);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to dump trim registries", e);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to check trim registries at server started", e);
        }
    }

    // Give the player some test items when they log in so they can apply the trim easily
    @SubscribeEvent
    public void onPlayerLogin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            try {
                ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
                ItemStack template = new ItemStack(COPPER_LIKE_SMITHING_TEMPLATE.get());
                ItemStack copper = new ItemStack(Items.COPPER_INGOT, 64);

                // Try to construct an ArmorTrim for our custom material/pattern and apply it to the chestplate
                try {
                    // Look up the pattern and material in the registry by ResourceLocation
                    // Get the server and registry access
                    net.minecraft.server.MinecraftServer server = serverPlayer.getServer();
                    net.minecraft.core.RegistryAccess registryAccess = server.registryAccess();

                    net.minecraft.core.Registry<net.minecraft.world.item.armortrim.TrimPattern> patternRegistry = registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.TRIM_PATTERN);
                    net.minecraft.core.Registry<net.minecraft.world.item.armortrim.TrimMaterial> materialRegistry = registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.TRIM_MATERIAL);

                    // Obtain holders for our custom pattern and material (throws if missing)
                    net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimPattern> patternKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.TRIM_PATTERN, new net.minecraft.resources.ResourceLocation(MODID, "simple_stripe"));
                    net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimMaterial> materialKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.TRIM_MATERIAL, new net.minecraft.resources.ResourceLocation(MODID, "copper_like"));

                    // Use non-throwing holder lookup so we can log presence without exceptions
                    java.util.Optional<net.minecraft.core.Holder.Reference<net.minecraft.world.item.armortrim.TrimPattern>> patternOpt = patternRegistry.getHolder(patternKey);
                    java.util.Optional<net.minecraft.core.Holder.Reference<net.minecraft.world.item.armortrim.TrimMaterial>> materialOpt = materialRegistry.getHolder(materialKey);

                    if (patternOpt.isPresent() && materialOpt.isPresent()) {
                        net.minecraft.core.Holder.Reference<net.minecraft.world.item.armortrim.TrimPattern> patternHolder = patternOpt.get();
                        net.minecraft.core.Holder.Reference<net.minecraft.world.item.armortrim.TrimMaterial> materialHolder = materialOpt.get();

                        // Construct ArmorTrim with the holders (material first, pattern second)
                        net.minecraft.world.item.armortrim.ArmorTrim armorTrim = new net.minecraft.world.item.armortrim.ArmorTrim(materialHolder, patternHolder);

                        // Apply the trim to the chestplate using ArmorTrim.setTrim
                        boolean applied = net.minecraft.world.item.armortrim.ArmorTrim.setTrim(server.registryAccess(), chestplate, armorTrim);
                        LOGGER.info("Attempted to apply armor trim: applied={}", applied);
                    } else {
                        LOGGER.warn("Could not find trim pattern '{}' present={} or material '{}' present={}: skipping apply", patternKey.location(), patternOpt.isPresent(), materialKey.location(), materialOpt.isPresent());
                    }
                } catch (Exception inner) {
                    LOGGER.error("Failed to construct or apply ArmorTrim", inner);
                }

                serverPlayer.getInventory().add(chestplate);
                serverPlayer.getInventory().add(template);
                serverPlayer.getInventory().add(copper);
            } catch (Exception e) {
                LOGGER.error("Failed to give test items on login", e);
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
