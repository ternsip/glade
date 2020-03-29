package com.ternsip.glade.universe.parts.chunks;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class SidesUpdate {

    private List<SidePosition> sidesToRemove = new ArrayList<>();
    private List<Side> sidesToAdd = new ArrayList<>();

    public boolean isEmpty() {
        return getSidesToAdd().isEmpty() && getSidesToRemove().isEmpty();
    }

    public void toAdd(Side side) {
        getSidesToAdd().add(side);
    }

    public void toRemove(SidePosition sidePosition) {
        getSidesToRemove().add(sidePosition);
    }

    public void clear() {
        sidesToRemove.clear();
        sidesToAdd.clear();
    }

}