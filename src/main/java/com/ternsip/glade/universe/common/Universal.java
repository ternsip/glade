package com.ternsip.glade.universe.common;

import com.ternsip.glade.universe.Universe;

public interface Universal {

    Universe UNIVERSE = new Universe();

    default Universe getUniverse() {
        return UNIVERSE;
    }

}
