package com.ternsip.glade;

import com.ternsip.glade.graphics.display.DisplayManager;
import com.ternsip.glade.universe.Universe;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)]
// TODO FINISH FONTS
// TODO SHADOWING ?
// TODO PHYSICAL COLLISIONS
// TODO read about MemoryStack for optimising buffer allocation
// TODO LookAt bug (collinear)
// TODO Animated Textures
// TODO Author rights
@RequiredArgsConstructor
public class Glade {

    @SneakyThrows
    public static void main(String[] args) {
        Thread universeThread = new Thread(Universe::new);
        new DisplayManager();
        universeThread.start();
        universeThread.join();
    }

}
