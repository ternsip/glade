package com.ternsip.glade.graphics.display;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DisplayCallbacks {

    private final List<ResizeCallback> resizeCallbacks = new ArrayList<>();
    private final List<KeyCallback> keyCallbacks = new ArrayList<>();
    private final List<MouseButtonCallback> mouseButtonCallbacks = new ArrayList<>();
    private final List<ErrorCallback> errorCallbacks = new ArrayList<>();
    private final List<CursorPosCallback> cursorPosCallbacks = new ArrayList<>();
    private final List<ScrollCallback> scrollCallbacks = new ArrayList<>();

    @FunctionalInterface
    public interface ResizeCallback {
        void apply(int width, int height);
    }

    @FunctionalInterface
    public interface KeyCallback {
        void apply(int key, int scanCode, int action, int mods);
    }

    @FunctionalInterface
    public interface MouseButtonCallback {
        void apply(int button, int action, int mods);
    }

    @FunctionalInterface
    public interface ErrorCallback {
        void apply(int error, long description);
    }

    @FunctionalInterface
    public interface CursorPosCallback {
        void apply(double x, double y, double dx, double dy);
    }

    @FunctionalInterface
    public interface ScrollCallback {
        void apply(double xOffset, double yOffset);
    }

}
