package com.unifi.ing.engine.utils;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	
	/*
	 * La classe si occupa di creare l'iterfaccia grafica
	 * che consente di mostrare gli oggetti in 3d
	 * 
	 */
	public static void createDisplay(){
		
		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		attribs.withForwardCompatible(true);
		attribs.withProfileCompatibility(true);
		
		try {
			//Imposto le dimensioni del display
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("3D Test");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		/*
		 * La classe GL11 contiene tutti i metodi
		 * forniti tramite la libreria OpenGL 1.1
		 * 
		 */
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay(){
		Display.sync(FPS_CAP);
		Display.update();
		
		/*
		 * currentFrameTime viene utilizzato principalmente per
		 * calcolare quanto tempo intercorre tra un frame e l'altro 
		 *  
		 */
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds(){
		return delta;
	}
	public static void closeDisplay(){
		Display.destroy();
	}
	
	private static long getCurrentTime(){
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
}
