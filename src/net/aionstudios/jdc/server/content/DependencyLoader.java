package net.aionstudios.jdc.server.content;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

import net.aionstudios.jdc.JDC;

public class DependencyLoader {
	
	private static URL[] dependencies;
	private static URLClassLoader urlClassLoader;
	
	/**
	 * Adds a {@link URL} from which to load classes by reference to a jar file.
	 * @param f		The jar file from which to obtain a {@link URL}.
	 */
	public static void loadDependencies() {
		List<URL> urls = new ArrayList<>();
		File f = new File("./dependencies");
		if(!f.exists()) f.mkdir();
		for(File c : f.listFiles()) {
			System.out.println("Dependency: "+c.getName());
			if(c.getPath().endsWith(".jar")) {
				try {
					urls.add(c.toURI().toURL());
				} catch (MalformedURLException e) {
					System.out.println("Dependency failed: '"+c.getName()+"'!");
					e.printStackTrace();
				}
			}
		}
		dependencies = urls.toArray(new URL[0]);
		urlClassLoader = new URLClassLoader(dependencies, ClassLoader.getSystemClassLoader());
	}
	
	public static URL[] getDependencies() {
		return dependencies;
	}
	
	public static URLClassLoader getUrlClassLoader() {
		return urlClassLoader;
	}

}