package com.ternsip.glade.universal;

import com.ternsip.glade.model.Mesh;
import com.ternsip.glade.model.loader.animation.animation.Animation;
import com.ternsip.glade.model.loader.animation.animation.JointTransform;
import com.ternsip.glade.model.loader.animation.animation.KeyFrame;
import com.ternsip.glade.model.loader.animation.model.Joint;
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
import java.util.stream.IntStream;

import static com.ternsip.glade.utils.Utils.loadResourceAsAssimp;
import static org.lwjgl.assimp.Assimp.*;

public class AnimMeshesLoader {

    public static final int FLAG_ALLOW_ORIGINS_WITHOUT_BONES = 0x1;

    private static List<JointTransform> buildJointTransforms(AINodeAnim aiNodeAnim) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        List<JointTransform> jointTransforms = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
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
            jointTransforms.add(new JointTransform(translation, scale, rotation));

        }
        return jointTransforms;
    }

    public static AnimGameItem loadAnimGameItem(File meshFile, File animationFile, File texturesDir) {
        return loadAnimGameItem(meshFile, animationFile, texturesDir, 0);
    }

    public static AnimGameItem loadAnimGameItem(File meshFile, File animationFile, File texturesDir, int loaderFlags) {
        int assimpFlags = aiProcess_GenSmoothNormals |
                aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate |
                aiProcess_FixInfacingNormals |
                aiProcess_LimitBoneWeights;
        return loadAnimGameItem(meshFile, animationFile, texturesDir, assimpFlags, loaderFlags);
    }

    @SneakyThrows
    public static AnimGameItem loadAnimGameItem(
            File meshFile,
            File animationFile,
            File texturesDir,
            int assimpFlags,
            int loaderFlags
    ) {
        AIScene aiSceneMesh = loadResourceAsAssimp(meshFile, assimpFlags);
        AIScene aiSceneAnimation = animationFile.equals(meshFile) ? aiSceneMesh : loadResourceAsAssimp(animationFile, assimpFlags);

        Material[] materials = processMaterials(aiSceneMesh.mMaterials(), texturesDir);

        List<Bone> allBones = new ArrayList<>();
        int numMeshes = aiSceneMesh.mNumMeshes();
        PointerBuffer aiMeshes = aiSceneMesh.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            int materialIdx = aiMesh.mMaterialIndex();
            Material material = (materialIdx >= 0 && materialIdx < materials.length) ? materials[materialIdx] : new Material();
            float[] vertices = process3DVectorBuffer(aiMesh.mVertices());
            float[] normals = process3DVectorBuffer(aiMesh.mNormals());
            float[] textures = process3DVectorBufferTextures(aiMesh.mTextureCoords(0));
            int[] indices = process3DVectorBufferIndices(aiMesh.mFaces());
            Skeleton skeleton = processBones(aiMesh.mBones());
            Mesh mesh = new Mesh(
                    vertices,
                    normals,
                    textures,
                    indices,
                    skeleton,
                    material
            );
            meshes[i] = mesh;
            allBones.addAll(Arrays.asList(skeleton.getBones()));
        }
        Map<String, Integer> jointNameToIndex = IntStream
                .range(0, allBones.size())
                .boxed()
                .collect(Collectors.toMap(i -> allBones.get(i).getBoneName(), i -> i, (o, n) -> o));

        Joint headJoint = createJoints(aiSceneMesh.mRootNode(), new Matrix4f(), jointNameToIndex, loaderFlags);
        Map<String, Animation> animations = buildAnimations(aiSceneAnimation);

        return new AnimGameItem(meshes, allBones.stream().map(Bone::getBoneName).collect(Collectors.toList()), headJoint, animations);
    }

    public static Joint createJoints(
            AINode aiNode,
            Matrix4fc parentTransform,
            Map<String, Integer> jointNameToIndex,
            int loaderFlags
    ) {
        if (aiNode == null) {
            return new Joint(-1, "Missing", Collections.emptyList(), new Matrix4f(), new Matrix4f());
        }
        String jointName = aiNode.mName().dataString();
        int numChildren = aiNode.mNumChildren();
        int jointIndex = jointNameToIndex.getOrDefault(jointName, -1);
        Matrix4f localBindTransform = toMatrix(aiNode.mTransformation());
        boolean marginal = ((loaderFlags & FLAG_ALLOW_ORIGINS_WITHOUT_BONES) == 0) && (jointIndex == -1);
        Matrix4f bindTransform = marginal ? new Matrix4f() : parentTransform.mul(localBindTransform, new Matrix4f());
        Matrix4f inverseBindTransform = bindTransform.invert(new Matrix4f());
        List<Joint> children = new ArrayList<>();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Joint childJoint = createJoints(aiChildNode, bindTransform, jointNameToIndex, loaderFlags);
            children.add(childJoint);
        }
        return new Joint(jointIndex, jointName, children, localBindTransform, inverseBindTransform);
    }

    private static Map<String, Animation> buildAnimations(AIScene aiScene) {
        Map<String, Animation> animations = new HashMap<>();

        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));

            // Calculate transformation matrices for each node
            int numJoints = aiAnimation.mNumChannels();
            PointerBuffer aiNodeAnimList = aiAnimation.mChannels();
            Map<String, List<JointTransform>> jointNameToTransforms = new HashMap<>();
            int maxKeyFrameLength = 0;
            for (int j = 0; j < numJoints; j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiNodeAnimList.get(j));
                String jointName = aiNodeAnim.mNodeName().dataString();
                List<JointTransform> jointTransforms = buildJointTransforms(aiNodeAnim);
                maxKeyFrameLength = Math.max(jointTransforms.size(), maxKeyFrameLength);
                jointNameToTransforms.put(jointName, jointTransforms);
            }

            // Turn jointNameToTransforms to KeyFrames
            KeyFrame[] keyFrames = new KeyFrame[maxKeyFrameLength];
            float ticksPerSecond = (float) Math.max(aiAnimation.mTicksPerSecond(), 1.0);
            float duration = (float) aiAnimation.mDuration() / ticksPerSecond;
            float deltaTime = maxKeyFrameLength == 1 ? duration : (duration / (maxKeyFrameLength - 1));
            for (int j = 0; j < keyFrames.length; ++j) {
                Map<String, JointTransform> localMap = new HashMap<>();
                for (Map.Entry<String, List<JointTransform>> entry : jointNameToTransforms.entrySet()) {
                    localMap.put(entry.getKey(), entry.getValue().get(j % entry.getValue().size()));
                }
                keyFrames[j] = new KeyFrame(deltaTime * j, localMap);
            }

            Animation animation = new Animation(duration, keyFrames);
            animations.put(aiAnimation.mName().dataString(), animation);
        }
        return animations;
    }

    private static Skeleton processBones(PointerBuffer aiBones) {
        if (aiBones == null) {
            return new Skeleton(new Bone[0]);
        }
        aiBones.rewind();
        Bone[] bones = new Bone[aiBones.remaining()];
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
            bones[i] = new Bone(boneName, toMatrix(aiBone.mOffsetMatrix()), boneWeights);
        }
        return new Skeleton(bones);
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
    static Material[] processMaterials(PointerBuffer aiMaterials, File texturesDir) {
        if (aiMaterials == null) {
            return new Material[0];
        }
        aiMaterials.rewind();
        Material[] materials = new Material[aiMaterials.remaining()];
        for (int i = 0; aiMaterials.remaining() > 0; ++i) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get());
            materials[i]= processMaterial(aiMaterial, texturesDir);
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
        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
                colour);
        if (result == 0) {
            diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Vector4f specular = Material.DEFAULT_COLOUR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0,
                colour);
        if (result == 0) {
            specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        return new Material(diffuse, specular, texture, 1.0f);
    }

    static int[] process3DVectorBufferIndices(AIFace.Buffer aiFaces) {
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

    static float[] process3DVectorBufferTextures(AIVector3D.Buffer aiVector) {
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

    static float[] process3DVectorBuffer(AIVector3D.Buffer aiVector) {
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

}
