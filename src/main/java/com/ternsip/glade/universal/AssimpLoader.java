package com.ternsip.glade.universal;

import com.ternsip.glade.utils.Maths;
import lombok.SneakyThrows;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.lang.Math;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static com.ternsip.glade.universal.Mesh.MAX_BONES;
import static com.ternsip.glade.utils.Utils.assertThat;
import static com.ternsip.glade.utils.Utils.loadResourceAsAssimp;
import static org.lwjgl.assimp.Assimp.*;

public class AssimpLoader {

    public static Model loadModel(File meshFile, File animationFile, File texturesDir) {
        int assimpFlags = aiProcess_GenSmoothNormals |
                aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate |
                aiProcess_FixInfacingNormals |
                aiProcess_LimitBoneWeights;
        return loadModel(meshFile, animationFile, texturesDir, assimpFlags);
    }

    @SneakyThrows
    public static Model loadModel(
            File meshFile,
            File animationFile,
            File texturesDir,
            int assimpFlags
    ) {
        AIScene aiSceneMesh = loadResourceAsAssimp(meshFile, assimpFlags);
        AIScene aiSceneAnimation = animationFile.equals(meshFile) ? aiSceneMesh : loadResourceAsAssimp(animationFile, assimpFlags);
        Material[] materials = processMaterials(aiSceneMesh.mMaterials(), texturesDir);
        Skeleton skeleton = processSkeleton(aiSceneMesh);
        PointerBuffer aiMeshes = aiSceneMesh.mMeshes();
        Mesh[] meshes = processMeshes(aiSceneMesh, materials, skeleton, aiMeshes);
        Map<String, Animation> animations = buildAnimations(aiSceneAnimation);
        Set<String> allPossibleBoneNames = animations.values()
                .stream()
                .map(Animation::findAllDistinctBonesNames)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        Bone rootBone = createBones(aiSceneMesh.mRootNode(), new Matrix4f(), skeleton, allPossibleBoneNames);
        assertThat(MAX_BONES > skeleton.numberOfUniqueBones());
        return new Model(meshes, rootBone, skeleton.numberOfUniqueBones(), animations);
    }

    private static Mesh[] processMeshes(
            AIScene aiSceneMesh,
            Material[] materials,
            Skeleton skeleton,
            PointerBuffer aiMeshes
    ) {
        Mesh[] meshes = new Mesh[aiSceneMesh.mNumMeshes()];
        for (int meshIndex = 0; meshIndex < aiSceneMesh.mNumMeshes(); meshIndex++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(meshIndex));
            int materialIdx = aiMesh.mMaterialIndex();
            Material material = (materialIdx >= 0 && materialIdx < materials.length) ? materials[materialIdx] : new Material();
            int numVertices = aiMesh.mNumVertices();
            float[] vertices = process3DVector(aiMesh.mVertices());
            float[] normals = process3DVector(aiMesh.mNormals());
            float[] colors = processColors(aiMesh.mColors(0));
            float[] textures = processTextures(aiMesh.mTextureCoords(0));
            int[] indices = processIndices(aiMesh.mFaces());

            Mesh mesh = new Mesh(
                    vertices,
                    normals,
                    colors,
                    textures,
                    indices,
                    skeleton.getBonesWeights(meshIndex, numVertices, Mesh.MAX_WEIGHTS),
                    skeleton.getBoneNameToBone(meshIndex, numVertices, Mesh.MAX_WEIGHTS),
                    material
            );
            meshes[meshIndex] = mesh;
        }
        return meshes;
    }

    public static Bone createBones(
            AINode aiNode,
            Matrix4fc parentTransform,
            Skeleton skeleton,
            Set<String> allPossibleBoneNames
    ) {
        if (aiNode == null) {
            return new Bone(-1, "Missing", Collections.emptyList(), new Matrix4f(), new Matrix4f());
        }
        String boneName = aiNode.mName().dataString();
        int boneIndex = skeleton.getSkeletonBoneNameToIndex().getOrDefault(boneName, -1);
        Matrix4f localBindTransform = toMatrix(aiNode.mTransformation());
        boolean marginal = !allPossibleBoneNames.contains(boneName);
        Matrix4f bindTransform = marginal ? new Matrix4f() : parentTransform.mul(localBindTransform, new Matrix4f());
        Matrix4f inverseBindTransform = bindTransform.invert(new Matrix4f());
        List<Bone> children = new ArrayList<>();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < aiNode.mNumChildren(); i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Bone childBone = createBones(aiChildNode, bindTransform, skeleton, allPossibleBoneNames);
            children.add(childBone);
        }
        return new Bone(boneIndex, boneName, children, localBindTransform, inverseBindTransform);
    }

    private static Map<String, Animation> buildAnimations(AIScene aiScene) {
        Map<String, Animation> animations = new HashMap<>();

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
            float deltaTime = maxKeyFrameLength == 1 ? duration : (duration / (maxKeyFrameLength - 1));
            for (int j = 0; j < keyFrames.length; ++j) {
                Map<String, BoneTransform> localMap = new HashMap<>();
                for (Map.Entry<String, List<BoneTransform>> entry : boneNameToTransforms.entrySet()) {
                    localMap.put(entry.getKey(), entry.getValue().get(j % entry.getValue().size()));
                }
                keyFrames[j] = new KeyFrame(deltaTime * j, localMap);
            }

            Animation animation = new Animation(duration, keyFrames);
            animations.put(aiAnimation.mName().dataString(), animation);
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
            Quaternionfc rotation = Maths.fromMatrix(mat);
            boneTransforms.add(new BoneTransform(translation, scale, rotation));

        }
        return boneTransforms;
    }

    private static Skeleton processSkeleton(AIScene aiScene) {
        Skeleton.Bone[][] boneMeshes = new Skeleton.Bone[aiScene.mNumMeshes()][];
        for (int i = 0; i < aiScene.mNumMeshes(); i++) {
            AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(i));
            boneMeshes[i] = processBones(aiMesh.mBones());
        }
        return new Skeleton(boneMeshes);
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
        AIColor4D colour = AIColor4D.create();

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null,
                null, null, null, null, null);
        String textPath = path.dataString();
        Texture texture = null;
        if (textPath != null && textPath.length() > 0) {
            TextureCache textCache = TextureCache.getInstance();
            File textureFile = new File(texturesDir, textPath);
            texture = textCache.getTexture(textureFile);
        }

        Vector4f diffuse = Material.DEFAULT_COLOUR;
        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Vector4f specular = Material.DEFAULT_COLOUR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        return new Material(diffuse, specular, texture, 1.0f);
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
