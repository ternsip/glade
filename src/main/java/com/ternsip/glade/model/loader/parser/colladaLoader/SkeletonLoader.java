package com.ternsip.glade.model.loader.parser.colladaLoader;

import com.sun.deploy.xml.XMLNode;
import com.ternsip.glade.model.loader.parser.dataStructures.JointData;
import com.ternsip.glade.model.loader.parser.dataStructures.SkeletonData;
import com.ternsip.glade.model.loader.parser.xmlParser.XmlNode;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SkeletonLoader {

    private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
    private XmlNode armatureData;
    private List<String> boneOrder;
    private int jointCount = 0;

    public SkeletonLoader(XmlNode visualSceneNode, List<String> boneOrder) {
        this.armatureData = getChildByPredicate(visualSceneNode.getChild("visual_scene"), "node", e -> e.getAttribute("id").contains("Armature"));
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

    // TODO this should not be here
    public static XmlNode getChildByPredicate(XmlNode node, String childName, Function<XmlNode, Boolean> filter) {
        List<XmlNode> children = node.getChildren(childName);
        if (children == null || children.isEmpty()) {
            return null;
        }
        for (XmlNode child : children) {
            if (filter.apply(child)) {
                return child;
            }
        }
        return null;
    }

}
