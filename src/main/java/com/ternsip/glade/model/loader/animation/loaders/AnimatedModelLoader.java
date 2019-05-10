package com.ternsip.glade.model.loader.animation.loaders;

import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.model.loader.animation.animation.Animation;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.loader.animation.model.Joint;
import com.ternsip.glade.model.loader.engine.textures.Texture;
import com.ternsip.glade.model.loader.parser.colladaLoader.ColladaLoader;
import com.ternsip.glade.model.loader.parser.dataStructures.AnimatedModelData;
import com.ternsip.glade.model.loader.parser.dataStructures.JointData;
import com.ternsip.glade.model.loader.parser.dataStructures.MeshData;
import com.ternsip.glade.model.loader.parser.dataStructures.SkeletonData;

import java.io.File;

import static com.ternsip.glade.model.GLModel.SKIP_ARRAY_FLOAT;

public class AnimatedModelLoader {

    static int MAX_WEIGHTS = 3;

    // TODO animationFile = modelFile ?
    public static AnimatedModel loadEntity(File modelFile, File textureFile, File animationFile) {
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, MAX_WEIGHTS);
        MeshData mesh = entityData.getMeshData();
        GLModel model = new GLModel(mesh.getVertices(), mesh.getNormals(), SKIP_ARRAY_FLOAT, mesh.getTextureCoords(), mesh.getIndices(), mesh.getVertexWeights(), mesh.getJointIds(), textureFile);
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        Animation animation = AnimationLoader.loadAnimation(animationFile);
        AnimatedModel animatedModel = new AnimatedModel(model, headJoint, skeletonData.jointCount, animation);
        return animatedModel;
    }

    private static Joint createJoints(JointData data) {
        Joint joint = new Joint(data.getIndex(), data.getNameId(), data.getBindLocalTransform());
        for (JointData child : data.children) {
            joint.addChild(createJoints(child));
        }
        return joint;
    }

}
