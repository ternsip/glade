package com.ternsip.glade.common.events.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DebugEvent implements Event {

    private final String message;
    private final Exception exception;

}