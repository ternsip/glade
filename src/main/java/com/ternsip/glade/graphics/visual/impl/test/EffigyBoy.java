package com.ternsip.glade.graphics.visual.impl.test;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.io.File;

@Getter
@Setter
public class EffigyBoy extends EffigyAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .animationFiles(new File[]{
                        new File("models/boy/idle.dae"),
                        new File("models/boy/Jump.dae"),
                        new File("models/boy/Run.dae"),
                        new File("models/boy/Falling.dae"),
                        new File("models/boy/Floating.dae"),
                        new File("models/boy/Die.dae"),
                })
                .baseRotation(new Vector3f(0, 0, 0))
                .baseScale(new Vector3f(100))
                .build()
        );
    }

    @Override
    public Vector3f getCameraAttachmentPoint() {
        return super.getCameraAttachmentPoint().add(0, getScale().y() * 2, 0, new Vector3f());
    }

}
