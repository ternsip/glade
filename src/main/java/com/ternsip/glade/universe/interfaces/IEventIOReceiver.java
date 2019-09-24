package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.events.base.EventIOReceiver;
import com.ternsip.glade.common.logic.LazyWrapper;

public interface IEventIOReceiver {

    LazyWrapper<EventIOReceiver> EVENT_IO_RECEIVER = new LazyWrapper<>(EventIOReceiver::new);

    default EventIOReceiver getEventIOReceiver() {
        return EVENT_IO_RECEIVER.getObjective();
    }

}
