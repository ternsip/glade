package com.ternsip.glade.model.loader.parser.colladaLoader;

import com.ternsip.glade.model.loader.parser.dataStructures.JointData;
import com.ternsip.glade.model.loader.parser.dataStructures.SkeletonData;
import com.ternsip.glade.model.loader.parser.xmlParser.XmlNode;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class SkeletonLoader {

    private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
    private XmlNode armatureData;
    private List<String> boneOrder;
    private int jointCount = 0;

    public SkeletonLoader(XmlNode visualSceneNode, List<String> boneOrder) {
        this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
        this.boneOrder = boneOrder;
    }

    public SkeletonData extractBoneData() {
        XmlNode headNode = armatureData.getChild("node");
        JointData headJoint = loadJointData(headNode, true);
        return new SkeletonData(jointCount, headJoint);
    }

    private JointData loadJointData(XmlNode jointNode, boolean isRoot) {
        JointData joint = extractMainJointData(jointNode, isRoot);
        for (XmlNode childNode : jointNode.getChildren("node")) {
            joint.addChild(loadJointData(childNode, false));
        }
        return joint;
    }

    private JointData extractMainJointData(XmlNode jointNode, boolean isRoot) {
        String nameId = jointNode.getAttribute("id");
        int index = boneOrder.indexOf(nameId);
        String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
        Matrix4f matrix = convertData(matrixData);
        matrix.transpose();
        if (isRoot) {
            // TODO HEAL THIS
            //because in Blender z is up, but in our game y is up.
            CORRECTION.mul(matrix, matrix);
        }
        jointCount++;
        return new JointData(index, nameId, matrix);
    }

    private Matrix4f convertData(String[] rawData) {
        return new Matrix4f(
                Float.parseFloat(rawData[0]),
                Float.parseFloat(rawData[1]),
                Float.parseFloat(rawData[2]),
                Float.parseFloat(rawData[3]),
                Float.parseFloat(rawData[4]),
                Float.parseFloat(rawData[5]),
                Float.parseFloat(rawData[6]),
                Float.parseFloat(rawData[7]),
                Float.parseFloat(rawData[8]),
                Float.parseFloat(rawData[9]),
                Float.parseFloat(rawData[10]),
                Float.parseFloat(rawData[11]),
                Float.parseFloat(rawData[12]),
                Float.parseFloat(rawData[13]),
                Float.parseFloat(rawData[14]),
                Float.parseFloat(rawData[15])
        );
    }

}
