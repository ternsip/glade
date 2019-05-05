package com.unifi.ing.engine;

import com.unifi.ing.engine.entity.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.unifi.ing.engine.model.RawModel;
import com.unifi.ing.engine.model.TexturedModel;
import com.unifi.ing.engine.renderer.MasterRenderer;
import com.unifi.ing.engine.terrains.MultipleTerrain;
import com.unifi.ing.engine.terrains.Terrain;
import com.unifi.ing.engine.texture.ModelTexture;
import com.unifi.ing.engine.utils.DisplayManager;

import java.io.File;

public class MainGameLoop {
	public static void main(String[] args) {

		System.setProperty("org.lwjgl.librarypath", new File("lib/native").getAbsolutePath());

//		Creazione del display
		DisplayManager.createDisplay();
		
//		Inizializzazione dell'oggetto loader utilizzato per caricare modelli 3D
		Loader loader = new Loader();


		MultipleTerrain multipleTerrain = new MultipleTerrain(loader);
		
		
//		Creo l'oggetto luce e lo posiziono ad una determinata altezza
		Sun sun = new Sun(new Vector2f(0,0), new Vector2f(20000,20000), new Vector3f(1,1,1));


		MasterRenderer renderer = new MasterRenderer(loader);
		
		
//		Carico il modello del Rover
		RawModel roverModel = OBJLoader.loadObjModel("rover", loader);

		TexturedModel roverTexturedModel = new TexturedModel(roverModel, new ModelTexture(loader.loadTexture("roverTexture")));

//		Setto la posizione del rover in una determinata area
		Rover rover = new Rover(roverTexturedModel, new Vector3f(4200, 60, 4200), 0, 0, 0, 1);
		
//		Inizializzo la camera fornendogli come parametro l'oggetto da seguire
		
		Camera camera = new Camera(rover); 
		
//		List<Cube> cubes = new ArrayList<>();
//		RawModel cubeRaw = OBJLoader.loadObjModel("cube", loader); 
		
		for(int i = 1; i < 5; i++){
			float x = .0f;
			float z = .0f;

			if (i == 1)
				x = 2.0f;
			else if (i == 2)
				x = -2.0f;
			else if (i == 3)
				z = 2f;
			else
				z = -2f;
				
			TexturedModel cubeTexture = new TexturedModel(null, new ModelTexture(loader.loadTexture("cube" + i)));
			Cube cube = new Cube(cubeTexture, rover.getPosition(), 0, 0, 0, 1,x ,z,i);
			
			rover.addObserver(cube);
//			cubes.add(cube);
		}

//		Rimaniamo all'interno del ciclo finchè non vi è una richiesta di chiusura 
		while(!Display.isCloseRequested()){
//			Chiamo il metodo move del rover fornendogli come parametro il terreno su cui si deve muovere
//			Il rover si muoverà in base all'input fornito dall'utente gestito all'interno del metodo stesso
			rover.move(multipleTerrain);
			multipleTerrain.checkTerrain(rover.getPosition());
//			Ricalcolo la posizione della camera in base alla nuova posizione del Rover
			camera.move();
			
//			Eseguo il renderer sul rover
			renderer.processEntity(rover);
//			for (Cube cube : cubes) {
//				renderer.processEntity(cube);
//			}
//			Eseguo il renderer sul terreno
			
			for (Terrain terrain : multipleTerrain.getTerrains()) {
				renderer.processTerrain(terrain);
			}

			sun.move();
			renderer.render(sun, camera);
			

			DisplayManager.updateDisplay();
		}
		
//		Pulisco la memoria prima di terminare il programma
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
