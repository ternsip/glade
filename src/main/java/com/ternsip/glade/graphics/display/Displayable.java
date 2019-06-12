package com.ternsip.glade.graphics.display;

public interface Displayable {

    default DisplayManager getDisplayManager() {
        DisplayManager.INSTANCE.checkThreadSafety();
        return DisplayManager.INSTANCE;
    }

}
