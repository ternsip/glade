package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.display.Graphics;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyDynamicText;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.protocol.EntityStatisticsServerPacket;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.io.File;

@Getter
@Setter
public class EntityStatistics2D extends EntityDynamicText2D {

    private final Timer updateTimer = new Timer(1000);
    private final Timer blockTraceTimer = new Timer(50); // TODO get this value as a tickrate from options/balance
    private Vector3ic lookingAtBlockPosition = new Vector3i(0);
    private Block lookingAtBlock = Block.AIR;
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

        if (getBlockTraceTimer().isOver()) {
            Vector3fc eye = effigy.getGraphics().getCameraController().getTarget();
            // TODO take eye length from options
            Vector3fc direction = effigy.getGraphics().getCameraController().getLookDirection().mul(10, new Vector3f());
            getUniverseClient().getClient().send(new EntityStatisticsServerPacket(new LineSegmentf(eye, eye.add(direction, new Vector3f()))));
            getBlockTraceTimer().drop();
        }

        StringBuilder sb = new StringBuilder();
        Graphics graphics = effigy.getGraphics();
        sb.append("FPS : ").append(graphics.getWindowData().getFpsCounter().getFps()).append(System.lineSeparator());
        sb.append("Entities : ").append(graphics.getEffigyRepository().getLastSeenNumberOfEntitiesInFrustum()).append(System.lineSeparator());
        sb.append("TickRate : ").append(getUpdatesPerSecond()).append(System.lineSeparator());
        sb.append("Block: ").append(getLookingAtBlock().getName().toLowerCase()).append(" ");
        sb.append(String.format("pos: x=%s, y=%s, z=%s", getLookingAtBlockPosition().x(), getLookingAtBlockPosition().y(), getLookingAtBlockPosition().z())).append(System.lineSeparator());
        setText(sb.toString());
        effigy.alignOnScreen(new Vector2i(0, 0), new Vector2i(75, 75));
    }

    @Override
    public void update() {
        super.update();
        setUpdates(getUpdates() + 1);
        if (getUpdateTimer().isOver()) {
            getUpdateTimer().drop();
            setUpdatesPerSecond(getUpdates());
            setUpdates(0);
        }
    }
}
