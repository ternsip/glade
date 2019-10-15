package com.ternsip.glade.common.logic;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

@Slf4j
public class CompressionUtils {

    @SneakyThrows
    public static byte[] compress(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(out);
        dos.write(in);
        dos.flush();
        dos.close();
        return out.toByteArray();
    }

    @SneakyThrows
    public static byte[] decompress(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InflaterOutputStream ios = new InflaterOutputStream(out);
        ios.write(in);
        ios.flush();
        ios.close();
        return out.toByteArray();
    }

}