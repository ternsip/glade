package com.ternsip.glade.universe.storage;

import com.ternsip.glade.common.logic.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Getter
public class Storage {

    private static final File STORAGE_PARENT_FOLDER = new File("storage");
    private static final String EXTENSION = "dat";

    private final File file;
    private final DB db;
    private final ConcurrentMap map;
    private final boolean exists;

    @SneakyThrows
    public static Storage fromBytes(byte[] data, String name) {
        File file = new File(STORAGE_PARENT_FOLDER, name + "." + EXTENSION);
        Utils.ensureFileFolder(file);
        FileUtils.writeByteArrayToFile(file, data);
        return new Storage(name);
    }

    @SneakyThrows
    public Storage(String name) {
        this.file = new File(STORAGE_PARENT_FOLDER, name + "." + EXTENSION);
        Utils.ensureFileFolder(file);
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
    public synchronized void save(Object key, Object value) {
        getMap().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public synchronized void saveAll(Map m) {
        getMap().putAll(m);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T load(Object key) {
        return (T) getMap().get(key);
    }

    public synchronized boolean isExists(Object key) {
        return getMap().containsKey(key);
    }

    public synchronized void commit() {
        getDb().commit();
    }

    @SneakyThrows
    public synchronized byte[] getFileBytes() {
        getDb().commit();
        return IOUtils.toByteArray(new FileInputStream(file));
    }

    public synchronized void finish() {
        commit();
        getDb().close();
    }

}
