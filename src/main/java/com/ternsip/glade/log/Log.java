package com.unifi.ing.engine.log;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Log {

	private static String PATH = "/Users/maurizio/Desktop/log.txt";
	private static Log instance;
	
	private Log(){}
	
	public static Log getInstance(){
		if (instance == null){
			instance = new Log();
		}
		return instance;
	}
	
	public void write(String text) throws Exception{
		text = text + "\n";
		Files.write(Paths.get(PATH), text.getBytes(), StandardOpenOption.APPEND);
		System.out.println(text);
	}
}
