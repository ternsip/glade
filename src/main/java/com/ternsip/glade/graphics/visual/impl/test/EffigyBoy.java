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

    public float skyIntensity = 0;

    public Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/boy/boy.dae"))
                .baseRotation(new Vector3f((float) (-Math.PI / 2.0f), 0, 0))
                .build()
        );
    }

    @Override
    public Vector3f getCameraAttachmentPoint() {
        return super.getCameraAttachmentPoint().add(0, getScale().y() * 2, 0, new Vector3f());
    }

}
