package com.ternsip.glade.model.loader.animation.loaders;

import com.ternsip.glade.model.loader.animation.animation.AnimationI;
import com.ternsip.glade.model.loader.animation.animation.JointTransform;
import com.ternsip.glade.model.loader.animation.animation.KeyFrame;
import com.ternsip.glade.model.loader.parser.colladaLoader.ColladaLoader;
import com.ternsip.glade.model.loader.parser.dataStructures.AnimationData;
import com.ternsip.glade.model.loader.parser.dataStructures.JointTransformData;
import com.ternsip.glade.model.loader.parser.dataStructures.KeyFrameData;
import com.ternsip.glade.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnimationLoader {

    public static AnimationI loadAnimation(File colladaFile) {
        AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFile);
        KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = createKeyFrame(animationData.keyFrames[i]);
        }
        return new AnimationI(animationData.lengthSeconds, frames);
    }

    private static KeyFrame createKeyFrame(KeyFrameData data) {
        Map<String, JointTransform> map = new HashMap<String, JointTransform>();
        for (JointTransformData jointData : data.jointTransforms) {
            JointTransform jointTransform = createTransform(jointData.jointLocalTransform);
            map.put(jointData.jointNameId, jointTransform);
        }
        return new KeyFrame(data.time, map);
    }

    public static JointTransform createTransform(Matrix4f mat) {
        // TODO try to wrap this
        Vector3f translation = new Vector3f(mat.m30(), mat.m31(), mat.m32());
        Vector3f scaling = new Vector3f(mat.m00(), mat.m11(), mat.m33());
        Quaternionfc rotation = Maths.fromMatrix(mat);
        return new JointTransform(translation, scaling, rotation);
    }

}
