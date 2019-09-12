package com.ternsip.glade.network;

import com.ternsip.glade.common.events.base.EventReceiver;
import com.ternsip.glade.common.logic.LazyWrapper;

public interface INetworkServerEventReceiver {

    LazyWrapper<EventReceiver> NETWORK_SERVER_EVENT_RECEIVER = new LazyWrapper<>(EventReceiver::new);

    default EventReceiver getNetworkServerEventReceiver() {
        return NETWORK_SERVER_EVENT_RECEIVER.getObjective();
    }

}
