package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.display.WindowData;

public interface IWindowData {

    LazyWrapper<WindowData> WINDOW_DATA = new LazyWrapper<>(WindowData::new);

    default WindowData getWindowData() {
        return WINDOW_DATA.getObjective();
    }

}
