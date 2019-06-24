package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

@Getter
public class Chunk implements Serializable {

    public final static int RELAX_PERIOD_MILLISECONDS = 3000;
    public final static int SIZE_X = 32;
    public final static int SIZE_Z = 32;

    private transient Timer timer = new Timer(RELAX_PERIOD_MILLISECONDS);

    @Setter
    private transient boolean modified = false;

    private Block[][][] blocks = new Block[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
    private byte[][][] skyLights = new byte[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
    private byte[][][] emitLights = new byte[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
    private int[][] heights = new int[SIZE_X][SIZE_Z];

    private Sides sides = new Sides();
    private int xPos;
    private int zPos;

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