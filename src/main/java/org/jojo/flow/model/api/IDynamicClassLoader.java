package org.jojo.flow.model.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This interface represents a dynamic class loader for a jar file.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDynamicClassLoader extends IAPI {
    
    /**
     * Gets the default implementation with the default Java class loader as parent.
     * 
     * @param tmpDirForJarExtraction - the temp directory which is used for extracting the classes to
     * @return the default implementation
     */
    public static IDynamicClassLoader getDefaultImplementation(final File tmpDirForJarExtraction) {
        return (IDynamicClassLoader) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {File.class}, tmpDirForJarExtraction);
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param parent - the parent Java class loader
     * @param tmpDirForJarExtraction - the temp directory which is used for extracting the classes to
     * @return the default implementation
     */
    public static IDynamicClassLoader getDefaultImplementation(final ClassLoader parent, final File tmpDirForJarExtraction) {
        return (IDynamicClassLoader) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {ClassLoader.class, File.class}, parent, tmpDirForJarExtraction);
    }
    
    //TODO doc the methods
    
    void putExternalClass(final String name, final File file);
    
    Map<String, File> getExternalClassesMap();
    
    List<File> unpack(final File jarFile) throws IOException;
    
    String binaryNameOf(final String absPathName);

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
     * Loads a class by a name. This method exists for this loader to be used as an Java class loader.
     * 
     * @param name - the full name of the class to load
     * @return the class with the given name
     * @throws ClassNotFoundException if a class was not found
     * @see #loadClasses(File)
     */
    Class<?> loadClass(String name) throws ClassNotFoundException;
    
    /**
     * Gets the Java class loader representing this class loader.
     * 
     * @return the Java class loader representing this class loader
     */
    ClassLoader getClassLoader();
}