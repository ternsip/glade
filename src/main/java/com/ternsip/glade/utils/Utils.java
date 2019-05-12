package com.ternsip.glade.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.assimp.Assimp.aiGetErrorString;

public class Utils {

    @SneakyThrows
    public static InputStream loadResourceAsStream(File file) {
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(file.getPath());
        if (in == null) {
            throw new FileNotFoundException("Can't find file: " + file.getPath());
        }
        return in;
    }

    @SneakyThrows
    public static FileInputStream loadResourceAsFileStream(File file) {
        URL resource = Utils.class.getClassLoader().getResource(file.getPath());
        if (resource == null) {
            throw new IllegalArgumentException("Can't find file: " + file.getPath());
        }
        File rFile = new File(resource.toURI());
        return new FileInputStream(rFile);
    }

    @SneakyThrows
    public static BufferedReader loadResourceAsBufferedReader(File file) {
        return new BufferedReader(new InputStreamReader(loadResourceAsStream(file), StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static AIScene loadResourceAsAssimp(File file, int flags) {
        byte[] _data = IOUtils.toByteArray(Utils.loadResourceAsStream(file));
        ByteBuffer data = MemoryUtil.memCalloc(_data.length);
        data.put(_data);
        data.flip();
        AIScene scene = Assimp.aiImportFileFromMemory(data, flags, "");
        MemoryUtil.memFree(data);
        if (scene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }
        return scene;
    }

    public static byte[] bufferToArray(ByteBuffer buf) {
        buf.rewind();
        byte[] arr = new byte[buf.remaining()];
        buf.get(arr, 0, arr.length);
        return arr;
    }

    public static ByteBuffer arrayToBuffer(byte[] array) {
        ByteBuffer buf = ByteBuffer.allocateDirect(array.length);
        buf.put(array);
        buf.rewind();
        return buf.asReadOnlyBuffer();
    }

    public static int[] bufferToArray(IntBuffer buf) {
        buf.rewind();
        int[] arr = new int[buf.remaining()];
        buf.get(arr, 0, arr.length);
        return arr;
    }

    public static IntBuffer arrayToBuffer(int[] array) {
        IntBuffer buf = BufferUtils.createIntBuffer(array.length);
        buf.put(array);
        buf.flip();
        return buf.asReadOnlyBuffer();
    }

    public static float[] bufferToArray(FloatBuffer buf) {
        buf.rewind();
        float[] arr = new float[buf.remaining()];
        buf.get(arr, 0, arr.length);
        return arr;
    }

    public static FloatBuffer arrayToBuffer(float[] array) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(array.length);
        buf.put(array);
        buf.flip();
        return buf.asReadOnlyBuffer();
    }

    public static short[] bufferToArray(ShortBuffer buf) {
        buf.rewind();
        short[] arr = new short[buf.remaining()];
        buf.get(arr, 0, arr.length);
        return arr;
    }

    public static ShortBuffer arrayToBuffer(short[] array) {
        ShortBuffer buf = BufferUtils.createShortBuffer(array.length);
        buf.put(array);
        buf.flip();
        return buf.asReadOnlyBuffer();
    }

    @SneakyThrows
    public static byte[] loadResourceAsByteArray(File file) {
        return IOUtils.toByteArray(loadResourceAsStream(file));
    }

    @SneakyThrows
    public static ByteBuffer ioResourceToByteBuffer(File file) {
        return arrayToBuffer(loadResourceAsByteArray(file));
    }

}
