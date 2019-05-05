package com.unifi.ing.engine.terrains;

import java.util.Random;

public class HeightsGenerator {
	private static final float AMPLITUDE = 70f;
	private static final int OCTAVES = 3;
	private static final float ROUGHNESS = 0.3f;

	private Random random = new Random();
	private int seed;
	private int xOffset = 0;
	private int zOffset = 0;
	private int vertexCount;

	public HeightsGenerator(int vertexCount) {
		this.seed = random.nextInt(1000000000);
		this.vertexCount = vertexCount - 2;
	}


	public float generateHeight(int x, int z) {
		float total = 0;
		
		if(!(x > 2 && z > 2 && x < vertexCount && z < vertexCount))
			return total;
		
		float d = (float) Math.pow(2, OCTAVES-1);
		for(int i=0;i<OCTAVES;i++){
			float freq = (float) (Math.pow(2, i) / d);
			float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
			total += getInterpolatedNoise((x + xOffset)*freq, (z + zOffset)*freq) * amp;
		}
		
		return total;
		
	}

	private float getInterpolatedNoise(float x, float z){
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		//Ottengo la parte frazionata di x e z
		//calcolo i vertici smussati del quadrato attorno al punto in cui mi trovo 

		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		//a questo punto interpolo i punti per ottenere l'altezza
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}

	//	l'idea è quella di interpolare ogni vertice con la funzione coseno in modo tale da ottenere un terreno più fluido
	private float interpolate(float a, float b, float blend){
		double theta = blend * Math.PI;
		float f = (float)(1f - Math.cos(theta)) * 0.5f;
		return a * (1f - f) + b * f;
	}

	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
		+ getNoise(x + 1, z + 1)) / 16f; //mi calcolo l'altezza di ogni angolo
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1)
		+ getNoise(x, z + 1)) / 8f;//successivamente mi calcolo le altezze dei vertici ai lati
		float center = getNoise(x, z) / 4f;//per ultimo mi calcolo l'altezza del vertice centrale
		return corners + sides + center;
	}


	//Il terreno viene generato in modo Random ma ovviamente ci aspettiamo che ogni volta in cui
	//Ci troviamo nelle coordinate x,z il terreno deve avere sempre la stessa altezza
	private float getNoise(int x, int z) {

		//I due numeri a moltiplicare sono stati aggiunti in quanto per valori molto vicini di x,z
		//veniva generato lo stesso output in questo modo riusciamo ad avere valori sempre distinti.
		random.setSeed(x * 49632 + z * 325176 + seed);
		return random.nextFloat() * 2f - 1f;
	
	//Attraverso l'utilizzo esclusivo di questo metodo abbiamo come output un terreno non uniforme 
	//quindi abbiamo bisogno di metodi che ci garantiscono la possibilità di interpolare i vari punti generati
	//in modo da avere un terreno più smussato
}
}
