package com.chaosyr.mtc.peripheral.file_loader;

import com.chaosyr.mtc.MineTradingCardsShared;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is a class that handles reading our MTC Packs.
 */
public class MTCPackReader {
    /**
     * The MTC Packs Folder.
     */
    private static final File contentFolder = new File(MineTradingCardsShared.getDataDir());
    /**
     * The MTC Pack Zips
     */
    private static final List<String> contentZips = new ArrayList<>();
    /**
     * The MTC Pack Directories
     */
    private static final List<String> contentFolders = new ArrayList<>();

    /**
     * Handles the Reading of the MTC Pack Folders and returns the associated Files.
     * @param serverOrClient True for Server, False for Client.
     * @return This will return in the form of {@link MTCPackFolders}. This will return null if either No Content Packs were found, or No Editions/Cards were found.
     */
    public static MTCPackFolders ReadMTCPacksFolder(boolean serverOrClient) {
        MineTradingCardsShared.LOGGER.info("[MTC]: We are now assembling your valid Content Packs into our systems!");
        contentZips.clear();
        contentFolders.clear();

        if (contentFolder.exists() && contentFolder.isDirectory()) {
            try {
                for (File contentPack : Objects.requireNonNull(contentFolder.listFiles())) {
                    if (contentPack.isFile() && contentPack.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
                        contentZips.add(contentPack.getPath());
                        continue;
                    }
                    if (contentPack.exists() && contentPack.isDirectory()) {
                        contentFolders.add(contentPack.getPath());
                    }
                }
            } catch (NullPointerException e) {
                MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list MTC Packs from folder: " + contentFolder.getAbsolutePath());
                MineTradingCardsShared.LOGGER.error(e.getMessage());
            }
        }

        if (contentFolders.isEmpty()) {
            MineTradingCardsShared.LOGGER.warn("[MTC]: No MTC Packs found by which are Directories Skipping Load of Directories.");
        }

        if (contentZips.isEmpty()) {
            MineTradingCardsShared.LOGGER.warn("[MTC]: No MTC Packs found by which are Zipped Skipping Load of Zips.");
        }

        if (contentFolders.isEmpty() && contentZips.isEmpty()) {
            MineTradingCardsShared.LOGGER.error("[MTC]: If you are not developing your own and would like some, please go to the discord: https://discord.gg/TWDcAaX");
            MineTradingCardsShared.LOGGER.error("[MTC]: There is also the Notion Repository started by Chaosyr with Packs found here: https://mtc-cards.notion.site/3eda473cb0eb466e9bd90fdbfe2af5cd?v=c677ebc7cfc0416d9b36422a8918a2af");
            MineTradingCardsShared.LOGGER.error("[MTC]: Your MTC Packs Folder is here: " + contentFolder.getAbsolutePath());
            return null;
        }

        MineTradingCardsShared.LOGGER.info("[MTC]: We are now reading into your Content Packs from Subfolders (and Zips if found)");

        MTCPackFolders mtcPackFolders = ReadMTCPackContents(Stream.concat(contentFolders.stream(), contentZips.stream()).toList());

        if (mtcPackFolders.assets.isEmpty() && mtcPackFolders.zipAssets.isEmpty()) {
            if (serverOrClient) {
                MineTradingCardsShared.LOGGER.warn("[MTC]: No Assets folders found. Expected to be present on Client, but ideally have your Assets on the Server as well.");
                MineTradingCardsShared.LOGGER.warn("[MTC]: Expected path for Assets is: <Your MTC Pack>/assets/is_mtc/mtc/assets/");
                MineTradingCardsShared.LOGGER.warn("[MTC]: Your MTC Packs Folder is here: " + contentFolder.getAbsolutePath());
            } else {
                MineTradingCardsShared.LOGGER.error("[MTC]: No Assets folders found. Due to this your cards will be missing their Assets.");
                MineTradingCardsShared.LOGGER.error("[MTC]: Expected path for Assets is: <Your MTC Pack>/assets/is_mtc/mtc/assets/");
                MineTradingCardsShared.LOGGER.error("[MTC]: Your MTC Packs Folder is here: " + contentFolder.getAbsolutePath());
            }
        }
        if (mtcPackFolders.cards.isEmpty() && mtcPackFolders.zipCards.isEmpty()) {
            MineTradingCardsShared.LOGGER.error("[MTC]: No Cards folders found.");
            MineTradingCardsShared.LOGGER.error("[MTC]: Expected path for Cards is: <Your MTC Pack>/assets/is_mtc/mtc/cards/");
            MineTradingCardsShared.LOGGER.error("[MTC]: Your MTC Packs Folder is here: " + contentFolder.getAbsolutePath());
        }
        if (mtcPackFolders.editions.isEmpty() && mtcPackFolders.zipEditions.isEmpty()) {
            MineTradingCardsShared.LOGGER.error("[MTC]: No Editions folders found.");
            MineTradingCardsShared.LOGGER.error("[MTC]: Expected path for Editions is: <Your MTC Pack>/assets/is_mtc/mtc/editions/");
            MineTradingCardsShared.LOGGER.error("[MTC]: Your MTC Packs Folder is here: " + contentFolder.getAbsolutePath());
        }
        if (mtcPackFolders.packs.isEmpty() && mtcPackFolders.zipPacks.isEmpty()) {
            MineTradingCardsShared.LOGGER.warn("[MTC]: No Custom Packs folders found.");
            MineTradingCardsShared.LOGGER.warn("[MTC]: Expected path for Custom Packs is: <Your MTC Pack>/assets/is_mtc/mtc/packs/");
            MineTradingCardsShared.LOGGER.warn("[MTC]: Your MTC Packs Folder is here: " + contentFolder.getAbsolutePath());
        }

        if ((mtcPackFolders.editions.isEmpty() && mtcPackFolders.zipEditions.isEmpty()) || (mtcPackFolders.cards.isEmpty() && mtcPackFolders.zipCards.isEmpty())) {
            MineTradingCardsShared.LOGGER.error("[MTC]: Found Nothing to Add, Skipping Loading Phase.");
            return null;
        }

        return mtcPackFolders;
    }

    /**
     * This is a Record for passing the MTCPackFolders Entries.
     * @param assets An {@link ArrayList ArrayList&lt;File&gt;} representing the Asset Files found outside of Zips.
     * @param zipAssets An {@link ArrayList ArrayList&lt;ZipAssociations&gt;} representing the Asset Files found in Zips.
     * @param cards An {@link ArrayList ArrayList&lt;File&gt;} representing the Card Files found outside of Zips.
     * @param zipCards An {@link ArrayList ArrayList&lt;ZipAssociations&gt;} representing the Card Files found in Zips.
     * @param editions An {@link ArrayList ArrayList&lt;File&gt;} representing the Edition Files found outside of Zips.
     * @param zipEditions An {@link ArrayList ArrayList&lt;ZipAssociations&gt;} representing the Edition Files found in Zips.
     * @param packs An {@link ArrayList ArrayList&lt;File&gt;} representing the Pack Files found outside of Zips.
     * @param zipPacks An {@link ArrayList ArrayList&lt;ZipAssociations&gt;} representing the Pack Files found in Zips.
     */
    public record MTCPackFolders (ArrayList<File> assets, ArrayList<ZipAssociations> zipAssets, ArrayList<File> cards, ArrayList<ZipAssociations> zipCards, ArrayList<File> editions, ArrayList<ZipAssociations> zipEditions, ArrayList<File> packs, ArrayList<ZipAssociations> zipPacks) {}

    /**
     * This is a Record for passing the Zip Associations of File Entries
     * @param archive The Archive the File Originates From.
     * @param entryName The Entry Name within the Archive.
     */
    public record ZipAssociations (File archive, String entryName) {}

    /**
     * Handles the reading of MTC Packs for their individual files.
     * @param MTCPacks The List of String representing Locations of MTC Packs.
     * @return This will return in the form of {@link MTCPackFolders}.
     */
    private static MTCPackFolders ReadMTCPackContents(List<String> MTCPacks) {
        ArrayList<ZipAssociations> mtcAssetZipEntries = new ArrayList<>();
        ArrayList<ZipAssociations> mtcCardZipEntries = new ArrayList<>();
        ArrayList<ZipAssociations> mtcEditionZipEntries = new ArrayList<>();
        ArrayList<ZipAssociations> mtcPackZipEntries = new ArrayList<>();
        ArrayList<File> mtcAssetFiles = new ArrayList<>();
        ArrayList<File> mtcCardFiles = new ArrayList<>();
        ArrayList<File> mtcEditionFiles = new ArrayList<>();
        ArrayList<File> mtcPackFiles = new ArrayList<>();
        for (String MTCPack : MTCPacks) {
            if (!MTCPack.toLowerCase(Locale.ROOT).endsWith(".zip")) {
                File assetsFolder = new File(MTCPack + "/assets/is_mtc/mtc/assets/");
                File cardsFolder = new File(MTCPack + "/assets/is_mtc/mtc/cards/");
                File editionsFolder = new File(MTCPack + "/assets/is_mtc/mtc/editions/");
                File packsFolder = new File(MTCPack + "/assets/is_mtc/mtc/packs/");

                if (assetsFolder.exists() && assetsFolder.isDirectory()) {
                    try {
                        for (File file : Objects.requireNonNull(assetsFolder.listFiles())) {
                            if (file.getName().endsWith(".png")) {
                                if (file.isFile()) {
                                    mtcAssetFiles.add(file);
                                    MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Asset: " + file.getPath());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list Assets from folder: " + assetsFolder.getAbsolutePath());
                        MineTradingCardsShared.LOGGER.error(e.getMessage());
                    }
                }
                if (cardsFolder.exists() && cardsFolder.isDirectory()) {
                    try {
                        for (File file : Objects.requireNonNull(cardsFolder.listFiles())) {
                            if (file.getName().endsWith(".cdf") || file.getName().endsWith(".json")) {
                                if (file.isFile()) {
                                    mtcCardFiles.add(file);
                                    MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Card: " + file.getPath());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list Cards from folder: " + cardsFolder.getAbsolutePath());
                        MineTradingCardsShared.LOGGER.error(e.getMessage());
                    }
                }
                if (editionsFolder.exists() && editionsFolder.isDirectory()) {
                    try {
                        for (File file : Objects.requireNonNull(editionsFolder.listFiles())) {
                            if (file.getName().endsWith(".json")) {
                                if (file.isFile()) {
                                    mtcEditionFiles.add(file);
                                    MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Edition: " + file.getPath());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list Editions from folder: " + editionsFolder.getAbsolutePath());
                        MineTradingCardsShared.LOGGER.error(e.getMessage());
                    }
                }
                if (packsFolder.exists() && packsFolder.isDirectory()) {
                    try {
                        for (File file : Objects.requireNonNull(packsFolder.listFiles())) {
                            if (file.getName().endsWith(".json")) {
                                if (file.isFile()) {
                                    mtcPackFiles.add(file);
                                    MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Pack: " + file.getPath());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list Packs from folder: " + packsFolder.getAbsolutePath());
                        MineTradingCardsShared.LOGGER.error(e.getMessage());
                    }
                }
            } else {
                File resourceZipFile = new File(MTCPack);
                try (ZipFile file = new ZipFile(resourceZipFile.toString())) {
                    for (Enumeration<? extends ZipEntry> e = file.entries(); e.hasMoreElements(); ) {
                        ZipEntry entry = e.nextElement();
                        if (!entry.isDirectory()) {
                            if (entry.getName().endsWith(".cdf") || entry.getName().endsWith(".json") || entry.getName().endsWith(".png")) {
                                if (entry.getName().startsWith("assets/is_mtc/mtc/assets/")) {
                                    if (entry.getName().endsWith(".png")) {
                                        mtcAssetZipEntries.add(new ZipAssociations(resourceZipFile, entry.getName()));
                                        MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Asset: " + entry.getName());
                                    }
                                }
                                if (entry.getName().startsWith("assets/is_mtc/mtc/cards/")) {
                                    if (entry.getName().endsWith(".cdf") || entry.getName().endsWith(".json")) {
                                        mtcCardZipEntries.add(new ZipAssociations(resourceZipFile, entry.getName()));
                                        MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Card: " + entry.getName());
                                    }
                                }
                                if (entry.getName().startsWith("assets/is_mtc/mtc/editions/")) {
                                    if (entry.getName().endsWith(".json")) {
                                        mtcEditionZipEntries.add(new ZipAssociations(resourceZipFile, entry.getName()));
                                        MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Edition: " + entry.getName());
                                    }
                                }
                                if (entry.getName().startsWith("assets/is_mtc/mtc/packs/")) {
                                    if (entry.getName().endsWith(".json")) {
                                        mtcPackZipEntries.add(new ZipAssociations(resourceZipFile, entry.getName()));
                                        MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Pack: " + entry.getName());
                                    }
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    MineTradingCardsShared.LOGGER.error("[MTC]: Failed to load MTC Packs Zip: " + resourceZipFile.getPath());
                    MineTradingCardsShared.LOGGER.error(e.getMessage());
                }
            }
        }
        return new MTCPackFolders(mtcAssetFiles, mtcAssetZipEntries, mtcCardFiles, mtcCardZipEntries, mtcEditionFiles, mtcEditionZipEntries, mtcPackFiles, mtcPackZipEntries);
    }
}
