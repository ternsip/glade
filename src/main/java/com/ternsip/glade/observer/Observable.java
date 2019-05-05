package com.ternsip.glade.observer;

import com.ternsip.glade.terrains.MultipleTerrain;

public interface Observable {
	public void notifyEntity(MultipleTerrain terrain);
}