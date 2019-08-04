package com.ternsip.glade.universe.protocol.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class BlocksObserverChanged implements Serializable {

    private final Vector3ic prevPos;
    private final Vector3ic nextPos;
    private final int prevViewDistance;
    private final int nextViewDistance;

}
