package com.ternsip.glade.observer;

import com.ternsip.glade.entity.Entity;
import com.ternsip.glade.terrains.MultipleTerrain;

public interface Observer {
	public void update(Entity entity, MultipleTerrain terrain);
}