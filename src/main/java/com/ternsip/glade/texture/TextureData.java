package com.unifi.ing.engine.texture;

import java.nio.ByteBuffer;

/*
 * 
 * 
 * 
 */

public class TextureData {
	private int width;
	private int height;
	private ByteBuffer buffer;

	public TextureData (ByteBuffer buffer, int width, int height){
		this.buffer = buffer;
		this.height = height;
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	
	

}
