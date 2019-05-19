package com.ternsip.glade.graphics.display;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DisplayEvents {

    private List<ResizeCallback> resizeCallbacks = new ArrayList<>();
    private List<KeyCallback> keyCallbacks = new ArrayList<>();
    private List<ErrorCallback> errorCallbacks = new ArrayList<>();
    private List<CursorPosCallback> cursorPosCallbacks = new ArrayList<>();
    private List<ScrollCallback> scrollCallbacks = new ArrayList<>();

    @FunctionalInterface
    public interface ResizeCallback {
        void apply(int width, int height);
    }

    @FunctionalInterface
    public interface KeyCallback {
        void apply(int key, int scanCode, int action, int mods);
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
