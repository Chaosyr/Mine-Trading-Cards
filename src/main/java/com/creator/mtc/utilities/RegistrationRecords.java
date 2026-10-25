package com.creator.mtc.utilities;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Supplier;

/**
 * This is a class that defines Records to be used alongside {@link RegistrationUtilities} and its functions.
 */
public class RegistrationRecords {
    /**
     * This is a record that defines Blocks.
     * @param internalName The Blocks internal Name.
     * @param properties The Blocks Properties.
     */
    public record RegistrationBlockRecord(String internalName, Supplier<BlockBehaviour.Properties> properties) {}
    /**
     * This is a record that defines Block Items.
     * @param internalName The Block Items internal Name.
     * @param block The Blocks Block.
     */
    public record RegistrationBlockItemRecord(String internalName, DeferredBlock<Block> block) {}

    /**
     * This is a record that defines Items.
     * @param internalName The Items Internal Name.
     * @param properties The Items Properties.
     */
    public record RegistrationItemRecord(String internalName, Supplier<Item.Properties> properties) {}

    /**
     * This is a record that defines Creative Tabs.
     * @param internalName The Tabs Internal Name.
     * @param creativeTab The Tabs Properties.
     */
    public record RegistrationCreativeTabRecord(String internalName, Supplier<CreativeModeTab> creativeTab) {}
}
