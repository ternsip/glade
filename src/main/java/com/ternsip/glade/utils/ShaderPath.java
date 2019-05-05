package com.unifi.ing.engine.utils;

public class ShaderPath {
	private static String PATH = "/com/unifi/ing/engine/shader/";
	public static String getShaderPath(String fileName){
		return PATH + fileName; 
	}
}
