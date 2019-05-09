package com.ternsip.glade;

import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.utils.Utils;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoader {

    @SneakyThrows
    public static GLModel loadObjModel(File modelFile, File textureFile) {
        BufferedReader reader = Utils.loadResourceAsBufferedReader(modelFile);
        String line;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Short> indices = new ArrayList<>();
        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        short[] indicesArray = null;

        while (true) {
            line = reader.readLine();
            String[] currentLine = line.split(" ");
            if (line.startsWith("v ")) {
                Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                vertices.add(vertex);
            } else if (line.startsWith("vt ")) {
                Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                textures.add(texture);
            } else if (line.startsWith("vn ")) {
                Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                normals.add(normal);
            } else if (line.startsWith("f ")) {
                textureArray = new float[vertices.size() * 2];
                normalsArray = new float[vertices.size() * 3];
                break;
            }

        }
        while (line != null) {
            if (!line.startsWith("f ")) {
                line = reader.readLine();
                continue;
            }
            String[] currentLine = line.split(" ");
            String[][] vertexArrays = {currentLine[1].split("/"), currentLine[2].split("/"), currentLine[3].split("/")};

            for (String[] vertexArray : vertexArrays) {
                int currentVertexPointer = Integer.parseInt(vertexArray[0]) - 1;
                indices.add((short) currentVertexPointer);
                Vector2f currentText = textures.get(Integer.parseInt(vertexArray[1]) - 1);
                textureArray[currentVertexPointer * 2] = currentText.x;
                textureArray[currentVertexPointer * 2 + 1] = 1 - currentText.y;
                Vector3f currentNorm = normals.get(Integer.parseInt(vertexArray[2]) - 1);
                normalsArray[currentVertexPointer * 3] = currentNorm.x;
                normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
                normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
            }
            line = reader.readLine();
        }
        reader.close();

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new short[indices.size()];
        int vertexPointer = 0;
        for (Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return new GLModel(verticesArray, normalsArray, null, textureArray, indicesArray, textureFile);
    }



}
