package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.EntityServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class EntitiesStateClientPacket extends ClientPacket {

    private final byte[] data;
    private final int size;

    @SneakyThrows
    public EntitiesStateClientPacket(Collection<EntityServer> serverEntities) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                int processedEntities = 0;
                for (EntityServer entityServer : serverEntities) {
                    if (!entityServer.isTransferable()) {
                        continue;
                    }
                    oos.writeLong(entityServer.getUuid().getMostSignificantBits());
                    oos.writeLong(entityServer.getUuid().getLeastSignificantBits());
                    entityServer.writeToStream(oos);
                    processedEntities++;
                }
                oos.flush();
                this.data = bos.toByteArray();
                this.size = processedEntities;
            }
        }
    }

    @Override
    @SneakyThrows
    public void apply(Connection connection) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(getData())) {
            try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                EntityClient lastEntity = null;
                for (int i = 0; i < getSize(); ++i) {
                    try {
                        UUID uuid = new UUID(ois.readLong(), ois.readLong());
                        lastEntity = getUniverseClient().getEntityClientRepository().getEntityByUUID(uuid);
                        lastEntity.readFromStream(ois);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(String.format("Broken entity: %s - %s", lastEntity, e.getMessage()), e);
                    }
                }
            }
        }
    }

}
