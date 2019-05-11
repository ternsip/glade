package com.ternsip.glade.model.loader.animation.loaders;

import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.model.loader.animation.animation.AnimationI;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.loader.animation.model.Joint;
import com.ternsip.glade.model.loader.parser.colladaLoader.ColladaLoader;
import com.ternsip.glade.model.loader.parser.dataStructures.AnimatedModelData;
import com.ternsip.glade.model.loader.parser.dataStructures.JointData;
import com.ternsip.glade.model.loader.parser.dataStructures.MeshData;
import com.ternsip.glade.model.loader.parser.dataStructures.SkeletonData;
import com.ternsip.glade.universal.Material;
import org.joml.Matrix4f;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.ternsip.glade.model.GLModel.SKIP_ARRAY_FLOAT;

public class AnimatedModelLoader {

    static int MAX_WEIGHTS = 3;

    // TODO animationFile = modelFile ?
    public static AnimatedModel loadEntity(File modelFile, File textureFile, File animationFile) {
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, MAX_WEIGHTS);
        MeshData mesh = entityData.getMeshData();
        GLModel model = new GLModel(mesh.getVertices(), mesh.getNormals(), mesh.getTextureCoords(), mesh.getIndices(), mesh.getVertexWeights(), mesh.getJointIds(), new Material(textureFile));
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint, new Matrix4f());
        AnimationI animation = AnimationLoader.loadAnimation(animationFile);
        AnimatedModel animatedModel = new AnimatedModel(model, headJoint, skeletonData.jointCount, animation);
        return animatedModel;
    }

    public static Joint createJoints(JointData data, Matrix4f parentBindTransform) {
        Matrix4f bindTransform = parentBindTransform.mul(data.getBindLocalTransform(), new Matrix4f());
        Matrix4f inverseBindTransform = bindTransform.invert(new Matrix4f());
        List<Joint> children = data.children.stream().map(e -> createJoints(e, bindTransform)).collect(Collectors.toList());
        return new Joint(data.getIndex(), data.getNameId(), children, data.getBindLocalTransform(), inverseBindTransform);
    }

}
