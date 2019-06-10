package com.ternsip.glade.universe.common;

import com.ternsip.glade.universe.Universe;

public interface Universal {

    default Universe getUniverse() {
        return Universe.INSTANCE;
    }

}
