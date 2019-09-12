package com.ternsip.glade.universe.storage;

import lombok.Getter;
import lombok.SneakyThrows;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

@Getter
public class Storage {

    private final File STORAGE_PARENT_FOLDER = new File("storage");
    private final String EXTENSION = "dat";

    private final File file;
    private final DB db;
    private final ConcurrentMap map;
    private final boolean exists;

    @SneakyThrows
    public Storage(String name) {
        this.file = new File(STORAGE_PARENT_FOLDER, name + "." + EXTENSION);
        File parent = this.file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        this.exists = file.exists();
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
