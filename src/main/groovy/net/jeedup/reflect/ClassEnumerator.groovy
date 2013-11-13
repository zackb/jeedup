package net.jeedup.reflect

import groovy.transform.CompileStatic

import java.util.jar.JarEntry
import java.util.jar.JarFile


@CompileStatic
class ClassEnumerator {

	private static Class<?> loadClass(String className) {
		try {
			return Class.forName(className)
		} 
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'")
		}
	}

	private static void processDirectory(File directory, String pkgname, ArrayList<Class<?>> classes) {
        for (String fileName : directory.list()) {
			String className = null
			if (fileName.endsWith(".class")) {
				className = pkgname + '.' + fileName.substring(0, fileName.length() - 6)
			}
			if (className != null) {
				classes.add(loadClass(className))
			}
			File subdir = new File(directory, fileName)
			if (subdir.isDirectory()) {
				processDirectory(subdir, pkgname + '.' + fileName, classes)
			}
		}
	}

	private static void processJarfile(URL resource, String pkgname, ArrayList<Class<?>> classes) {
		String relPath = pkgname.replace('.', '/')
		String resPath = resource.getPath()
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "")
		JarFile jarFile

		try {
			jarFile = new JarFile(jarPath)
		} catch (IOException e) {
			throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e)
		}
		Enumeration<JarEntry> entries = jarFile.entries()
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement()
			String entryName = entry.getName()
			String className = null
			if(entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
				className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "")
			}
			if (className != null) {
				classes.add(loadClass(className))
			}
		}
	}
	
	public static ArrayList<Class<?>> getClassesForPackage(String pkgname) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>()
		
		String relPath = pkgname.replace('.', '/')
	
		// Get a File object for the package
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath)
		if (resource == null) {
			throw new RuntimeException("Unexpected problem: No resource for " + relPath)
		}

		resource.getPath()
		if(resource.toString().startsWith("jar:")) {
			processJarfile(resource, pkgname, classes)
		} else {
			processDirectory(new File(resource.getPath()), pkgname, classes)
		}

		return classes
	}
}
