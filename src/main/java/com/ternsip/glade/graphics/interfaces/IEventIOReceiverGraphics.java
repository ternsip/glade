package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.events.base.EventIOReceiver;
import com.ternsip.glade.common.logic.LazyWrapper;

public interface IEventIOReceiverGraphics {

    LazyWrapper<EventIOReceiver> EVENT_IO_RECEIVER_GRAPHICS = new LazyWrapper<>(EventIOReceiver::new);

    default EventIOReceiver getEventIOReceiverGraphics() {
        return EVENT_IO_RECEIVER_GRAPHICS.getObjective();
    }

}
