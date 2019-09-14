package com.ternsip.glade.common.events.network;

import com.ternsip.glade.common.events.base.Event;
import com.ternsip.glade.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OnConnectedToServer implements Event {

    private final Connection connection;
    private final boolean postProcessed;

}
