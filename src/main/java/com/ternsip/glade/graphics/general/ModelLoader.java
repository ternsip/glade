package com.ternsip.glade.graphics.general;

import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import lombok.SneakyThrows;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.lang.Math;
import java.nio.IntBuffer;
import java.util.*;

import static com.ternsip.glade.common.logic.Utils.loadResourceAsAssimp;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.*;
import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {

    @SneakyThrows
    public static Model loadModel(Settings settings) {
        AIScene aiSceneMesh = loadResourceAsAssimp(settings.getMeshFile(), settings.getAssimpFlags());
        AIScene aiSceneAnimation = settings.isAnimationAndMeshInOneFile() ? aiSceneMesh : loadResourceAsAssimp(settings.getAnimationFile(), settings.getAssimpFlags());
        return new Model(
                processMeshes(aiSceneMesh, settings),
                settings.getBaseOffset(),
                settings.getBaseRotation(),
                settings.getBaseScale(),
                buildAnimations(aiSceneAnimation)
        );
    }

    public static Bone createBones(AINode aiNode, Skeleton skeleton) {
        String boneName = aiNode.mName().dataString();
        int boneIndex = skeleton.getBoneNameToIndex().getOrDefault(boneName, -1);
        Matrix4fc offsetMatrix = boneIndex != -1 ? skeleton.getBoneNameToBone().get(boneName).getOffsetMatrix() : new Matrix4f();
        List<Bone> children = new ArrayList<>();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < aiNode.mNumChildren(); i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Bone childBone = createBones(aiChildNode, skeleton);
            children.add(childBone);
        }
        return new Bone(boneIndex, boneName, children, toMatrix(aiNode.mTransformation()), offsetMatrix);
    }

    private static ArrayList<Mesh> processMeshes(AIScene aiSceneMesh, Settings settings) {
        Material[] materials = processMaterials(aiSceneMesh.mMaterials(), settings.getTexturesDir());
        PointerBuffer aiMeshes = aiSceneMesh.mMeshes();
        ArrayList<Mesh> meshes = new ArrayList<>();
        for (int meshIndex = 0; meshIndex < aiSceneMesh.mNumMeshes(); meshIndex++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(meshIndex));
            Material forceMaterial = settings.isManualMeshMaterialsExists(meshIndex) ? settings.getManualMeshMaterials()[meshIndex] : null;
            meshes.add(processMesh(aiMesh, materials, aiSceneMesh.mRootNode(), forceMaterial));
        }
        return meshes;
    }

    private static Mesh processMesh(AIMesh aiMesh, Material[] materials, AINode rootNode, Material forceMaterial) {
        Material material = forceMaterial == null ? materials[aiMesh.mMaterialIndex()] : forceMaterial;
        int numVertices = aiMesh.mNumVertices();
        float[] vertices = process3DVector(aiMesh.mVertices());
        float[] normals = process3DVector(aiMesh.mNormals());
        float[] colors = processColors(aiMesh.mColors(0));
        float[] textures = processTextures(aiMesh.mTextureCoords(0));
        int[] indices = processIndices(aiMesh.mFaces());
        Skeleton skeleton = new Skeleton(processBones(aiMesh.mBones()));
        AnimationData animationData = new AnimationData(createBones(rootNode, skeleton));
        return new Mesh(new MeshAttributes()
                .add(VERTICES, vertices)
                .add(NORMALS, normals)
                .add(COLORS, colors)
                .add(TEXTURES, textures)
                .add(INDICES, indices)
                .add(WEIGHTS, skeleton.getBonesWeights(numVertices))
                .add(BONE_INDICES, skeleton.getBoneIndices(numVertices)),
                material,
                animationData
        );
    }

    private static Map<String, FrameTrack> buildAnimations(AIScene aiScene) {
        Map<String, FrameTrack> animations = new HashMap<>();

        // Process all animations
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < aiScene.mNumAnimations(); i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));

            // Calculate transformation matrices for each node
            PointerBuffer aiNodeAnimList = aiAnimation.mChannels();
            Map<String, List<BoneTransform>> boneNameToTransforms = new HashMap<>();
            int maxKeyFrameLength = 0;
            for (int j = 0; j < aiAnimation.mNumChannels(); j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiNodeAnimList.get(j));
                String boneName = aiNodeAnim.mNodeName().dataString();
                List<BoneTransform> boneTransforms = buildBoneTransforms(aiNodeAnim);
                maxKeyFrameLength = Math.max(boneTransforms.size(), maxKeyFrameLength);
                boneNameToTransforms.put(boneName, boneTransforms);
            }

            // Turn boneNameToTransforms to KeyFrames
            KeyFrame[] keyFrames = new KeyFrame[maxKeyFrameLength];
            float ticksPerSecond = (float) Math.max(aiAnimation.mTicksPerSecond(), 1.0);
            float duration = (float) aiAnimation.mDuration() / ticksPerSecond;
            for (int j = 0; j < keyFrames.length; ++j) {
                Map<String, BoneTransform> localMap = new HashMap<>();
                for (Map.Entry<String, List<BoneTransform>> entry : boneNameToTransforms.entrySet()) {
                    localMap.put(entry.getKey(), entry.getValue().get(j % entry.getValue().size()));
                }
                keyFrames[j] = new KeyFrame(localMap);
            }

            FrameTrack frameTrack = new FrameTrack(duration, keyFrames);
            animations.put(aiAnimation.mName().dataString(), frameTrack);
        }
        return animations;
    }

    private static List<BoneTransform> buildBoneTransforms(AINodeAnim aiNodeAnim) {
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        List<BoneTransform> boneTransforms = new ArrayList<>();
        for (int i = 0; i < aiNodeAnim.mNumPositionKeys(); i++) {
            AIVector3D vec = positionKeys.get(i).mValue();
            Matrix4f mat = new Matrix4f().translate(vec.x(), vec.y(), vec.z());
            if (i < aiNodeAnim.mNumRotationKeys()) {
                AIQuaternion aiQuat = rotationKeys.get(i).mValue();
                Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
                mat.rotate(quat);
            }
            Vector3f scale = new Vector3f(1, 1, 1);
            if (i < aiNodeAnim.mNumScalingKeys()) {
                AIVector3D aiVScale = scalingKeys.get(i).mValue();
                scale.set(aiVScale.x(), aiVScale.y(), aiVScale.z());
            }
            Vector3f translation = new Vector3f(mat.m30(), mat.m31(), mat.m32());
            Quaternionfc rotation = mat.getNormalizedRotation(new Quaternionf());
            boneTransforms.add(new BoneTransform(translation, scale, rotation));
        }
        return boneTransforms;
    }

    private static Skeleton.Bone[] processBones(PointerBuffer aiBones) {
        if (aiBones == null) {
            return new Skeleton.Bone[0];
        }
        aiBones.rewind();
        Skeleton.Bone[] bones = new Skeleton.Bone[aiBones.remaining()];
        for (int i = 0; aiBones.remaining() > 0; i++) {
            AIBone aiBone = AIBone.create(aiBones.get());
            String boneName = aiBone.mName().dataString();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            aiWeights.rewind();
            Map<Integer, List<Float>> boneWeights = new HashMap<>();
            while (aiWeights.remaining() > 0) {
                AIVertexWeight aiWeight = aiWeights.get();
                boneWeights.computeIfAbsent(aiWeight.mVertexId(), e -> new ArrayList<>()).add(aiWeight.mWeight());
            }
            bones[i] = new Skeleton.Bone(boneName, toMatrix(aiBone.mOffsetMatrix()), boneWeights);
        }
        return bones;
    }

    private static Matrix4f toMatrix(AIMatrix4x4 mat) {
        return new Matrix4f(
                mat.a1(), mat.b1(), mat.c1(), mat.d1(),
                mat.a2(), mat.b2(), mat.c2(), mat.d2(),
                mat.a3(), mat.b3(), mat.c3(), mat.d3(),
                mat.a4(), mat.b4(), mat.c4(), mat.d4()
        );
    }

    @SneakyThrows
    private static Material[] processMaterials(PointerBuffer aiMaterials, File texturesDir) {
        if (aiMaterials == null) {
            return new Material[0];
        }
        aiMaterials.rewind();
        Material[] materials = new Material[aiMaterials.remaining()];
        for (int i = 0; aiMaterials.remaining() > 0; ++i) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get());
            materials[i] = processMaterial(aiMaterial, texturesDir);
        }
        return materials;
    }

    @SneakyThrows
    private static Material processMaterial(AIMaterial aiMaterial, File texturesDir) {
        Texture diffuseMap = processTexture(aiMaterial, aiTextureType_DIFFUSE, AI_MATKEY_COLOR_DIFFUSE, texturesDir);
        Texture specularMap = processTexture(aiMaterial, aiTextureType_SPECULAR, AI_MATKEY_COLOR_SPECULAR, texturesDir);
        Texture ambientMap = processTexture(aiMaterial, aiTextureType_AMBIENT, AI_MATKEY_COLOR_AMBIENT, texturesDir);
        Texture emissiveMap = processTexture(aiMaterial, aiTextureType_EMISSIVE, AI_MATKEY_COLOR_EMISSIVE, texturesDir);
        Texture heightMap = processTexture(aiMaterial, aiTextureType_HEIGHT, "", texturesDir);
        Texture normalsMap = processTexture(aiMaterial, aiTextureType_NORMALS, "", texturesDir);
        Texture shininessMap = processTexture(aiMaterial, aiTextureType_SHININESS, "", texturesDir);
        Texture opacityMap = processTexture(aiMaterial, aiTextureType_OPACITY, AI_MATKEY_COLOR_TRANSPARENT, texturesDir);
        Texture displacementMap = processTexture(aiMaterial, aiTextureType_DISPLACEMENT, "", texturesDir);
        Texture lightMap = processTexture(aiMaterial, aiTextureType_LIGHTMAP, "", texturesDir);
        Texture reflectionMap = processTexture(aiMaterial, aiTextureType_REFLECTION, AI_MATKEY_COLOR_REFLECTIVE, texturesDir);
        return new Material(diffuseMap, specularMap, ambientMap, emissiveMap, heightMap, normalsMap, shininessMap, opacityMap, displacementMap, lightMap, reflectionMap);
    }

    private static Texture processTexture(
            AIMaterial aiMaterial,
            int aiTextureType,
            CharSequence aiMaterialColor,
            File texturesDir
    ) {
        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType, 0, path, (IntBuffer) null, null, null, null, null, null);
        String textPath = path.dataString();
        File file = textPath == null || textPath.isEmpty() ? null : new File(texturesDir, textPath);
        AIColor4D aiColor4D = AIColor4D.create();
        Vector4f color = null;
        if (aiGetMaterialColor(aiMaterial, aiMaterialColor, aiTextureType_NONE, 0, aiColor4D) == 0) {
            color = new Vector4f(aiColor4D.r(), aiColor4D.g(), aiColor4D.b(), aiColor4D.a());
        }
        path.free();
        return new Texture(color, file);
    }

    private static int[] processIndices(AIFace.Buffer aiFaces) {
        if (aiFaces == null) return new int[0];
        aiFaces.rewind();
        ArrayList<Integer> indices = new ArrayList<>();
        while (aiFaces.remaining() > 0) {
            AIFace aiFace = aiFaces.get();
            IntBuffer buffer = aiFace.mIndices();
            buffer.rewind();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(i -> i).toArray();
    }

    private static float[] processTextures(AIVector3D.Buffer aiVector) {
        if (aiVector == null) return new float[0];
        aiVector.rewind();
        float[] texUV = new float[aiVector.remaining() * 2];
        for (int i = 0; aiVector.remaining() > 0; i++) {
            AIVector3D aiV = aiVector.get();
            texUV[i * 2] = aiV.x();
            texUV[i * 2 + 1] = 1 - aiV.y();
        }
        return texUV;
    }

    private static float[] process3DVector(AIVector3D.Buffer aiVector) {
        if (aiVector == null) return new float[0];
        aiVector.rewind();
        float[] array = new float[aiVector.remaining() * 3];
        for (int i = 0; aiVector.remaining() > 0; ++i) {
            AIVector3D aiV = aiVector.get();
            array[i * 3] = aiV.x();
            array[i * 3 + 1] = aiV.y();
            array[i * 3 + 2] = aiV.z();
        }
        return array;
    }

    private static float[] processColors(AIColor4D.Buffer aiVector) {
        if (aiVector == null) return new float[0];
        aiVector.rewind();
        float[] array = new float[aiVector.remaining() * 4];
        for (int i = 0; aiVector.remaining() > 0; ++i) {
            AIColor4D aiV = aiVector.get();
            array[i * 3] = aiV.r();
            array[i * 3 + 1] = aiV.g();
            array[i * 3 + 2] = aiV.b();
            array[i * 3 + 3] = aiV.a();
        }
        return array;
    }


}
