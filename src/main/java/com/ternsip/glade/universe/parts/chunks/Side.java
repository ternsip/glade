package com.ternsip.glade.universe.parts.chunks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Side implements Serializable {

    private final SidePosition sidePosition;
    private final SideData sideData;

}