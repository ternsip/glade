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
import java.util.*;
import java.util.stream.Collectors;

import static com.ternsip.glade.utils.Utils.loadResourceAsAssimp;
import static org.lwjgl.assimp.Assimp.*;

public class AnimMeshesLoader extends StaticMeshesLoader {

    private static List<JointTransform> buildJointTransforms(AINodeAnim aiNodeAnim) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        List<JointTransform> jointTransforms = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            AIVector3D vec =  positionKeys.get(i).mValue();
            Matrix4f mat = new Matrix4f().translate(vec.x(), vec.y(), vec.z());
            AIQuaternion aiQuat = rotationKeys.get(i).mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            mat.rotate(quat);
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
        return loadAnimGameItem(meshFile, animationFile, texturesDir,
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
                        | aiProcess_FixInfacingNormals | aiProcess_LimitBoneWeights);
    }

    @SneakyThrows
    public static AnimGameItem loadAnimGameItem(File meshFile, File animationFile, File texturesDir, int flags) {
        AIScene aiSceneMesh = loadResourceAsAssimp(meshFile, flags);
        AIScene aiSceneAnimation = animationFile.equals(meshFile) ? aiSceneMesh : loadResourceAsAssimp(animationFile, flags);

        int numMaterials = aiSceneMesh.mNumMaterials();
        PointerBuffer aiMaterials = aiSceneMesh.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materials, texturesDir);
        }

        List<Bone> boneList = new ArrayList<>();
        int numMeshes = aiSceneMesh.mNumMeshes();
        PointerBuffer aiMeshes = aiSceneMesh.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, materials, boneList);
            meshes[i] = mesh;
        }
        Map<String, Integer> jointNameToIndex = boneList.stream().collect(
                Collectors.toMap(Bone::getBoneName, Bone::getBoneId, (o, n) -> o)
        );

        AINode aiRootNode = aiSceneMesh.mRootNode();
        Joint headJoint = createJoints(aiRootNode, new Matrix4f(), jointNameToIndex);
        Map<String, Animation> animations = buildAnimations(aiSceneAnimation);

        return new AnimGameItem(meshes, boneList.stream().map(Bone::getBoneName).collect(Collectors.toList()), headJoint, animations);
    }

    public static Joint createJoints(AINode aiNode, Matrix4fc parentTransform, Map<String, Integer> jointNameToIndex) {
        String jointName = aiNode.mName().dataString();
        int jointIndex = jointNameToIndex.getOrDefault(jointName, -1);
        Matrix4f localBindTransform = AnimMeshesLoader.toMatrix(aiNode.mTransformation());
        Matrix4f bindTransform = jointIndex == -1 ? new Matrix4f() : parentTransform.mul(localBindTransform, new Matrix4f());
        Matrix4f inverseBindTransform = bindTransform.invert(new Matrix4f());
        List<Joint> children = new ArrayList<>();
        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Joint childJoint = createJoints(aiChildNode, bindTransform, jointNameToIndex);
            children.add(childJoint);
        }
        return new Joint(jointIndex, jointName, children, localBindTransform, inverseBindTransform);
    }

    private static List<AnimatedFrame> buildAnimationFrames(
            List<Bone> boneList, Node rootNode,
            Matrix4f rootTransformation
    ) {

        int numFrames = rootNode.getAnimationFrames();
        List<AnimatedFrame> frameList = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            AnimatedFrame frame = new AnimatedFrame();
            frameList.add(frame);

            int numBones = boneList.size();
            for (int j = 0; j < numBones; j++) {
                Bone bone = boneList.get(j);
                Node node = rootNode.findByName(bone.getBoneName());
                Matrix4f boneMatrix = Node.getParentTransforms(node, i);
                boneMatrix.mul(bone.getOffsetMatrix());
                boneMatrix = new Matrix4f(rootTransformation).mul(boneMatrix);
                frame.setMatrix(j, boneMatrix);
            }
        }

        return frameList;
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

            KeyFrame[] keyFrames = new KeyFrame[maxKeyFrameLength];
            float duration = (float) aiAnimation.mDuration();
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

    private static void processBones(
            AIMesh aiMesh, List<Bone> boneList, List<Integer> boneIds,
            List<Float> weights
    ) {
        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
            boneList.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                VertexWeight vw = new VertexWeight(bone.getBoneId(), aiWeight.mVertexId(),
                        aiWeight.mWeight());
                List<VertexWeight> vertexWeightList = weightSet.get(vw.getVertexId());
                if (vertexWeightList == null) {
                    vertexWeightList = new ArrayList<>();
                    weightSet.put(vw.getVertexId(), vertexWeightList);
                }
                vertexWeightList.add(vw);
            }
        }

        int numVertices = aiMesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for (int j = 0; j < Mesh.MAX_WEIGHTS; j++) {
                if (j < size) {
                    VertexWeight vw = vertexWeightList.get(j);
                    weights.add(vw.getWeight());
                    boneIds.add(vw.getBoneId());
                } else {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }
    }

    // TODO REMOVE THIS (THINK)
    private static void processBones2(AIMesh aiMesh, List<Bone> boneList, List<Integer> boneIds, List<Float> weights) {
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        int numVertices = aiMesh.mNumVertices();
        boneIds = Arrays.asList(new Integer[numVertices]);
        weights = Arrays.asList(new Float[numVertices]);
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
            boneList.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                int vertexIndex = aiWeight.mVertexId();
                boneIds.set(vertexIndex, bone.getBoneId());
                weights.set(vertexIndex, aiWeight.mWeight());
            }
        }
    }

    private static Mesh processMesh(AIMesh aiMesh, List<Material> materials, List<Bone> boneList) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Integer> joints = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);
        processBones(aiMesh, boneList, joints, weights);

        Material material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new Material();
        }


        return new Mesh(
                Utils.listToArray(vertices),
                Utils.listToArray(normals),
                Utils.listToArray(textures),
                Utils.listIntToArray(indices),
                Utils.listToArray(weights),
                Utils.listIntToArray(joints),
                material
        );
    }

    private static Node processNodesHierarchy(AINode aiNode, Node parentNode) {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parentNode);

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = processNodesHierarchy(aiChildNode, node);
            node.addChild(childNode);
        }

        return node;
    }

    private static Matrix4f toMatrix(AIMatrix4x4 aiMatrix4x4) {
        Matrix4f result = new Matrix4f();
        result.m00(aiMatrix4x4.a1());
        result.m10(aiMatrix4x4.a2());
        result.m20(aiMatrix4x4.a3());
        result.m30(aiMatrix4x4.a4());
        result.m01(aiMatrix4x4.b1());
        result.m11(aiMatrix4x4.b2());
        result.m21(aiMatrix4x4.b3());
        result.m31(aiMatrix4x4.b4());
        result.m02(aiMatrix4x4.c1());
        result.m12(aiMatrix4x4.c2());
        result.m22(aiMatrix4x4.c3());
        result.m32(aiMatrix4x4.c4());
        result.m03(aiMatrix4x4.d1());
        result.m13(aiMatrix4x4.d2());
        result.m23(aiMatrix4x4.d3());
        result.m33(aiMatrix4x4.d4());

        return result;
    }
}
