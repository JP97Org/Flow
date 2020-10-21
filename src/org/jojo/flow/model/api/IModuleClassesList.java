package org.jojo.flow.model.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This interface represents a module classes list, i.e. a list of flow module classes.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IModuleClassesList extends IAPI {
    
    /**
     * Gets the default implementation which does not automatically load all given jars.
     * 
     * @param loader - the given class loader
     * @param jarFiles - the given jars
     * @return the default implementation
     * @see #getDefaultImplementation(IDynamicClassLoader, boolean, File...)
     */
    public static IModuleClassesList getDefaultImplementation(final IDynamicClassLoader loader, final File... jarFiles) {
        return (IModuleClassesList) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IDynamicClassLoader.class, File[].class}, loader, jarFiles);
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param loader - the given class loader
     * @param isLoadingAll - determines whether this list should always automatically load all given jars.
     * @param jarFiles - the given jars
     * @return the default implementation
     * @see #getDefaultImplementation(IDynamicClassLoader, File...)
     */
    public static IModuleClassesList getDefaultImplementation(final IDynamicClassLoader loader, 
            final boolean isLoadingAll, final File... jarFiles) {
        return (IModuleClassesList) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IDynamicClassLoader.class, boolean.class, File[].class}, loader, isLoadingAll, jarFiles);
    }

    /**
     * Gets the class loader for the jars.
     * 
     * @return the class loader for the jars
     */
    IDynamicClassLoader getClassLoader();

    /**
     * Adds a jar file. If this list {@code isloadingAll} the jar is directly loaded. 
     * If an exception occurred the jar might be loaded partially anyway.
     * 
     * @param jarFile - the jar file
     * @return this list
     * @throws ClassNotFoundException if a class is not found
     * @throws IOException if an I/O failure occurs
     * @see #loadAll()
     */
    IModuleClassesList addJarFile(File jarFile) throws ClassNotFoundException, IOException;

    /**
     * Adds and loads a jar file. If an exception occurred the jar might be loaded partially anyway.
     * A partially loaded jar counts as not loaded.
     * 
     * @param jarFile - the jar file
     * @return this list
     * @throws ClassNotFoundException if a class is not found
     * @throws IOException if an I/O failure occurs
     * @see #loadAll()
     */
    IModuleClassesList loadJarFile(File jarFile) throws ClassNotFoundException, IOException;

    /**
     * Loads all the jar files which have not already been loaded. If an exception occured
     * some jars might be loaded (even partially) anyway. A partially loaded jar counts as not loaded.
     * 
     * @return
     * @throws ClassNotFoundException if a class is not found
     * @throws IOException if an I/O failure occurs
     */
    IModuleClassesList loadAll() throws ClassNotFoundException, IOException;

    /**
     * Gets a copy of the list of flow module classes.
     * 
     * @return a copy of the list of flow module classes
     */
    List<Class<? extends IFlowModule>> getModuleClassesList();
}