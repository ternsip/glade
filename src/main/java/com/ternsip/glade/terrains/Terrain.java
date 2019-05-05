package com.unifi.ing.engine.terrains;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.unifi.ing.engine.Loader;
import com.unifi.ing.engine.model.RawModel;
import com.unifi.ing.engine.texture.TerrainTexture;
import com.unifi.ing.engine.texture.TerrainTexturePack;
import com.unifi.ing.engine.utils.Maths;

public class Terrain {

	static final int SIZE = 2800;
	private static final int VERTEX_COUNT = 64;

	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	private float[][] heights;
	
	public Terrain(int gridX, int gridZ,int x,int z, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap){
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader);
		this.x = x;
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}



	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	

	public float getHeightOfTerrain(float worldX, float worldZ){
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		
		float gridSquareSize = SIZE / ((float) heights.length -1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		

		if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0){
			return 0;
		}

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

		if (xCoord <= (1-zCoord)) {
			return Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
									heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} 
		
		return Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
									heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
	}


	private RawModel generateTerrain(Loader loader){
		
		HeightsGenerator generator = new HeightsGenerator(VERTEX_COUNT);
		
		

		
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				//Genero l'altezza del terreno a seconda delle coordinate x,z
				float height = getHeight(j, i, generator);
				heights[j][i] = height;
				vertices[vertexPointer*3+1] = height;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j,i,generator);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		
		
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}


	public Vector3f calculateNormal(int x, int z, HeightsGenerator generator){
		float heightL = getHeight(x-1, z, generator);
		float heightR = getHeight(x+1, z, generator);
		float heightD = getHeight(x, z-1, generator);
		float heightU = getHeight(x, z+1, generator);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}
	
	
	public Vector3f calculateNormal(float x, float z, HeightsGenerator generator){
		float heightL = getHeight((int)(x-1f), (int)z, generator);
		float heightR = getHeight((int)(x+1f), (int)z, generator);
		float heightD = getHeight((int)x, (int)(z-1f), generator);
		float heightU = getHeight((int)x, (int)(z+1f), generator);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}

	private float getHeight(int x, int z, HeightsGenerator generator){
		return generator.generateHeight(x, z);
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	public boolean isInside(Vector3f position){
		return isInside(position.x, position.z);
	}

	public boolean isInside(float x, float z){
		
		float xMax = this.x + SIZE;
		float xMin = this.x;
		
		float zMax = this.z + SIZE;
		float zMin = this.z;
		
		if((x > xMin && x < xMax) &&
				(z > zMin && z < zMax)){
			return true;
		}
		
		return false;
	}
	
	public void setPosition(float x,float z){
		this.x = x;
		this.z = z;
	}
}
