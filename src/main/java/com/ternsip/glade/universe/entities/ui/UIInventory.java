package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import org.joml.Vector3f;

import java.io.File;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;

@Getter
public class UIInventory extends EntityUI {

    private static final int SELECTION_CELLS = 10;
    private static final float SELECTION_BAR_CELL_SIZE = 0.5f / SELECTION_CELLS;
    private static final float SELECTION_BAR_START_X = (SELECTION_CELLS - 1) * SELECTION_BAR_CELL_SIZE;

    private final Callback<KeyEvent> keyCallback = this::handleKeyEvent;

    private final EntitySprite[] selectionBarCells = new EntitySprite[SELECTION_CELLS];
    private final EntitySprite itemSelection;

    private int cellSelected = 0;

    public UIInventory() {
        super(true);

        for (int i = 0; i < SELECTION_CELLS; ++i) {
            this.selectionBarCells[i] = new EntitySprite(new File("interface/item_cell.png"), true, true);
            this.selectionBarCells[i].setScale(new Vector3f(SELECTION_BAR_CELL_SIZE, SELECTION_BAR_CELL_SIZE, 1));
            this.selectionBarCells[i].setPosition(new Vector3f(-SELECTION_BAR_START_X + SELECTION_BAR_CELL_SIZE * i * 2, -1 + 2 * SELECTION_BAR_CELL_SIZE, 0));
        }

        this.itemSelection = new EntitySprite(new File("interface/item_selection.png"), true, true);
        this.itemSelection.setScale(new Vector3f(SELECTION_BAR_CELL_SIZE, SELECTION_BAR_CELL_SIZE, 1));
        this.itemSelection.setPosition(new Vector3f(-SELECTION_BAR_START_X, -1 + 2 * SELECTION_BAR_CELL_SIZE, 0));

    }

    @Override
    public void register() {
        super.register();
        for (EntitySprite sbc : getSelectionBarCells()) {
            sbc.register();
        }
        getItemSelection().register();
        getUniverseClient().getEventIOReceiver().registerCallback(KeyEvent.class, getKeyCallback());
    }

    @Override
    public void unregister() {
        super.unregister();
        for (EntitySprite sbc : getSelectionBarCells()) {
            sbc.unregister();
        }
        getItemSelection().unregister();
        getUniverseClient().getEventIOReceiver().unregisterCallback(KeyEvent.class, getKeyCallback());
    }

    public void setCellSelected(int cellSelected) {
        this.cellSelected = cellSelected;
        getItemSelection().setPosition(new Vector3f(-SELECTION_BAR_START_X + SELECTION_BAR_CELL_SIZE * cellSelected * 2, -1 + 2 * SELECTION_BAR_CELL_SIZE, 0));
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() >= GLFW_KEY_0 && event.getKey() <= GLFW_KEY_9) {
            setCellSelected((event.getKey() - GLFW_KEY_0 + SELECTION_CELLS - 1) % SELECTION_CELLS);
        }
    }

}
