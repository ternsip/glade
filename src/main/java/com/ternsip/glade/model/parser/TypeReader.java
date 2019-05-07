package com.ternsip.glade.model.parser;

import java.io.IOException;

/**
 * A generic inteface used for converting a byte stream to types used in a
 * 3ds file.
 *
 * @author Kjetil �ster�s
 */
public interface TypeReader {
    short getShort() throws IOException;

    int getInt() throws IOException;

    float getFloat() throws IOException;

    void skip(int i) throws IOException;

    String readString() throws IOException;

    int position();
}
