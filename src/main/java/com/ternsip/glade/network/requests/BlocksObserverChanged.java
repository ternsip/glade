package com.ternsip.glade.network.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3fc;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class BlocksObserverChanged implements Serializable {

    private final Vector3fc prevPos;
    private final Vector3fc nextPos;

}
