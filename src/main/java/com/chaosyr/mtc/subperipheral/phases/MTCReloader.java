package com.chaosyr.mtc.subperipheral.phases;

import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * This is a Class that acts as our Reload Listener.
 */
public class MTCReloader implements PreparableReloadListener {
    /**
     * This is our Loading Action
     */
    private final Runnable loadingAction;

    /**
     * Applies the Loading Action from its Implementation {@link PreparableReloadListener}
     * @param loadingAction The Initial Loading Action.
     */
    public MTCReloader(Runnable loadingAction) {
        this.loadingAction = loadingAction;
    }

    /**
     * The Reload Functionality.
     * @param currentReload The current state or Reloading.
     * @param taskExecutor The Task Executor for the Task of Reloading itself.
     * @param barrier The {@link net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier}
     * @param reloadExecutor The Task Executor for our Reloading.
     * @return A CompletableFuture with a Supplied Async, that is Composed when the Barrier finishes, and runs when the Async Runner finishes.
     */
    @Override
    public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier barrier, Executor reloadExecutor) {
        return CompletableFuture.supplyAsync(() -> (Void) null, taskExecutor).thenCompose(barrier::wait).thenRunAsync(loadingAction, reloadExecutor);
    }
}
