package com.ternsip.glade.universe.parts.chunks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class LightUpdateRequest {

    private final Vector3ic start;
    private final Vector3ic size;

    public Vector3i getEndExcluding() {
        return new Vector3i(start).add(size);
    }

}