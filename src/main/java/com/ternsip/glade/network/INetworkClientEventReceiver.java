package com.ternsip.glade.network;

import com.ternsip.glade.common.events.base.EventReceiver;
import com.ternsip.glade.common.logic.LazyWrapper;

public interface INetworkClientEventReceiver {

    LazyWrapper<EventReceiver> NETWORK_CLIENT_EVENT_RECEIVER = new LazyWrapper<>(EventReceiver::new);

    default EventReceiver getNetworkClientEventReceiver() {
        return NETWORK_CLIENT_EVENT_RECEIVER.getObjective();
    }

}
