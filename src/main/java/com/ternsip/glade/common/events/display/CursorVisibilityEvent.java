package com.ternsip.glade.common.events.display;

import com.ternsip.glade.common.events.base.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CursorVisibilityEvent implements Event {

    private final boolean visible;

}