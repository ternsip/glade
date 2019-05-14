package com.ternsip.glade.universal;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.ternsip.glade.utils.Utils.arrayToBuffer;
import static com.ternsip.glade.utils.Utils.loadResourceAsByteArray;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;

public class Font {

    public static final File FONT_AMATIC = new File("fonts/amatic.ttf");
    private static final int BITMAP_W = 512;
    private static final int BITMAP_H = 512;
    public Map<File, Integer> fontToTexture = new HashMap<>();

    public int generateTexture(File font, int fontHeight) {
        if (fontToTexture.containsKey(font)) {
            return fontToTexture.get(font);
        }
        int texID = glGenTextures();
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
        ByteBuffer ttf = arrayToBuffer(loadResourceAsByteArray(font));
        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        stbtt_BakeFontBitmap(ttf, fontHeight, bitmap, BITMAP_W, BITMAP_H, 32, cdata);
        glBindTexture(GL_TEXTURE_2D, texID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        fontToTexture.put(font, texID);
        return texID;
    }


}