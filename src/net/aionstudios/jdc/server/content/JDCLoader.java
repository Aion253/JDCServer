package net.aionstudios.jdc.server.content;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import net.aionstudios.jdc.JDC;

/**
 * Loads external archives into the JVM and creates new {@link JDC} instances for {@link Website}'s {@link ContentProcessor}s.
 * @author Winter
 *
 */
public class JDCLoader {
	
	private static URLClassLoader ucl;
	private static List<URL> urls = new ArrayList<URL>();
	//Should add support for settings based import of dependencies not included in the library or server so that they don't have to be re-packaged in order for the project to run.
	
	/**
	 * Creates a new {@link URLClassLoader} using the JVM class loader and a list of jar files to be loaded.
	 * <p>
	 * The JVM class loader is obtained by calling <Class>.class.getClassLoader().
	 */
	public static void initializeClassLoader() {
		//Should read a file for urls and other information first, then load classes.
		ucl = new URLClassLoader(urls.toArray(new URL[0]), DependencyLoader.getUrlClassLoader());
	}
	
	/**
	 * Adds a {@link URL} from which to load classes by reference to a jar file.
	 * @param f		The jar file from which to obtain a {@link URL}.
	 */
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
	
	/**
	 * Instantiates a class, given its fully qualified name, after its archive's {@link URL} has been added into the JVM via a {@link URLClassLoader} and the method {@link JDCLoader#initializeClassLoader()}.
	 * <p>
	 * We assume the type of the class to be {@link JDC} as expected and should an error occur, return nothing.
	 * @param jdcClass		The fully qualified name of a {@link JDC} class within an external library.
	 * @return A {@link JDC} instance if one was found, legally accessed and properly cast and instantiated at the given fully qualified name within one of the archives available to the JVM's class loader. Null otherwise.
	 */
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
	
	public static JDC getSingleJDC(File f, String jdcClass) {
		try {
			if(f.exists()) {
				URL[] u = {f.toURI().toURL()};
				Class<?> classToLoad = Class.forName(jdcClass, true, new URLClassLoader(u, DependencyLoader.getUrlClassLoader()));
				JDC instance = (JDC) classToLoad.newInstance();
				return instance;
			}
			System.err.println("Failed loading external JDC!");
			System.err.println("File does not exist!");
			return null;
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
		} catch (MalformedURLException e) {
			System.err.println("Failed loading external JDC!");
			e.printStackTrace();
			return null;
		}
	}

}
