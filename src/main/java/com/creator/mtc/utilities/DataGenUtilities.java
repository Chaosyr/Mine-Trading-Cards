package com.creator.mtc.utilities;

import com.creator.mtc.Main;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that handles the underlying DataGenUtilities
 */
@EventBusSubscriber(modid = Main.MODID)
public class DataGenUtilities {
    /**
     * Grabs the Client Data and shoves our Generators in.
     * @param event The {@link GatherDataEvent.Client} event.
     */
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        generator.addProvider(true, new MTCModelProvider(packOutput));
        generator.addProvider(true, new MTCLanguageProvider(packOutput));
    }
}

/**
 * This is a class that handles the Model Providing for our Models.
 */
class MTCModelProvider extends ModelProvider {
    /**
     * The Constructor for our Model Provider
     * @param output A {@link PackOutput} which is associated with the mod's DataGenerator as used in {@link DataGenUtilities}.
     */
    public MTCModelProvider (PackOutput output) {
        super(output, Main.MODID);
    }

    /**
     * Data Generates our Models.
     * @param blockModelGenerators The Block Model Generators.
     * @param itemModelGenerators The Item Model Generators.
     */
    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModelGenerators, @NonNull ItemModelGenerators itemModelGenerators) {
        for (var item : RegistrationUtilities.ITEMS.getEntries()) {
            itemModelGenerators.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
        }

        for (var block : RegistrationUtilities.BLOCKS.getEntries()) {
            blockModelGenerators.createTrivialCube(block.get());
        }
    }
}

/**
 * This is a class that handles the Language Providing for EN_US universally.
 */
class MTCLanguageProvider extends LanguageProvider {
    /**
     * The Constructor for our Language Provider
     * @param output A {@link PackOutput} which is associated with the mod's DataGenerator as used in {@link DataGenUtilities}.
     */
    public MTCLanguageProvider (PackOutput output) {
        super(output, Main.MODID, "en_us");
    }

    /**
     * Data Generates our English Translations.
     */
    @Override
    protected void addTranslations() {
        DataGenLangFunctions.DataGenItems(RegistrationUtilities.ITEMS.getEntries().stream().toList());
        DataGenLangFunctions.DataGenBlocks(RegistrationUtilities.BLOCKS.getEntries().stream().toList());
        DataGenLangFunctions.DataGenTabs(RegistrationUtilities.CREATIVE_MODE_TABS.getEntries().stream().toList());
    }
}

/**
 * This is a class that adds some Utility Functions for translating our Mod's Content.
 */
class DataGenLangFunctions {
    /**
     * This is what we call a DataGenField, basically its a pair of Internal to External values used in Translations.
     * @param internal The Internal Key of the Content Item.
     * @param external The External Value of the Content Item.
     */
    public record DataGenField (String internal, String external) {}

    /**
     * A function for Data Generating Blocks
     * @param blocks A {@link List List&lt;DeferredHolder&lt;Block, ? extends Block&gt;&gt;} which we pass through to generate the DataGenField Records Array.
     * @return A {@link List List&lt;DataGenField&gt;} for doing translations of Blocks.
     */
    public static List<DataGenField> DataGenBlocks(List<DeferredHolder<Block, ? extends Block>> blocks) {
        List<DataGenField> fields = new ArrayList<>();
        for (var block : blocks) {
            fields.add(new DataGenField(String.format("%s.%s.%s", "block", Main.MODID, block.getRegisteredName()), StringExtenders.convertToPlainLang(block.getRegisteredName())));
        }
        return fields;
    }

    /**
     * A function for Data Generating Items
     * @param items A {@link List List&lt;DeferredHolder&lt;Item, ? extends Item&gt;&gt;} which we pass through to generate the DataGenField Records Array.
     * @return A {@link List List&lt;DataGenField&gt;} for doing translations of Items.
     */
    public static List<DataGenField> DataGenItems(List<DeferredHolder<Item, ? extends Item>> items) {
        List<DataGenField> fields = new ArrayList<>();
        for (var item : items) {
            fields.add(new DataGenField(String.format("%s.%s.%s", "item", Main.MODID, item.getRegisteredName()), StringExtenders.convertToPlainLang(item.getRegisteredName())));
        }
        return fields;
    }

    /**
     * A function for Data Generating Tabs
     * @param tabs A {@link List List&lt;DeferredHolder&lt;CreativeModeTab, ? extends CreativeModeTab&gt;&gt;} which we pass through to generate the DataGenField Records Array.
     * @return A {@link List List&lt;DataGenField&gt;} for doing translations of Tabs.
     */
    public static List<DataGenField> DataGenTabs (List<DeferredHolder<CreativeModeTab, ? extends CreativeModeTab>> tabs) {
        List<DataGenField> fields = new ArrayList<>();
        for (var tab : tabs) {
            fields.add(new DataGenField(String.format("%s.%s.%s", "itemGroup", Main.MODID, tab.getRegisteredName()), StringExtenders.convertToPlainLang(tab.getRegisteredName())));
        }
        return fields;
    }
}

/**
 * This is a class with some String Extenders we use for Translations.
 */
class StringExtenders {
    /**
     * This function converts the passed string into something like "Plain Text Name" from the original "plain_text_name".
     * @param string The String in which to convert.
     * @return The updated string.
     */
    public static String convertToPlainLang(String string) {
        String[] stringParts = string.split("_");
        String toReturn = "";
        for (String stringPart : stringParts) {
            String firstChar = "";
            if (stringPart.isEmpty()) {
                Main.LOGGER.warn("Hey, when converting the following string, there was consecutive underscores: " + string);
                continue;
            }
            firstChar = stringPart.substring(0, 1).toUpperCase();
            toReturn += firstChar + stringPart.substring(1) + " ";
        }
        return toReturn.trim();
    }
}