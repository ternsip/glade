package com.ternsip.glade.universe.parts.chunks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Side {

    private final SidePosition sidePosition;
    private final SideData sideData;

}