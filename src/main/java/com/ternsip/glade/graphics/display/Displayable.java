package com.ternsip.glade.graphics.display;

public interface Displayable {

    default DisplayManager getDisplayManager() {
        return DisplayManager.INSTANCE;
    }

}
