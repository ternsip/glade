package com.ternsip.glade.graphics.display;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class DisplaySnapCollector {

    private final LinkedBlockingQueue<ResizeEvent> resizeEvents = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<KeyEvent> keyEvents = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<MouseButtonEvent> mouseButtonEvents = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<CursorPosEvent> cursorPosEvents = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<ScrollEvent> scrollEvents = new LinkedBlockingQueue<>();
    private final AtomicBoolean applicationActive = new AtomicBoolean(true);

    @RequiredArgsConstructor
    @Getter
    public static class ResizeEvent {
        private final int width;
        private final int height;
    }

    @RequiredArgsConstructor
    @Getter
    public static class KeyEvent {
        private final int key;
        private final int scanCode;
        private final int action;
        private final int mods;
    }

    @RequiredArgsConstructor
    @Getter
    public static class MouseButtonEvent {
        private final int button;
        private final int action;
        private final int mods;
    }

    @RequiredArgsConstructor
    @Getter
    public static class CursorPosEvent {
        private final double x;
        private final double y;
        private final double dx;
        private final double dy;
    }

    @RequiredArgsConstructor
    @Getter
    public static class ScrollEvent {
        private final double xOffset;
        private final double yOffset;
    }

}
