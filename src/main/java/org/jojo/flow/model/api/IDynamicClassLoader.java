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
    
    /**
     * Puts the external class defined by the given class name as an external class located
     * in the given class file.
     * 
     * @param name - the given class name (must not be {@code null})
     * @param file - the class given file (must not be {@code null})
     */
    void putExternalClass(final String name, final File file);
    
    /**
     * Gets a copy of the mappings from class names to external class files.
     * 
     * @return a copy of the mappings from class names to external class files
     */
    Map<String, File> getExternalClassesMap();
    
    /**
     * Unpacks the given jar files class files to the temp directory specified at construction time.
     * 
     * @param jarFile - the given jar file
     * @return the list of extracted class files
     * @throws IOException if an I/O failure occurs
     */
    List<File> unpack(final File jarFile) throws IOException;
    
    /**
     * Gets the binary name of the public class defined by the absolute path name of the respective
     * class file.
     * 
     * @param absPathName - the absolute path name of the respective class file
     * @return the binary name of the public class
     */
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
}