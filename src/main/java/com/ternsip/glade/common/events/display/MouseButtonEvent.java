package com.ternsip.glade.common.events.display;

import com.ternsip.glade.common.events.base.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MouseButtonEvent implements Event {

    private final int button;
    private final int action;
    private final int mods;

}