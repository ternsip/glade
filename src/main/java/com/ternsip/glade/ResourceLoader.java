package com.ternsip.glade;


import com.mokiat.data.front.parser.*;
import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.utils.Utils;
import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ternsip.glade.model.GLModel.SKIP_ARRAY;

public class ResourceLoader {

    @SneakyThrows
    public static GLModel loadObjModel(File modelFile, File textureFile) {
        final IOBJParser parser = new OBJParser();
        final OBJModel model = parser.parse(Utils.loadResourceAsStream(modelFile));
        List<OBJVertex> oVertices = model.getVertices();
        float[] vertices = new float[oVertices.size() * 3];
        for (int i = 0; i < oVertices.size(); ++i) {
            vertices[i * 3] = oVertices.get(i).x;
            vertices[i * 3 + 1] = oVertices.get(i).y;
            vertices[i * 3 + 2] = oVertices.get(i).z;
        }
        List<OBJNormal> oNormals = model.getNormals();
        float[] normals = new float[oNormals.size() * 3];
        for (int i = 0; i < oNormals.size(); ++i) {
            normals[i * 3] = oNormals.get(i).x;
            normals[i * 3 + 1] = oNormals.get(i).y;
            normals[i * 3 + 2] = oNormals.get(i).z;
        }
        List<OBJTexCoord> oTextures = model.getTexCoords();
        float[] textures = new float[oTextures.size() * 3];
        for (int i = 0; i < oTextures.size(); ++i) {
            textures[i * 3] = oTextures.get(i).u;
            textures[i * 3 + 1] = oTextures.get(i).v;
        }
        List<OBJObject> oObjects = model.getObjects();
        List<Short> indices = new ArrayList<>();
        for (int i = 0; i < oObjects.size(); ++i) {
            for (OBJMesh objMesh : oObjects.get(i).getMeshes()) {
                for (OBJFace objFace : objMesh.getFaces()) {
                    // TODO THIS IS INCORRECT
                    //https://community.khronos.org/t/vbo-gl-element-array-buffer-with-tex-uv-coords/56627
                    //http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-9-vbo-indexing/
                    List<Short> refs = objFace.getReferences().stream().map(e -> (short) e.vertexIndex).collect(Collectors.toList());
                    List<Short> resRefs = new ArrayList<>(); // triangulated arr
                    for (int j = 1; j < refs.size() - 1; ++j) {
                        resRefs.add(refs.get(0));
                        resRefs.add(refs.get(j));
                        resRefs.add(refs.get(j + 1));
                    }
                    indices.addAll(resRefs);
                }
            }
        }
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); ++i) {
            indicesArray[i] = indices.get(i);
        }
        return new GLModel(vertices, normals, SKIP_ARRAY, textures, indicesArray, textureFile);
    }


}
