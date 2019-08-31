package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.Packet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
public class ConsoleMessagePacket implements Packet {

    private final String message;

    @Override
    public void apply(Connection connection) {
        log.info("Received message: " + message);
    }

}
