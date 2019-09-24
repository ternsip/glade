package com.ternsip.glade.universe.entities.base;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Transferable {

    void readFromStream(ObjectInputStream ois) throws IOException;
    void writeToStream(ObjectOutputStream oos) throws IOException;

}
