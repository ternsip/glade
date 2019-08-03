package com.ternsip.glade.universe.parts.chunks;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
public class BlocksUpdate {

    private final List<SidePosition> sidesToRemove = Collections.synchronizedList(new ArrayList<>());
    private final List<Side> sidesToAdd = Collections.synchronizedList(new ArrayList<>());

    public BlocksUpdate(Sides sides, boolean additive) {
        if (additive) {
            sides.getSides().forEach((pos, data) -> {
                add(new Side(pos, data));
            });
        } else {
            sides.getSides().forEach((pos, data) -> {
                remove(pos);
            });
        }
    }

    public boolean isEmpty() {
        return getSidesToAdd().isEmpty() && getSidesToRemove().isEmpty();
    }

    public void add(Side side) {
        getSidesToAdd().add(side);
    }

    public void remove(SidePosition sidePosition) {
        getSidesToRemove().add(sidePosition);
    }

}