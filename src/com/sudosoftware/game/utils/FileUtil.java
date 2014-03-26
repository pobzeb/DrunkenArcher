package com.sudosoftware.game.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.sudosoftware.game.Game;

public class FileUtil {

	public static String getFileInSameLevelOf(String path, String name) {
		path = path.replaceAll("\\\\", "/");
		path = path.substring(0, path.lastIndexOf("/") + 1) + name;
		return path;
	}

	public static String readFromFile(String name) {
		StringBuilder source = new StringBuilder();

		try {
			System.out.println("Loading File: " + name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(Game.class.getClassLoader().getResourceAsStream(name)));
			String line;
			while ((line = reader.readLine()) != null) {
				source.append(line).append("\n");
			}
			reader.close();
		}
		catch (Exception e) {
			System.err.println("Error loading file: " + name);
			e.printStackTrace();
			Game.end();
		}

		return source.toString();
	}

	public static String[] readAllLines(String name) {
		return readFromFile(name).split("\n");
	}
}
