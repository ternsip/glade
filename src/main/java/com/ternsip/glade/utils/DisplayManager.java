package com.ternsip.glade.utils;

import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DisplayManager {

    private static ArrayList<Callback> callbacks = new ArrayList<>();

    private static final int FPS_CAP = 120;

    private static long lastFrameTime;
    private static float fps;
    private static long window;

    private static Vector2i windowSize;
    private static float ratio;

    public static void createDisplay() {
        registerErrorCallback(GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        Vector2i mainDisplaySize = getMainDisplaySize();
        windowSize = new Vector2i((int) (mainDisplaySize.x() * 0.8), (int) (mainDisplaySize.y() * 0.8));
        window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Glade", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        registerFrameBufferSizeCallback(((window, width, height) -> {
            windowSize = new Vector2i(width, height);
            ratio = (float) windowSize.x() / windowSize.y();
        }));
        glfwSetWindowPos(window, (int) (mainDisplaySize.x() * 0.1), (int) (mainDisplaySize.y() * 0.1));

        // Create OpenGL context
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Enable vertical synchronization
        glfwSwapInterval(1);

        registerCloser();

        lastFrameTime = getCurrentTime();
    }

    public static void loop(Runnable runnable) {
        /* Loop until window gets closed */
        while (!glfwWindowShouldClose(window)) {
            runnable.run();

            // Calc fps
            long currentFrameTime = getCurrentTime();
            fps = (currentFrameTime - lastFrameTime) / 1000f;
            lastFrameTime = currentFrameTime;

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static float getFrameTimeSeconds() {
        return fps;
    }

    public static void closeDisplay() {

        // Release window
        glfwDestroyWindow(window);

        // Release all callbacks
        for (Callback callback : callbacks) {
            callback.free();
        }

        // Terminate GLFW
        glfwTerminate();
    }

    public static int getWidth() {
        return windowSize.x();
    }

    public static int getHeight() {
        return windowSize.y();
    }

    public static boolean isKeyDown(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    public static boolean isMouseDown(int key) {
        return glfwGetMouseButton(window, key) == GLFW_PRESS;
    }

    public static void registerScrollCallback(GLFWScrollCallbackI callback) {
        GLFWScrollCallback scrollCallback = GLFWScrollCallback.create(callback);
        callbacks.add(scrollCallback);
        glfwSetScrollCallback(window, scrollCallback);
    }

    public static void registerCursorPosCallback(GLFWCursorPosCallbackI callback) {
        GLFWCursorPosCallback posCallback = GLFWCursorPosCallback.create(callback);
        callbacks.add(posCallback);
        glfwSetCursorPosCallback(window, posCallback);
    }

    public static void registerKeyCallback(GLFWKeyCallbackI callback) {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(callback);
        callbacks.add(keyCallback);
        glfwSetKeyCallback(window, keyCallback);
    }

    public static void registerErrorCallback(GLFWErrorCallbackI callback) {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.create(callback);
        callbacks.add(errorCallback);
        glfwSetErrorCallback(errorCallback);
    }

    public static void registerFrameBufferSizeCallback(GLFWFramebufferSizeCallbackI callback) {
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(callback);
        callbacks.add(framebufferSizeCallback);
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);
    }

    public static void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public static Vector2i getMainDisplaySize() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    // TODO MOVE TO HOTKEYS CLASS
    private static void registerCloser() {
        registerKeyCallback(((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                close();
            }
        }));
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
