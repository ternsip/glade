package com.ternsip.glade.universe;

import com.ternsip.glade.universe.common.Camera;
import com.ternsip.glade.universe.common.Sun;
import com.ternsip.glade.universe.entities.impl.EntityFps;
import com.ternsip.glade.universe.entities.repository.EntityRepository;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import com.ternsip.glade.universe.graphicals.impl.*;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

@Getter
public class Universe {

    private Sun sun;
    private GraphicalPlayer graphicalPlayer;
    private Camera camera;
    private EntityRepository entityRepository = new EntityRepository();

    public void initialize() {

        sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));
        graphicalPlayer = new GraphicalPlayer();
        graphicalPlayer.setScale(new Vector3f(5, 5, 5));
        camera = new Camera(graphicalPlayer);

        Graphical graphicalCube = new GraphicalCube();

        Graphical graphicalLamp = new GraphicalLamp();
        graphicalLamp.setPosition(new Vector3f(-60f, 0, -60));
        graphicalLamp.setScale(new Vector3f(40, 40, 40));

        Graphical graphicalBottle = new GraphicalBottle();
        graphicalBottle.setPosition(new Vector3f(-30f, 0, -20));
        graphicalBottle.setScale(new Vector3f(5, 5, 5));

        Graphical graphicalZebra = new GraphicalZebra();
        graphicalZebra.setPosition(new Vector3f(-20f, 0, -20));
        graphicalZebra.setScale(new Vector3f(30, 30, 30));

        Graphical graphicalWolf = new GraphicalWolf();
        graphicalWolf.setPosition(new Vector3f(-140f, 0, -40));
        graphicalWolf.setScale(new Vector3f(30, 30, 30));

        Graphical graphicalHagrid = new GraphicalHagrid();
        graphicalHagrid.setPosition(new Vector3f(20f, 2, 2));
        graphicalHagrid.setScale(new Vector3f(15, 15, 15));

        Graphical graphicalSpider = new GraphicalSpider();
        graphicalSpider.setPosition(new Vector3f(20f, 2, -20));
        graphicalSpider.setScale(new Vector3f(5, 5, 5));

        Graphical graphicalWarrior = new GraphicalWarrior();
        graphicalWarrior.setPosition(new Vector3f(-20f, 2, 2));
        graphicalWarrior.setScale(new Vector3f(10, 10, 10));

        Graphical graphicalDude2 = new GraphicalDude();
        graphicalDude2.setPosition(new Vector3f(-20f, 0, -20));
        graphicalDude2.setScale(new Vector3f(10f, 10f, 10f));

        Graphical3DText graphical3DText = new Graphical3DText(new File("fonts/default.png"), "Hello world!", new Vector3f(0, 0.1f, 0));
        GraphicalAxis graphicalAxis = new GraphicalAxis();

        GraphicalSky graphicalSky = new GraphicalSky();

        //GraphicalText graphicalText = new GraphicalText(new File("fonts/default.png"), "1234567890.1234567890");

        new EntityFps();

        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                Graphical graphical = new GraphicalHagrid();
                graphical.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                graphical.setScale(new Vector3f(15, 15, 15));
            }
        }

    }

    public void update() {
        getCamera().update();
        getSun().update();
    }

    public void finish() {

    }
}
