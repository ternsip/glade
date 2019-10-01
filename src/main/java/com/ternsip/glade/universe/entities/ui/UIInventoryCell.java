package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.universe.entities.impl.EntityItemSprite;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import com.ternsip.glade.universe.parts.items.Item;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.File;

import static com.ternsip.glade.universe.parts.items.ItemEmpty.EMPTY_ITEM_KEY;

@Getter
@Setter
public class UIInventoryCell extends EntityUI {

    private static final float ITEM_SCALE = 0.65f;

    private final EntitySprite backgroundSprite;
    private EntityItemSprite itemSprite;
    private boolean registered = false;

    public UIInventoryCell(File background) {
        super(true);
        this.backgroundSprite = new EntitySprite(background, true, true);
        this.itemSprite = new EntityItemSprite(EMPTY_ITEM_KEY, true, true);
    }

    @Override
    public void register() {
        super.register();
        getBackgroundSprite().register();
        getItemSprite().register();
        setRegistered(true);
    }

    @Override
    public void unregister() {
        super.unregister();
        getBackgroundSprite().unregister();
        getItemSprite().unregister();
        setRegistered(false);
    }

    @Override
    public void update() {
        super.update();

        Vector3fc scale = getVisualScale();
        Vector3fc position = getVisualPosition();
        Vector3fc rotation = getVisualRotation();

        getBackgroundSprite().setScale(scale);
        getBackgroundSprite().setRotation(rotation);
        getBackgroundSprite().setPosition(position);
        getBackgroundSprite().setVisible(isVisible());

        getItemSprite().setScale(new Vector3f(scale).mul(ITEM_SCALE));
        getItemSprite().setRotation(rotation);
        getItemSprite().setPosition(new Vector3f(position).add(0, 0, -0.01f));
        getItemSprite().setVisible(isVisible());
    }

    public void updateItem(Item item) {
        if (!getItemSprite().getKey().equals(item.getKey())) {
            EntityItemSprite newItemSprite = new EntityItemSprite(item.getKey(), true, true);
            if (isRegistered()) {
                getItemSprite().unregister();
                newItemSprite.register();
            }
            setItemSprite(newItemSprite);
        }
    }

}