package com.koios.util;

/**
 * Legacy compatibility shim. The Exile prototype has no external player-count
 * endpoint. Construction and run are deliberately inert: no scheduler, network
 * request, embedded credential or background thread is created.
 *
 * A future public service should be a separate explicitly configured, opt-in
 * integration rather than reviving the former forum hook.
 */
public final class UpdatePlayersOnline implements Runnable {
    public UpdatePlayersOnline() {
        // Retain the old constructor signature for dormant legacy call sites.
    }

    @Override
    public void run() {
        // Intentionally local-only until a real Exile service is designed.
    }
}
