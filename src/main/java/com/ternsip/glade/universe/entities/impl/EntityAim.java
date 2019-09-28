package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.camera.CameraController;
import com.ternsip.glade.graphics.display.Graphics;
import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.io.File;

@Getter
@Setter
public class EntityAim extends EntitySprite {

    public EntityAim() {
        super(new File("tools/aim.png"), true, true);
        setScale(new Vector3f(0.01f));
    }

    @Override
    public void update(EffigySprite effigy) {
        super.update(effigy);
        Graphics graphics = effigy.getGraphics();
        CameraController cameraController = effigy.getGraphics().getCameraController();
        if (cameraController.isThirdPerson()) {
            graphics.getWindowData().enableCursor();
            setVisible(false);
        } else {
            graphics.getWindowData().disableCursor();
            setVisible(true);
        }
    }
}
