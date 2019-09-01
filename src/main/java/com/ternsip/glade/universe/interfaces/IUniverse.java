package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.Universe;

public interface IUniverse {

    ThreadWrapper<Universe> UNIVERSE_THREAD = new ThreadWrapper<>(Universe::new, 1000L / 128);

    static void stopUniverseThread() {
        UNIVERSE_THREAD.stop();
    }

    default boolean isUniverseThreadActive() {
        return UNIVERSE_THREAD.isActive();
    }

    default Universe getUniverse() {
        return UNIVERSE_THREAD.getObjective();
    }

}
