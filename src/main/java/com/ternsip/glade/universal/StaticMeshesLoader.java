package com.ternsip.glade.universal;

import lombok.SneakyThrows;
import org.joml.Vector4f;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class StaticMeshesLoader {

    @SneakyThrows
    protected static void processMaterial(AIMaterial aiMaterial, List<Material> materials, File texturesDir) {
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

        Material material = new Material(diffuse, specular, 1.0f);
        material.setTexture(texture);
        materials.add(material);
    }

    protected static int[] process3DVectorBufferIndices(AIFace.Buffer aiFaces) {
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

    protected static float[] process3DVectorBufferTextures(AIVector3D.Buffer aiVector) {
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

    protected static float[] process3DVectorBuffer(AIVector3D.Buffer aiVector) {
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
