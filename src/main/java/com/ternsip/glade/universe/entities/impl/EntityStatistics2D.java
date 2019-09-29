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
    private final Vector3i lookingAtBlockPosition = new Vector3i(0);
    private LineSegmentf eyeSegment = new LineSegmentf(new Vector3f(0), new Vector3f(0));
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
        Vector3fc eye = effigy.getGraphics().getCameraController().getTarget();
        Vector3fc direction = effigy.getGraphics().getCameraController().getLookDirection().mul(getUniverseClient().getBalance().getPlayerExamineLength(), new Vector3f());
        setEyeSegment(new LineSegmentf(new Vector3f(eye), new Vector3f(eye).add(direction)));
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

    @Override
    public void networkUpdate() {
        super.networkUpdate();
        getUniverseClient().getClient().send(new EntityStatisticsServerPacket(getEyeSegment()));
    }
}
