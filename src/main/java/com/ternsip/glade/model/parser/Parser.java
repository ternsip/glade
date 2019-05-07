package com.ternsip.glade.model.parser;

import com.ternsip.glade.Loader;
import com.ternsip.glade.utils.Utils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class Parser {

    private Model model;
    private TypeReader reader;
    private ModelObject currentObject;

    @SneakyThrows
    public static Model load3dModel(File modelFile, File textureFile) {
        FileInputStream stream = Utils.loadResourceAsFileStream(modelFile);
        int texture = Loader.loadTexturePNG(textureFile);
        try (FileChannel channel = stream.getChannel()) {
            MapReader reader = new MapReader(channel);
            Parser parser = new Parser(reader);
            Model model = parser.parseFile();
            model.texture = texture;
            return model;
        } catch (IOException e) {
            throw new ParserException(e);
        }
    }

    public Parser(TypeReader reader) {
        this.reader = reader;
    }

    public Model parseFile() throws ParserException {
        try {
            int limit = readChunk();
            while (reader.position() < limit) {
                readChunk();
            }
        } catch (IOException e) {
            throw new ParserException(e);
        }
        return model;
    }

    private int readChunk() throws IOException {
        short type = reader.getShort();
        int size = reader.getInt(); // this is probably unsigned but for convenience we use signed int
        //System.out.println("Chunk 0x%04x (%d)" + type + ":" +  size);
        parseChunk(type, size);
        return size;
    }

    private void parseChunk(short type, int size) throws IOException {
        switch (type) {
            case 0x0002:
                parseVersionChunk();
                break;
            case 0x3d3d:
                break;
            case 0x4000:
                parseObjectChunk();
                break;
            case 0x4100:
                break;
            case 0x4110:
                parseVerticesList();
                break;
            case 0x4120:
                parseFacesDescription();
                break;
            case 0x4140:
                parseMappingCoordinates();
                break;
            case 0x4160:
                parseLocalCoordinateSystem();
                break;
            case 0x4d4d:
                parseMainChunk();
                break;
            case (short) 0xafff: // Material block
                break;
            case (short) 0xa000: // Material name
                parseMaterialName();
                break;
            case (short) 0xa200: // Texture map 1
                break;
            case (short) 0xa300: // Mapping filename
                parseMappingFilename();
                break;
            default:
                skipChunk(type, size);
        }
    }

    private void skipChunk(int type, int size) throws IOException {
        //System.out.println("skipping chunk");
        move(size - 6); // size includes headers. header is 6 bytes
    }

    private void move(int i) throws IOException {
        reader.skip(i);
    }

    private void parseMainChunk() {
        model = new Model();
    }

    private void parseVersionChunk() throws IOException {
        int version = reader.getInt();
        //System.out.println("3ds version %d" + version);
    }

    private void parseObjectChunk() throws IOException {
        String name = reader.readString();
        //System.out.println("Found Object %s"+  name);
        currentObject = model.addObject(name);
    }

    private void parseVerticesList() throws IOException {
        short numVertices = reader.getShort();
        float[] vertices = new float[numVertices * 3];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = reader.getFloat();
        }

        currentObject.vertices = vertices;
        //System.out.println("Found %d vertices" + numVertices);
    }

    private void parseFacesDescription() throws IOException {
        short numFaces = reader.getShort();
        short[] indices = new short[numFaces * 3];
        for (int i = 0; i < numFaces; i++) {
            indices[i * 3] = reader.getShort();
            indices[i * 3 + 1] = reader.getShort();
            indices[i * 3 + 2] = reader.getShort();
            reader.getShort(); // Discard face flag
        }
        //System.out.println("Found %d faces"+  numFaces);
        currentObject.indices = indices;
    }

    private void parseLocalCoordinateSystem() throws IOException {
        float[] x1 = new float[3];
        float[] x2 = new float[3];
        float[] x3 = new float[3];
        float[] origin = new float[3];
        readVector(x1);
        readVector(x2);
        readVector(x3);
        readVector(origin);
    }

    private void parseMappingCoordinates() throws IOException {
        short numVertices = reader.getShort();
        float[] uv = new float[numVertices * 2];
        for (int i = 0; i < numVertices; i++) {
            uv[i * 2] = reader.getFloat();
            uv[i * 2 + 1] = reader.getFloat();
        }
        currentObject.textureCoordinates = uv;
        //System.out.println("Found %d mapping coordinates "+ numVertices);
    }

    private void parseMaterialName() throws IOException {
        String materialName = reader.readString();
        //System.out.println("Material name %s "+ materialName);
    }

    private void parseMappingFilename() throws IOException {
        String mappingFile = reader.readString();
        //System.out.println("Mapping file %s" + mappingFile);
    }

    private void readVector(float[] v) throws IOException {
        v[0] = reader.getFloat();
        v[1] = reader.getFloat();
        v[2] = reader.getFloat();
    }

}
