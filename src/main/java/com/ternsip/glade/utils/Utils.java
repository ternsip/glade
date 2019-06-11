package com.ternsip.glade.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        ByteBuffer data = BufferUtils.createByteBuffer(_data.length);
        data.put(_data);
        data.flip();
        AIScene scene = Assimp.aiImportFileFromMemory(data, flags, "");
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

    public static int[] listToIntArray(List<Integer> list) {
        return ArrayUtils.toPrimitive(list.toArray(new Integer[0]), 0);
    }

    public static float[] listToFloatArray(List<Float> list) {
        return ArrayUtils.toPrimitive(list.toArray(new Float[0]), 0);
    }

    @SneakyThrows
    public static byte[] loadResourceAsByteArray(File file) {
        return IOUtils.toByteArray(loadResourceAsStream(file));
    }

    @SneakyThrows
    public static ByteBuffer loadResourceToByteBuffer(File file) {
        return arrayToBuffer(loadResourceAsByteArray(file));
    }

    // Handle all such situations, it also can cause memory problems
    public static void assertThat(boolean condition) {
        if (!condition) {
            // TODO use logging
            System.out.println("Assertion failed!");
        }
    }

    public static List<File> getResourceListing(String[] extensions) {
        Reflections reflections = new Reflections("", new ResourcesScanner());
        String pattern = "(.*\\." + String.join(")|(.*\\.", extensions) + ")";
        return reflections
                .getResources(Pattern.compile(pattern))
                .stream()
                .map(File::new)
                .collect(Collectors.toList());
    }

    // TODO USE CYCLE INSTEAD OF RECURSION USING FOR (;;)
    @SneakyThrows
    public static Method findDeclaredMethodInHierarchy(
            Class<?> objectClass,
            String methodName
    ) {
        try {
            return objectClass.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            if (objectClass.getSuperclass() != null) {
                return findDeclaredMethodInHierarchy(objectClass.getSuperclass(), methodName);
            }
            throw e;
        }
    }

    @SneakyThrows
    public static <T> T createInstanceSilently(Class<? extends T> clazz) {
        return clazz.newInstance();
    }

    public static Set<File> getAllParentDirectories(File file) {
        Set<File> parentDirectories = new HashSet<>();
        File parent = file.getParentFile();
        while (parent != null) {
            parentDirectories.add(parent);
            parent = parent.getParentFile();
        }
        return parentDirectories;
    }

    public static boolean isSubDirectoryPresent(File file, String subDirectory) {
        for (File parentDirectory : getAllParentDirectories(file)) {
            if (parentDirectory.getName().equals(subDirectory)) {
                return true;
            }
        }
        return false;
    }

    public static Map<File, Collection<File>> combineByParentDirectory(Collection<File> files) {
        Map<File, Collection<File>> parentToFiles = new HashMap<>();
        for (File file : files) {
            if (file.getParentFile() != null) {
                parentToFiles.computeIfAbsent(file.getParentFile(), e -> new ArrayList<>());
                parentToFiles.get(file.getParentFile()).add(file);
            }
        }
        return parentToFiles;
    }

}
