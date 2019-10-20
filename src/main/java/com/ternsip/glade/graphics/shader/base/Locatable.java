package com.ternsip.glade.graphics.shader.base;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public abstract class Locatable {

    public static final int NOT_LOCATED = -1;

    private int location = NOT_LOCATED;

    public abstract void locate(int programID, String name);

}
