package com.ternsip.glade.universe.parts.chunks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class BlocksUpdate {

    private final List<SidePosition> sidesToRemove;
    private final List<Side> sidesToAdd;

}