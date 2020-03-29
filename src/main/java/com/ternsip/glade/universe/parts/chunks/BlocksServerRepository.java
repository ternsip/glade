package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import com.ternsip.glade.universe.protocol.GridTransferClientPacket;
import com.ternsip.glade.universe.storage.Storage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class BlocksServerRepository extends BlocksRepositoryBase implements Threadable, IUniverseServer {

    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();
    private static final int UPDATE_SIZE = 256;

    private final Callback<OnClientConnect> onClientConnectCallback = this::whenClientConnected;
    private final Storage storage;

    public BlocksServerRepository() {
        this.storage = new Storage("blocks_meta");
        if (storage.isExists()) {
            loadFromDisk();
        } else {
            Timer timer = new Timer();
            log.info("World generation has been stared");
            for (int x = 0; x < SIZE_X; x += UPDATE_SIZE) {
                for (int z = 0; z < SIZE_Z; z += UPDATE_SIZE) {
                    int endX = Math.min(x + UPDATE_SIZE, SIZE_X) - 1;
                    int endZ = Math.min(z + UPDATE_SIZE, SIZE_Z) - 1;
                    for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
                        chunkGenerator.populate(this, x, z, endX, endZ);
                    }
                }
            }
            saveToDisk();
            log.info("World generation time spent: {}s", timer.spent() / 1000.0f);
        }
    }

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

    @Override
    public void init() {
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientConnect.class, getOnClientConnectCallback());
    }

    @Override
    public void update() {
    }

    @Override
    public void finish() {
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientConnect.class, getOnClientConnectCallback());
        saveToDisk();
        storage.finish();
    }

    private void whenClientConnected(OnClientConnect onClientConnect) {
        getUniverseServer().getServer().send(new GridTransferClientPacket(getGridBlocks()), onClientConnect.getConnection());
    }

    private void saveToDisk() {
        getGridBlocks().saveChunks();
        getGridBlocks().cleanTree();
        storage.save("block", getGridBlocks());
    }

    private void loadFromDisk() {
        setGridBlocks(storage.load("block"));
    }

}
