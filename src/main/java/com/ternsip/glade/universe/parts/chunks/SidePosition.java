package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class SidePosition implements Serializable {

    private final int x;
    private final int y;
    private final int z;
    private final BlockSide side;

}
