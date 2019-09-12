package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.events.base.EventSnapReceiver;
import com.ternsip.glade.common.logic.LazyWrapper;

/**
 * TODO Be aware that this snap receiver is for graphical thread
 */
public interface IEventSnapReceiver {

    LazyWrapper<EventSnapReceiver> EVENT_SNAP_RECEIVER = new LazyWrapper<>(EventSnapReceiver::new);

    default EventSnapReceiver getEventSnapReceiver() {
        return EVENT_SNAP_RECEIVER.getObjective();
    }

}
