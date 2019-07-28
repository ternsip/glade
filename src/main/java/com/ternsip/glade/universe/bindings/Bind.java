package com.ternsip.glade.universe.bindings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.lwjgl.glfw.GLFW.*;

@RequiredArgsConstructor
@Getter
public enum Bind {

    TOGGLE_MENU(new KeyState(GLFW_KEY_ESCAPE, GLFW_PRESS, 0)),
    TEST_BUTTON(new KeyState(GLFW_KEY_O, GLFW_RELEASE, 0)),
    EXIT_GAME(new KeyState(GLFW_KEY_F4, GLFW_PRESS, GLFW_MOD_ALT));

    private final KeyState defaultKeyState;

}
