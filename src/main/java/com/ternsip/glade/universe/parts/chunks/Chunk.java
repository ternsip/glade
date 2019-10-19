package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.parts.blocks.Block;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Chunk implements Serializable {

    public static final int RELAX_PERIOD_MILLISECONDS = 30000;
    public static final int SIZE_X = 32;
    public static final int SIZE_Z = 32;
    public static final Vector3ic SIZE = new Vector3i(SIZE_X, BlocksRepository.SIZE_Y, SIZE_Z);
    public static final Indexer INDEXER = new Indexer(SIZE);

    public volatile transient Timer timer = new Timer(RELAX_PERIOD_MILLISECONDS);
    public volatile transient boolean modified = false;

    public volatile Block[][][] blocks = new Block[SIZE_X][BlocksRepository.SIZE_Y][SIZE_Z]; // TODO why volatile???
    public volatile Sides sides = new Sides();
    public volatile EngagedBlocks engagedBlocks = new EngagedBlocks();
    public volatile int xPos;
    public volatile int zPos;

    public Chunk(int xPos, int zPos) {
        this.xPos = xPos;
        this.zPos = zPos;
    }

    public Vector3ic getStart() {
        return new Vector3i(xPos * Chunk.SIZE_X, 0, zPos * Chunk.SIZE_Z);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.timer = new Timer(RELAX_PERIOD_MILLISECONDS);
        this.modified = false;
    }

}