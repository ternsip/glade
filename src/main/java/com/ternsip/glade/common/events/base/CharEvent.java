package com.ternsip.glade.common.events.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CharEvent implements Event {

    private final int unicodePoint;

}