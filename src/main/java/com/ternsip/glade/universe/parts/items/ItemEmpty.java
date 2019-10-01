package com.ternsip.glade.universe.parts.items;

import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import lombok.Getter;

@Getter
public class ItemEmpty extends Item {

    public static final String EMPTY_ITEM_KEY = "NO_ITEM_KEY";

    public ItemEmpty() {
        setCount(0);
    }

    @Override
    public void use(EntityPlayerServer player) {}

    @Override
    public Object getKey() {
        return EMPTY_ITEM_KEY;
    }

}
