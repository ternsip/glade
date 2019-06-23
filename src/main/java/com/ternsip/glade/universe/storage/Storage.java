package com.ternsip.glade.universe.storage;

import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import lombok.SneakyThrows;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

@Getter
public class Storage implements Universal {

    private final File STORAGE_PARENT_FOLDER = new File("storage");
    private final String EXTENSION = "dat";

    private final File file;
    private final DB db;
    private final ConcurrentMap map;

    @SneakyThrows
    public Storage(String name) {
        this.file = new File(new File(STORAGE_PARENT_FOLDER, getUniverse().getName()), name + "." + EXTENSION);
        File parent = this.file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        this.db = DBMaker
                .fileDB(file)
                .transactionEnable()
                .closeOnJvmShutdown()
                .fileChannelEnable()
                .make();
        this.map = db.hashMap("map").createOrOpen();
    }

    @SuppressWarnings("unchecked")
    public void save(Object key, Object value) {
        getMap().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T load(Object key) {
        return (T) getMap().get(key);
    }

    public boolean isExists(Object key) {
        return getMap().containsKey(key);
    }

    public void commit() {
        getDb().commit();
    }

    public void finish() {
        commit();
        getDb().close();
    }

}
