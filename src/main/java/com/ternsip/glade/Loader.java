package com.ternsip.glade;

import com.ternsip.glade.model.RawModel;
import com.ternsip.glade.texture.TextureData;
import com.ternsip.glade.utils.Utils;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import lombok.SneakyThrows;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;


public class Loader {

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();

	public RawModel loadToVAO(float [] positions, float[] textureCoords,float[] normals, int[] indices){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,2, textureCoords);
		storeDataInAttributeList(2,3, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	//TODO ricontrollare che venga effettivamente utilizzato
	public RawModel loadToVAO(float [] positions, int dimensions){
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length/dimensions);
	}

	@SneakyThrows
	public static int loadTexturePNG(File file){

		//load png file
		PNGDecoder decoder = new PNGDecoder(Utils.loadResourceAsStream(file));

		//create a byte buffer big enough to store RGBA values
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

		//decode
		decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

		//flip the buffer so its ready to read
		buffer.flip();

		//create a texture
		int id = glGenTextures();

		//bind the texture
		glBindTexture(GL_TEXTURE_2D, id);

		//tell opengl how to unpack bytes
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		//set the texture parameters, can be GL_LINEAR or GL_NEAREST
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		//upload texture
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		// Generate Mip Map
		glGenerateMipmap(GL_TEXTURE_2D);

		return id;
	}

	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	public void cleanUp(){
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for (int  vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture:textures){
			GL11.glDeleteTextures(texture);
		}
	}

	private void storeDataInAttributeList(int attributeNumber,int coordinateSize, float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}


	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}

	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	private IntBuffer storeInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public int loadCubeMap(List<String> textureFile){
		int texID = glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for (int i = 0; i < textureFile.size(); i++) {

			TextureData data = decodeTextureFile("res/" + textureFile.get(i) + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), 
					data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		textures.add(texID);
		
		return texID;
	}

	private TextureData decodeTextureFile(String fileName){
		int with = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try{
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			with = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * with * height);
			decoder.decode(buffer, with * 4, Format.RGBA);
			buffer.flip();
			in.close();
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Thread to load texture " + fileName + "did not work");
			System.exit(-1);
		}
		return new TextureData(buffer, with, height);
	}
}
