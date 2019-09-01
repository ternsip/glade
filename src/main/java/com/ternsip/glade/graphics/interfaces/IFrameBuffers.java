package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.display.FrameBuffers;

public interface IFrameBuffers {

    LazyWrapper<FrameBuffers> FRAME_BUFFERS = new LazyWrapper<>(FrameBuffers::new);

    default FrameBuffers getFrameBuffers() {
        return FRAME_BUFFERS.getObjective();
    }

}
