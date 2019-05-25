package com.ternsip.glade;

import com.ternsip.glade.graphics.display.DisplayManager;
import com.ternsip.glade.graphics.renderer.base.MasterRenderer;
import com.ternsip.glade.universe.Universe;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)]
// TODO FONTS
// TODO SHADOWING ?
// TODO PHYSICAL COLLISIONS
// TODO read about MemoryStack for optimising buffer allocation
@SpringBootApplication
@Component
@RequiredArgsConstructor
@EnableScheduling
public class Glade implements CommandLineRunner {

    private final DisplayManager displayManager;
    private final Universe universe;
    private final MasterRenderer masterRenderer;

    public static void main(String[] args) {
        //LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(Glade.class, args);
        //log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        // TODO Check performance with runnable and without it
        displayManager.loop(() -> {
            universe.update();
            masterRenderer.render();
        });
        universe.finish();
        masterRenderer.finish();
        displayManager.finish();
    }

}
