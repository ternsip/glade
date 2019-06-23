package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class SideData {

    private final byte light;
    private final Block block;

}