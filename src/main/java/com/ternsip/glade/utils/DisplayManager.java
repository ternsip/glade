package com.ternsip.glade.utils;

import lombok.Getter;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Getter
public class DisplayManager {

    private final int FPS_CAP = 120;
    private ArrayList<Callback> callbacks = new ArrayList<>();
    private long lastFrameTime;
    private float deltaTime;
    private float fps;
    private long window;

    private Vector2i windowSize;
    private float ratio;

    public void createDisplay() {
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

        // TODO Should it really be here?
        //glEnable(GL_TEXTURE_2D);
        //glActiveTexture(GL_TEXTURE0);

        registerCloser();

        lastFrameTime = getCurrentTime();
    }

    public void loop(Runnable runnable) {
        /* Loop until window gets closed */
        while (!glfwWindowShouldClose(window)) {
            runnable.run();

            // Calc fps
            long currentFrameTime = getCurrentTime();
            deltaTime = (currentFrameTime - lastFrameTime) / 1000f;
            fps = 1 / deltaTime;
            lastFrameTime = currentFrameTime;

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public void closeDisplay() {

        // Release window
        glfwDestroyWindow(window);

        // Release all callbacks
        for (Callback callback : callbacks) {
            callback.free();
        }

        // Terminate GLFW
        glfwTerminate();
    }

    public int getWidth() {
        return windowSize.x();
    }

    public int getHeight() {
        return windowSize.y();
    }

    public boolean isKeyDown(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    public boolean isMouseDown(int key) {
        return glfwGetMouseButton(window, key) == GLFW_PRESS;
    }

    public void registerScrollCallback(GLFWScrollCallbackI callback) {
        GLFWScrollCallback scrollCallback = GLFWScrollCallback.create(callback);
        callbacks.add(scrollCallback);
        glfwSetScrollCallback(window, scrollCallback);
    }

    public void registerCursorPosCallback(GLFWCursorPosCallbackI callback) {
        GLFWCursorPosCallback posCallback = GLFWCursorPosCallback.create(callback);
        callbacks.add(posCallback);
        glfwSetCursorPosCallback(window, posCallback);
    }

    public void registerKeyCallback(GLFWKeyCallbackI callback) {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(callback);
        callbacks.add(keyCallback);
        glfwSetKeyCallback(window, keyCallback);
    }

    public void registerErrorCallback(GLFWErrorCallbackI callback) {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.create(callback);
        callbacks.add(errorCallback);
        glfwSetErrorCallback(errorCallback);
    }

    public void registerFrameBufferSizeCallback(GLFWFramebufferSizeCallbackI callback) {
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(callback);
        callbacks.add(framebufferSizeCallback);
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);
    }

    public void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public Vector2i getMainDisplaySize() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    // TODO MOVE TO HOTKEYS CLASS
    private void registerCloser() {
        registerKeyCallback(((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                close();
            }
        }));
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
