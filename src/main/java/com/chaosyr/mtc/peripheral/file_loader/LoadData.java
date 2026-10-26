package com.chaosyr.mtc.peripheral.file_loader;

import com.chaosyr.mtc.MineTradingCardsShared;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is a class that handles Loading our MTC Packs.
 */
public class LoadData {
    private static final File contentFolder = new File(MineTradingCardsShared.getDataDir());
    private static final List<String> contentZips = new ArrayList<>();
    private static final List<String> contentFolders = new ArrayList<>();
    private static final List<File> assetsFolders = new ArrayList<>();
    private static final List<File> cardsFolders = new ArrayList<>();
    private static final List<File> editionsFolders = new ArrayList<>();
    private static final List<File> packsFolders = new ArrayList<>();

    public static void LoadDataFiles() {
        MineTradingCardsShared.LOGGER.info("[MTC]: We are now assembling your valid Content Packs into our systems!");
        contentZips.clear();
        contentFolders.clear();
        assetsFolders.clear();
        cardsFolders.clear();
        editionsFolders.clear();
        packsFolders.clear();

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
            return;
        }

        MineTradingCardsShared.LOGGER.info("[MTC]: We are now reading into and loading your Content Packs from Subfolders (and Zips if found)");

        MTCPackFolders mtcPackFolders = LoadMTCPacks(Stream.concat(contentFolders.stream(), contentZips.stream()).toList());

        if (mtcPackFolders.assets.isEmpty() && mtcPackFolders.zipAssets.isEmpty()) {
            MineTradingCardsShared.LOGGER.error("[MTC]: No Assets folders found.");
            MineTradingCardsShared.LOGGER.error("[MTC]: Expected path for Assets is: <Your MTC Pack>/assets/is_mtc/mtc/assets/");
            MineTradingCardsShared.LOGGER.error("[MTC]: Your MTC Packs Folder is here: " + contentFolder.getAbsolutePath());
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

        if ((mtcPackFolders.editions.isEmpty() && mtcPackFolders.zipEditions.isEmpty()) && (mtcPackFolders.cards.isEmpty() && mtcPackFolders.zipCards.isEmpty())) {
            MineTradingCardsShared.LOGGER.error("[MTC]: Found Nothing to Add, Skipping Loading Phase.");
        }
    }

    private record MTCPackFolders (ArrayList<File> assets, ArrayList<ZipAssociations> zipAssets, ArrayList<File> cards, ArrayList<ZipAssociations> zipCards, ArrayList<File> editions, ArrayList<ZipAssociations> zipEditions, ArrayList<File> packs, List<ZipAssociations> zipPacks) {}
    private record ZipAssociations (File archive, String entryName) {}

    private static MTCPackFolders LoadMTCPacks (List<String> MTCPacks) {
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
                    assetsFolders.add(assetsFolder);
                    try {
                        for (File file : Objects.requireNonNull(assetsFolder.listFiles())) {
                            if (file.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
                                if (file.isFile()) {
                                    mtcAssetFiles.add(file);
                                    MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Asset: " + file.getPath());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list Assets from folder: " + assetsFolder.getAbsolutePath());
                    }
                }
                if (cardsFolder.exists() && cardsFolder.isDirectory()) {
                    cardsFolders.add(cardsFolder);
                    try {
                        for (File file : Objects.requireNonNull(cardsFolder.listFiles())) {
                            if (file.getName().toLowerCase(Locale.ROOT).endsWith(".cdf") || file.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
                                if (file.isFile()) {
                                    mtcCardFiles.add(file);
                                    MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Card: " + file.getPath());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list Cards from folder: " + cardsFolder.getAbsolutePath());
                    }
                }
                if (editionsFolder.exists() && editionsFolder.isDirectory()) {
                    editionsFolders.add(editionsFolder);
                    try {
                        for (File file : Objects.requireNonNull(editionsFolder.listFiles())) {
                            if (file.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
                                if (file.isFile()) {
                                    mtcEditionFiles.add(file);
                                    MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Edition: " + file.getPath());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list Editions from folder: " + editionsFolder.getAbsolutePath());
                    }
                }
                if (packsFolder.exists() && packsFolder.isDirectory()) {
                    packsFolders.add(packsFolder);
                    try {
                        for (File file : Objects.requireNonNull(packsFolder.listFiles())) {
                            if (file.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
                                if (file.isFile()) {
                                    mtcPackFiles.add(file);
                                    MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Pack: " + file.getPath());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        MineTradingCardsShared.LOGGER.error("[MTC]: Failed to list Packs from folder: " + packsFolder.getAbsolutePath());
                    }
                }
            } else {
                File resourceZipFile = new File(MTCPack);
                try (ZipFile file = new ZipFile(resourceZipFile.toString())) {
                    for (Enumeration<? extends ZipEntry> e = file.entries(); e.hasMoreElements(); ) {
                        ZipEntry entry = e.nextElement();
                        if (!entry.isDirectory()) {
                            if (entry.getName().toLowerCase(Locale.ROOT).endsWith(".cdf") || entry.getName().toLowerCase(Locale.ROOT).endsWith(".json") || entry.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
                                if (entry.getName().startsWith("assets/is_mtc/mtc/assets/")) {
                                    if (entry.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
                                        mtcAssetZipEntries.add(new ZipAssociations(resourceZipFile, entry.getName()));
                                        MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Asset: " + entry.getName());
                                    }
                                }
                                if (entry.getName().startsWith("assets/is_mtc/mtc/cards/")) {
                                    if (entry.getName().toLowerCase(Locale.ROOT).endsWith(".cdf") || entry.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
                                        mtcCardZipEntries.add(new ZipAssociations(resourceZipFile, entry.getName()));
                                        MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Card: " + entry.getName());
                                    }
                                }
                                if (entry.getName().startsWith("assets/is_mtc/mtc/editions/")) {
                                    if (entry.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
                                        mtcEditionZipEntries.add(new ZipAssociations(resourceZipFile, entry.getName()));
                                        MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Edition: " + entry.getName());
                                    }
                                }
                                if (entry.getName().startsWith("assets/is_mtc/mtc/packs/")) {
                                    if (entry.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
                                        mtcPackZipEntries.add(new ZipAssociations(resourceZipFile, entry.getName()));
                                        MineTradingCardsShared.LOGGER.debug("[MTC]: Retrieved MTC Pack: " + entry.getName());
                                    }
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    MineTradingCardsShared.LOGGER.error("[MTC]: Failed to load MTC Packs Zip: " + resourceZipFile.getPath());
                }
            }
        }
        return new MTCPackFolders(mtcAssetFiles, mtcAssetZipEntries, mtcCardFiles, mtcCardZipEntries, mtcEditionFiles, mtcEditionZipEntries, mtcPackFiles, mtcPackZipEntries);
    }
}
