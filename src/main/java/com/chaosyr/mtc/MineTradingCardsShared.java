package com.chaosyr.mtc;

import com.chaosyr.mtc.core.card_pack_selection.buttons.CardPackSelectionButton;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
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
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CardPackSelectionButton::registerButtons);
    }
}

/**
 * The Server Mod
 */
@EventBusSubscriber(modid = MineTradingCardsShared.GUID, value = Dist.DEDICATED_SERVER)
class MineTradingCardsServer {

}