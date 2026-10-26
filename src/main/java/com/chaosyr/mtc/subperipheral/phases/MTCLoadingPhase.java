package com.chaosyr.mtc.subperipheral.phases;

import com.chaosyr.mtc.peripheral.file_loader.MTCPackReader;

/**
 * This is our Loading Phase System.
 */
public class MTCLoadingPhase {
    /**
     * Handles the Client Side Loading.
     */
    public static void runClientAssetLoading() {
        MTCPackReader.MTCPackFolders Folders = MTCPackReader.ReadMTCPacksFolder(false);
    }

    /**
     * Handles the Server Side Loading.
     */
    public static void runServerPackLoading() {
        MTCPackReader.MTCPackFolders Folders = MTCPackReader.ReadMTCPacksFolder(true);
    }
}
