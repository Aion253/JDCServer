package net.aionstudios.jdc.server.content;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import net.aionstudios.jdc.JDC;

public class ContentLoader {
	
	private static URLClassLoader ucl;
	private static List<URL> urls = new ArrayList<URL>();
	//Should add support for settings based import of dependencies not included in the library or server so that they don't have to be re-packaged in order for the project to run.
	
	public static void initializeClassLoader() {
		//Should read a file for urls and other information first, then load classes.
		ucl = new URLClassLoader(urls.toArray(new URL[0]), ContentLoader.class.getClassLoader());
	}
	
	public static void addClassLoaderURL(File f) {
		if(f.exists()) {
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				System.err.println("Failed to convert file to URL!");
				e.printStackTrace();
			}
		} else {
			try {
				System.err.println("Failed to add file '"+f.getCanonicalPath()+"', it doesn't exist!");
			} catch (IOException e) {
				System.err.println("Failed accessing file system!");
				e.printStackTrace();
			}
		}
	}
	
	public static JDC getJDCFromLibrary(String jdcClass) {
		try {
			Class<?> classToLoad = Class.forName(jdcClass, true, ucl);
			JDC instance = (JDC) classToLoad.newInstance();
			return instance;
		} catch (ClassNotFoundException e) {
			System.err.println("Failed loading external JDC!");
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			System.err.println("Failed loading external JDC!");
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			System.err.println("Failed loading external JDC!");
			e.printStackTrace();
			return null;
		}
	}

}
