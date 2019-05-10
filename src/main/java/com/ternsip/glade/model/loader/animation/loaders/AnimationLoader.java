package com.ternsip.glade.model.loader.animation.loaders;

import com.ternsip.glade.model.loader.animation.animation.Animation;
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
import java.util.HashMap;
import java.util.Map;

/**
 * This class loads up an animation collada file, gets the information from it,
 * and then creates and returns an {@link Animation} from the extracted data.
 *
 * @author Karl
 */
public class AnimationLoader {

    /**
     * Loads up a collada animation file, and returns and animation created from
     * the extracted animation data from the file.
     *
     * @param colladaFile - the collada file containing data about the desired
     *                    animation.
     * @return The animation made from the data in the file.
     */
    public static Animation loadAnimation(File colladaFile) {
        AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFile);
        KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = createKeyFrame(animationData.keyFrames[i]);
        }
        return new Animation(animationData.lengthSeconds, frames);
    }

    /**
     * Creates a keyframe from the data extracted from the collada file.
     *
     * @param data - the data about the keyframe that was extracted from the
     *             collada file.
     * @return The keyframe.
     */
    private static KeyFrame createKeyFrame(KeyFrameData data) {
        Map<String, JointTransform> map = new HashMap<String, JointTransform>();
        for (JointTransformData jointData : data.jointTransforms) {
            JointTransform jointTransform = createTransform(jointData);
            map.put(jointData.jointNameId, jointTransform);
        }
        return new KeyFrame(data.time, map);
    }

    /**
     * Creates a joint transform from the data extracted from the collada file.
     *
     * @param data - the data from the collada file.
     * @return The joint transform.
     */
    private static JointTransform createTransform(JointTransformData data) {
        Matrix4f mat = data.jointLocalTransform;
        // TODO try to wrap this
        Vector3f translation = new Vector3f(mat.m30(), mat.m31(), mat.m32());
        Quaternionfc rotation = Maths.fromMatrix(mat);
        return new JointTransform(translation, rotation);
    }

}
