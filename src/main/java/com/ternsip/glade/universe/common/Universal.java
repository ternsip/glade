package com.ternsip.glade.universe.common;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.Universe;

public interface Universal {

    ThreadWrapper<Universe> UNIVERSE_THREAD = new ThreadWrapper<>(new Universe());

    static boolean isThreadActive() {
        return UNIVERSE_THREAD.isActive();
    }

    static void stopThread() {
        UNIVERSE_THREAD.stop();
    }

    default Universe getUniverse() {
        return UNIVERSE_THREAD.getObjective();
    }

}
