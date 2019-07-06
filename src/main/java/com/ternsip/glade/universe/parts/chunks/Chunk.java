package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.parts.blocks.Block;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Chunk implements Serializable {

    public final static int RELAX_PERIOD_MILLISECONDS = 3000;
    public final static int SIZE_X = 32;
    public final static int SIZE_Z = 32;

    public transient Timer timer = new Timer(RELAX_PERIOD_MILLISECONDS);
    public transient boolean modified = false;

    public Block[][][] blocks = new Block[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
    public byte[][][] skyLights = new byte[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
    public byte[][][] emitLights = new byte[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
    public int[][] heights = new int[SIZE_X][SIZE_Z];
    public Sides sides = new Sides();
    public int xPos;
    public int zPos;

    public Chunk(int xPos, int zPos) {
        this.xPos = xPos;
        this.zPos = zPos;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.timer = new Timer(RELAX_PERIOD_MILLISECONDS);
        this.modified = false;
    }

}