package com.ternsip.glade.renderer.base;

import com.ternsip.glade.utils.Utils;
import org.reflections.Reflections;

import java.util.Collection;
import java.util.stream.Collectors;

public class MasterRenderer {

    private Collection<Renderer> renders;

    public void initialize() {
        Reflections reflections = new Reflections();
        renders = reflections.getSubTypesOf(Renderer.class).stream().map(Utils::createInstanceSilently).collect(Collectors.toList());
    }

    public void render() {
        renders.forEach(Renderer::render);
    }

    public void finish() {
        renders.forEach(Renderer::finish);
    }

}
