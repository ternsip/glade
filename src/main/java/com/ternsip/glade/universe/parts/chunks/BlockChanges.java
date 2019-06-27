package com.ternsip.glade.universe.parts.chunks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class BlockChanges {

    private final List<SidePosition> sidesToRemove;
    private final List<Side> sidesToAdd;

    public BlockChanges() {
        this.sidesToRemove = new ArrayList<>();
        this.sidesToAdd = new ArrayList<>();
    }

    public boolean isEmpty() {
        return getSidesToAdd().isEmpty() && getSidesToRemove().isEmpty();
    }
}