package com.ternsip.glade.model.loader.animation.loaders;

import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.loader.animation.model.Joint;
import com.ternsip.glade.model.loader.engine.globjects.Vao;
import com.ternsip.glade.model.loader.engine.textures.Texture;
import com.ternsip.glade.model.loader.parser.colladaLoader.ColladaLoader;
import com.ternsip.glade.model.loader.parser.dataStructures.AnimatedModelData;
import com.ternsip.glade.model.loader.parser.dataStructures.JointData;
import com.ternsip.glade.model.loader.parser.dataStructures.MeshData;
import com.ternsip.glade.model.loader.parser.dataStructures.SkeletonData;

import java.io.File;

public class AnimatedModelLoader {

    static int MAX_WEIGHTS = 3;

    public static AnimatedModel loadEntity(File modelFile, File textureFile) {
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, MAX_WEIGHTS);
        Vao model = createVao(entityData.getMeshData());
        Texture texture = loadTexture(textureFile);
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        return new AnimatedModel(model, texture, headJoint, skeletonData.jointCount);
    }

    private static Texture loadTexture(File textureFile) {
        return Texture.newTexture(textureFile).anisotropic().create();
    }

    private static Joint createJoints(JointData data) {
        Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
        for (JointData child : data.children) {
            joint.addChild(createJoints(child));
        }
        return joint;
    }

    private static Vao createVao(MeshData data) {
        Vao vao = Vao.create();
        vao.bind();
        vao.createIndexBuffer(data.getIndices());
        vao.createAttribute(0, data.getVertices(), 3);
        vao.createAttribute(1, data.getTextureCoords(), 2);
        vao.createAttribute(2, data.getNormals(), 3);
        vao.createIntAttribute(3, data.getJointIds(), 3);
        vao.createAttribute(4, data.getVertexWeights(), 3);
        vao.unbind();
        return vao;
    }

}
