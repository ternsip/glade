package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import com.ternsip.glade.universe.protocol.GridTransferClientPacket;
import com.ternsip.glade.universe.storage.Storage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class BlocksServerRepository extends BlocksRepositoryBase implements IUniverseServer {

    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();
    private static final int UPDATE_SIZE = 256;
    private final Storage storage = new Storage("server_world");

    private final Callback<OnClientConnect> onClientConnectCallback = this::whenClientConnected;

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void init() {
        super.init();
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientConnect.class, getOnClientConnectCallback());
        if (getStorage().isExists()) {
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
                    getBlocksCompressor().saveChunks();
                    getBlocksCompressor().cleanTree();
                }
            }
            log.info("World generation time spent: {}s", timer.spent() / 1000.0f);
            timer.drop();
            for (int x = 0; x < SIZE_X; x += UPDATE_LIMIT) {
                for (int y = 0; y < SIZE_Y; y += UPDATE_LIMIT) {
                    for (int z = 0; z < SIZE_Z; z += UPDATE_LIMIT) {
                        int sizeX = Math.min(UPDATE_LIMIT, SIZE_X - x);
                        int sizeY = Math.min(UPDATE_LIMIT, SIZE_Y - y);
                        int sizeZ = Math.min(UPDATE_LIMIT, SIZE_Z - z);
                        Vector3ic start = new Vector3i(x, y, z);
                        Vector3ic size = new Vector3i(sizeX, sizeY, sizeZ);
                        updateArea(start, size);
                        getPosToChunk().forEach(this::saveChunk);
                        getPosToChunk().clear();
                        getLightCompressor().saveChunks();
                        getLightCompressor().cleanTree();
                        getHeightCompressor().saveChunks();
                        getHeightCompressor().cleanTree();
                    }
                }
            }
            log.info("Chunk rendering time: {}s", timer.spent() / 1000.0f);
            saveToDisk();
        }
    }

    @Override
    public void finish() {
        super.finish();
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientConnect.class, getOnClientConnectCallback());
        compressGrids();
        saveToDisk();
        getStorage().finish();
    }

    @Override
    protected void onSidesUpdate(SidesUpdate sidesUpdate) {
    }

    private synchronized void whenClientConnected(OnClientConnect onClientConnect) {
        getUniverseServer().getServer().send(new GridTransferClientPacket(getStorage().getFileBytes()), onClientConnect.getConnection());
    }

}
