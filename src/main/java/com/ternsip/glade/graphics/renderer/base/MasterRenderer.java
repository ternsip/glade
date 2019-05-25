package com.ternsip.glade.graphics.renderer.base;

import com.ternsip.glade.universe.Universe;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
public class MasterRenderer {

    public static MasterRenderer INSTANCE;

    private final Universe universe;
    private final Collection<Renderer> renders;

    public MasterRenderer(Universe universe, Set<Renderer> renders) {
        INSTANCE = this;
        this.universe = universe;
        this.renders = renders
                .stream()
                .sorted(Comparator.comparing(Renderer::getPriority))
                .collect(Collectors.toList());
    }

    public void render() {
        renders.forEach(Renderer::render);
    }

    public void finish() {
        renders.forEach(Renderer::finish);
    }

}
