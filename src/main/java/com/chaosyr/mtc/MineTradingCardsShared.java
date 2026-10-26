package com.chaosyr.mtc;

import com.chaosyr.mtc.core.card_pack_selection.buttons.CardPackSelectionButton;
import com.chaosyr.mtc.subperipheral.phases.MTCLoadingPhase;
import com.chaosyr.mtc.subperipheral.phases.MTCReloader;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

/**
 * The Shared Mod
 */
@Mod(MineTradingCardsShared.GUID)
public class MineTradingCardsShared {
    /**
     * Our Mods GUID used to identify our Mod.
     */
    public static final String GUID = "mine_trading_cards";
    /**
     * Our Mod's Logger.
     */
    public static final Logger LOGGER = LogUtils.getLogger();
    /**
     * MTC Content Packs Folder
     */
    private static final String DATA_DIR = FMLPaths.CONFIGDIR.get().getParent().toString().replace('\\', '/') + "/mtc_packs/";

    /**
     * Our Mod's Shared Startup Point
     * @param modEventBus The {@link IEventBus} representing our Mod's event bus.
     * @param modContainer The {@link ModContainer} representing our Mod's container.
     */
    public MineTradingCardsShared(IEventBus modEventBus, ModContainer modContainer) {

    }

    /**
     * A small function for fetching the Data Directory.
     * @return The Content Packs Folder.
     */
    public static String getDataDir() {
        return DATA_DIR;
    }
}

/**
 * The Client Mod
 */
@EventBusSubscriber(modid = MineTradingCardsShared.GUID, value = Dist.CLIENT)
class MineTradingCardsClient {
    /**
     * Register Our Menu Buttons
     * @param event The {@link FMLClientSetupEvent}.
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen) {
            CardPackSelectionButton.registerButtons();
        }
    }

    /**
     * Runs when Resource Packs/Data Packs are Reloaded.
     * @param event The {@link AddClientReloadListenersEvent}.
     */
    @SubscribeEvent
    public static void addClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(MineTradingCardsShared.GUID, "client_pack_loading"),
                new MTCReloader(MTCLoadingPhase::runClientAssetLoading)
        );
    }
}

/**
 * The Server Mod
 */
@EventBusSubscriber(modid = MineTradingCardsShared.GUID, value = Dist.DEDICATED_SERVER)
class MineTradingCardsServer {
    /**
     * Runs when Resource Packs/Data Packs are Reloaded.
     * @param event The {@link AddServerReloadListenersEvent}.
     */
    @SubscribeEvent
    public static void addServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(MineTradingCardsShared.GUID, "server_pack_loading"),
                new MTCReloader(MTCLoadingPhase::runServerPackLoading)
        );
    }
}