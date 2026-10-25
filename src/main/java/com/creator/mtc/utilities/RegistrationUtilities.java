package com.creator.mtc.utilities;

import com.creator.mtc.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles registrations for the mod.
 */
public class RegistrationUtilities {
    /**
     * This is our registry of {@link DeferredRegister.Blocks} for the mod, basically where all the Blocks are stored.
     */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Main.MODID);
    /**
     * This is our registry of {@link DeferredRegister.Items} for the mod, basically where all the Items are stored.
     */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Main.MODID);
    /**
     * This is our registry of {@link DeferredRegister DeferredRegister&lt;CreativeModeTab&gt;} for the mod, basically where all the Creative Mode Tabs are stored.
     */
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);

    /**
     * This function handles the Registry of Blocks (phase 1), it is essentially a function that handles registering multiple blocks in one go.
     * @param records This function takes a {@link RegistrationRecords.RegistrationBlockRecord} array of records to be parsed through.
     * @return This function returns a {@link List List&lt;DeferredBlock&gt;} specifically a {@link DeferredBlock DeferredBlock&lt;Block&gt;}.
     */
    public static List<DeferredBlock<Block>> RegisterBlocks(RegistrationRecords.RegistrationBlockRecord[] records) {
        List<DeferredBlock<Block>> blocks = new ArrayList<>();
        for (RegistrationRecords.RegistrationBlockRecord record : records) {
            blocks.add(BLOCKS.registerSimpleBlock(record.internalName(), record.properties()));
        }
        return blocks;
    }

    /**
     * This function handles creating the {@link RegistrationRecords.RegistrationBlockItemRecord} for each block from {@link #RegisterBlocks(RegistrationRecords.RegistrationBlockRecord[])}.
     * @param blocks The Output of {@link #RegisterBlocks(RegistrationRecords.RegistrationBlockRecord[])}.
     * @return An Array of {@link RegistrationRecords.RegistrationBlockItemRecord}.
     */
    public static RegistrationRecords.RegistrationBlockItemRecord[] CreateRegistrationBlockItemRecordsArray (List<DeferredBlock<Block>> blocks) {
        List<RegistrationRecords.RegistrationBlockItemRecord> records = new ArrayList<>();
        for (DeferredBlock<Block> block : blocks) {
            records.add(new RegistrationRecords.RegistrationBlockItemRecord(block.getRegisteredName(), block));
        }
        return records.toArray(new RegistrationRecords.RegistrationBlockItemRecord[0]);
    }

    /**
     * This function handles the Registry of Blocks (phase 2), it is essentially a function that handles registering multiple items for the prior blocks in one go.
     * @param records This function takes a {@link RegistrationRecords.RegistrationBlockItemRecord} array of records to be parsed through.
     * @return This function returns a {@link List List&lt;DeferredItem&gt;} specifically a {@link DeferredItem DeferredItem&lt;BlockItem&gt;}.
     */
    public static List<DeferredItem<BlockItem>> RegisterBlocks(RegistrationRecords.RegistrationBlockItemRecord[] records) {
        List<DeferredItem<BlockItem>> items = new ArrayList<>();
        for (RegistrationRecords.RegistrationBlockItemRecord record : records) {
            items.add(ITEMS.registerSimpleBlockItem(record.internalName(), record.block()));
        }
        return items;
    }

    /**
     * This function handles the Registry of Items, it is essentially a function that handles registering multiple items in one go.
     * @param records This function takes a {@link RegistrationRecords.RegistrationItemRecord} array of records to be parsed through.
     * @return This function returns a {@link List List&lt;DeferredItem&gt;} specifically a {@link DeferredItem DeferredItem&lt;Item&gt;}.
     */
    public static List<DeferredItem<Item>> RegisterItems(RegistrationRecords.RegistrationItemRecord[] records) {
        List<DeferredItem<Item>> items = new ArrayList<>();
        for (RegistrationRecords.RegistrationItemRecord record : records) {
            items.add(ITEMS.registerSimpleItem(record.internalName(), record.properties()));
        }
        return items;
    }

    /**
     * This function handles the Registry of Creative Tabs, it is essentially a function that handles registering multiple Creative Tabs in one go.
     * @param records This function takes a {@link RegistrationRecords.RegistrationCreativeTabRecord} array of records to be parsed through.
     * @return This function returns a {@link List List&lt;DeferredHolder&gt;} specifically a {@link DeferredHolder DeferredHolder&lt;CreativeModeTab, CreativeModeTab&gt;}.
     */
    public static List<DeferredHolder<CreativeModeTab, CreativeModeTab>> RegisterCreativeTabs (RegistrationRecords.RegistrationCreativeTabRecord[] records) {
        List<DeferredHolder<CreativeModeTab, CreativeModeTab>> tabs = new ArrayList<>();
        for (RegistrationRecords.RegistrationCreativeTabRecord record : records) {
            tabs.add(CREATIVE_MODE_TABS.register(record.internalName(), record.creativeTab()));
        }
        return tabs;
    }
}
