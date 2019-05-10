package com.ternsip.glade.model.loader.engine.textures;

import com.ternsip.glade.utils.Utils;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import lombok.SneakyThrows;
import org.lwjgl.opengl.*;

import java.io.File;
import java.nio.ByteBuffer;

import static com.ternsip.glade.utils.Utils.arrayToBuffer;
import static com.ternsip.glade.utils.Utils.bufferToArray;
import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class TextureUtils {

    @SneakyThrows
    public static TextureData decodeTextureFile(File file) {
        PNGDecoder decoder = new PNGDecoder(Utils.loadResourceAsStream(file));
        int width = decoder.getWidth();
        int height = decoder.getHeight();
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
        decoder.decode(buffer, width * 4, Format.BGRA);
        return new TextureData(bufferToArray(buffer), width, height);
    }

    public static int loadTextureToOpenGL(TextureData textureData, TextureBuilder builder) {
        int texID = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureData.getWidth(), textureData.getHeight(), 0, GL12.GL_BGRA,
                GL_UNSIGNED_BYTE, arrayToBuffer(textureData.getData()));
        if (builder.isMipmap()) {
            GL30.glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            if (builder.isAnisotropic() && getCapabilities().GL_EXT_texture_filter_anisotropic) {
                glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
                glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 4.0f);
            }
        } else if (builder.isNearest()) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        }
        if (builder.isClampEdges()) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
        return texID;
    }

}
