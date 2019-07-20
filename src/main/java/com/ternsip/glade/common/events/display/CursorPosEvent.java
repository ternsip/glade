package com.ternsip.glade.common.events.display;

import com.ternsip.glade.common.events.base.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CursorPosEvent implements Event {

    private final double x;
    private final double y;
    private final double dx;
    private final double dy;
    private final double normalX;
    private final double normalY;

}