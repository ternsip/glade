package com.ternsip.glade.graphics.entities.base;

import com.ternsip.glade.graphics.renderer.base.Renderer;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class FigureRepository {

    private final Map<Class<? extends Renderer>, Set<Figure>> figures = new HashMap<>();

    public void addFigure(Figure figure) {
        figures.computeIfAbsent(figure.getRenderer(), e -> new HashSet<>()).add(figure);
    }

    public void removeFigure(Figure figure) {
        figures.get(figure.getRenderer()).remove(figure);
    }

    public void update() {
        figures.values().forEach(e -> e.forEach(Figure::update));
    }

    public void finish() {
        figures.values().forEach(e -> e.forEach(Figure::finish));
    }

}
