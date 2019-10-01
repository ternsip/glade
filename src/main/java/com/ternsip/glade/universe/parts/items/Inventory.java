package com.ternsip.glade.universe.parts.items;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Inventory implements Serializable {

    private final Item[] items;

    public Inventory(int size) {
        this.items = new Item[size];
        for (int i = 0; i < size; ++i) {
            this.items[i] = new ItemEmpty();
        }
    }

}
