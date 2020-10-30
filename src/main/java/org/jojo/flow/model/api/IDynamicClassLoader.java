package org.jojo.flow.model.api;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;

/**
 * This interface represents a dynamic class loader for classes in jar files.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDynamicClassLoader extends IAPI {
    
    /**
     * The default dynamic class loader.
     * 
     * @see #getDefaultImplementation()
     */
    public static final IDynamicClassLoader DEFAULT_DYNAMIC_LOADER = getDefaultImplementation();
    
    /**
     * Gets the default implementation with an empty URLClassLoader as class loader to use.
     * 
     * @return the default implementation
     * @see #DEFAULT_DYNAMIC_LOADER
     * @see #getDefaultImplementation(ClassLoader)
     */
    public static IDynamicClassLoader getDefaultImplementation() {
        return (IDynamicClassLoader) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param class loader to use - the class loader to use
     * @return the default implementation
     */
    public static IDynamicClassLoader getDefaultImplementation(final URLClassLoader toUse) {
        return (IDynamicClassLoader) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {ClassLoader.class}, toUse);
    }

    /**
     * Loads all classes in the given jar file.
     * 
     * @param jarFile - the given jar file
     * @return a list of all the classes defined in the given jar file
     * @throws ClassNotFoundException if a class was not found
     * @throws IOException if an I/O failure occurs
     */
    List<Class<?>> loadClasses(File jarFile) throws ClassNotFoundException, IOException;

    /**
     * Loads a class by a binary name. This method exists for this loader to be used as an Java class loader.
     * 
     * @param name - the full name of the class to load
     * @return the class with the given name
     * @throws ClassNotFoundException if a class was not found
     * @see ClassLoader#loadClass(String)
     * @see #loadClasses(File)
     */
    Class<?> loadClass(String name) throws ClassNotFoundException;
    
    /**
     * Gets the Java class loader representing this class loader.
     * 
     * @return the Java class loader representing this class loader
     */
    ClassLoader getClassLoader();

    /**
     * Gets the class names of the classes in the given jar file.
     * 
     * @param jarFile - the given jar file
     * @return the class names of the classes in the given jar file
     * @throws IOException if an I/O failure occurs
     */
    List<String> getClassNames(File jarFile) throws IOException;
}