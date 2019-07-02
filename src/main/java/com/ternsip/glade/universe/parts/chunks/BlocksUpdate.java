package com.ternsip.glade.universe.parts.chunks;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class BlocksUpdate {

    private final List<SidePosition> sidesToRemove = new ArrayList<>();
    private final List<Side> sidesToAdd = new ArrayList<>();

    public boolean isEmpty() {
        return getSidesToAdd().isEmpty() && getSidesToRemove().isEmpty();
    }

    public BlocksUpdate(Sides sides) {
        sides.getSides().forEach((pos, data) -> {
            add(new Side(pos, data));
        });
    }

    public void add(Side side) {
        getSidesToAdd().add(side);
    }

    public void remove(SidePosition sidePosition) {
        getSidesToRemove().add(sidePosition);
    }

}