package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
public class SidePosition {

    public final int x;
    public final int y;
    public final int z;
    public final BlockSide side;

}