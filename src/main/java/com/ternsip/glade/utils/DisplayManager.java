package com.ternsip.glade.utils;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.function.Function;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DisplayManager {

    private static GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    private static GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }
        }
    };

    private static ArrayList<Callback> callbacks = new ArrayList<>();

    private static final int FPS_CAP = 120;

    private static long lastFrameTime;
    private static float delta;
    private static long window;

    private static IntBuffer widthBuffer;
    private static IntBuffer heightBuffer;

    private static int width;
    private static int height;
    private static float ratio;

    public static void createDisplay() {

        /* Set the error callback */
        glfwSetErrorCallback(errorCallback);

        /* Initialize GLFW */
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        /* Center the window on screen */
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        width = (int) (vidMode.width() * 0.8);
        height = (int) (vidMode.height() * 0.8);

        /* Create window */
        window = glfwCreateWindow(width, height, "Glade", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //TODO glfwSetFramebufferSizeCallback(window, this::resize);

        glfwSetWindowPos(window, (int) (vidMode.width() * 0.1), (int) (vidMode.height() * 0.1));

        /* Create OpenGL context */
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        /* Enable vertical synchronization */
        glfwSwapInterval(1);

        glfwSetKeyCallback(window, keyCallback);

        /* Declare buffers for using inside the loop */
        widthBuffer = MemoryUtil.memAllocInt(1);
        heightBuffer = MemoryUtil.memAllocInt(1);

        glViewport(0, 0, width, height);
        lastFrameTime = getCurrentTime();
    }

    public static void loop(Runnable runnable) {
        /* Loop until window gets closed */
        while (!glfwWindowShouldClose(window)) {

            refreshSize();

            /* Set viewport and clear screen */
            //glViewport(0, 0, width, height);
            //glClear(GL_COLOR_BUFFER_BIT);

            /* Set ortographic projection */
            //glMatrixMode(GL_PROJECTION);
            //glLoadIdentity();
            //glOrtho(-ratio, ratio, -1f, 1f, 1f, -1f);
            //glMatrixMode(GL_MODELVIEW);

            /* Rotate matrix */
            //glLoadIdentity();
            //glRotatef((float) glfwGetTime() * 50f, 0f, 0f, 1f);

            runnable.run();

            long currentFrameTime = getCurrentTime();
            delta = (currentFrameTime - lastFrameTime) / 1000f;
            lastFrameTime = currentFrameTime;

            /* Render triangle */
            //glBegin(GL_TRIANGLES);
            //glColor3f(1f, 0f, 0f);
            //glVertex3f(-0.6f, -0.4f, 0f);
            //glColor3f(0f, 1f, 0f);
            //glVertex3f(0.6f, -0.4f, 0f);
            //glColor3f(0f, 0f, 1f);
            //glVertex3f(0f, 0.6f, 0f);
            //glEnd();

            /* Swap buffers and poll Events */
            glfwSwapBuffers(window);
            glfwPollEvents();

        }
    }

    public static float getFrameTimeSeconds() {
        return delta;
    }

    public static void closeDisplay() {
        /* Free buffers */
        MemoryUtil.memFree(widthBuffer);
        MemoryUtil.memFree(heightBuffer);

        /* Release window and its callbacks */
        glfwDestroyWindow(window);
        keyCallback.free();
        for (Callback callback : callbacks) {
            callback.free();
        }

        /* Terminate GLFW and release the error callback */
        glfwTerminate();
        errorCallback.free();
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
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

    private static void refreshSize() {
        /* Get width and height to calcualte the ratio */
        glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
        width = widthBuffer.get();
        height = heightBuffer.get();
        ratio = (float) width / height;

        /* Flip buffers for next loop */
        widthBuffer.flip();
        heightBuffer.flip();
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
