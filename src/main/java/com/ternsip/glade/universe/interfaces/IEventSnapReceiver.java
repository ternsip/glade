package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.events.base.EventSnapReceiver;
import com.ternsip.glade.common.logic.LazyWrapper;

public interface IEventSnapReceiver {

    LazyWrapper<EventSnapReceiver> EVENT_SNAP_RECEIVER = new LazyWrapper<>(EventSnapReceiver::new);

    default EventSnapReceiver getEventSnapReceiver() {
        return EVENT_SNAP_RECEIVER.getObjective();
    }

}
