package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.events.base.EventSnapReceiver;
import com.ternsip.glade.common.logic.LazyWrapper;

public interface IEventSnapReceiverGraphics {

    LazyWrapper<EventSnapReceiver> EVENT_SNAP_RECEIVER_GRAPHICS = new LazyWrapper<>(EventSnapReceiver::new);

    default EventSnapReceiver getEventSnapReceiverGraphics() {
        return EVENT_SNAP_RECEIVER_GRAPHICS.getObjective();
    }

}
