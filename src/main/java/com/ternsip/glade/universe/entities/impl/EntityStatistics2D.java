package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.display.Graphics;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyDynamicText;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.io.File;

@Getter
@Setter
public class EntityStatistics2D extends EntityDynamicText2D {

    private final Timer updateTimer = new Timer(1000);
    private int updates = 0;
    private int updatesPerSecond = 0;

    public EntityStatistics2D(File file, Vector4fc color, boolean useAspect) {
        super(file, "NO DATA CALCULATED", color, useAspect);
        setShiftX(true);
        setShiftY(true);
    }

    @Override
    public void update(EffigyDynamicText effigy) {
        super.update(effigy);
        StringBuilder sb = new StringBuilder();
        Graphics graphics = effigy.getGraphics();
        sb.append("FPS : ").append(graphics.getWindowData().getFpsCounter().getFps()).append(System.lineSeparator());
        sb.append("Entities : ").append(graphics.getEffigyRepository().getLastSeenNumberOfEntitiesInFrustum()).append(System.lineSeparator());
        sb.append("TickRate : ").append(getUpdatesPerSecond()).append(System.lineSeparator());
        Vector3fc eye = graphics.getCameraController().getTarget();
        Vector3fc direction = graphics.getCameraController().getLookDirection().mul(10, new Vector3f());
        LineSegmentf segment = new LineSegmentf(eye, eye.add(direction, new Vector3f()));
        // TODO Critical bug (using server on client), Probably need to make texts client-server
        Vector3ic pos = getUniverseServer().getBlocks().traverse(segment, block -> block != Block.AIR);
        if (pos != null) {
            Block block = getUniverseServer().getBlocks().getBlock(pos);
            sb.append("Block: ").append(block.getName().toLowerCase()).append(" ");
            sb.append(String.format("pos: x=%s, y=%s, z=%s", pos.x(), pos.y(), pos.z())).append(System.lineSeparator());
        }
        setText(sb.toString());
        effigy.alignOnScreen(new Vector2i(0, 0), new Vector2i(75, 75));
    }

    @Override
    public void clientUpdate() {
        super.clientUpdate();
        setUpdates(getUpdates() + 1);
        if (getUpdateTimer().isOver()) {
            getUpdateTimer().drop();
            setUpdatesPerSecond(getUpdates());
            setUpdates(0);
        }
    }
}
