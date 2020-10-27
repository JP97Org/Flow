package org.jojo.flow.model.api;

import java.io.File;

/**
 * This interface represents a facade for storing and loading.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IStoreLoadFacade extends IAPI {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     * @see #getDefaultImplementation(IModuleClassesList)
     * @see #getDefaultImplementation(IModuleClassesList, IDynamicClassLoader)
     */
    public static IStoreLoadFacade getDefaultImplementation() {
        return (IStoreLoadFacade) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param list - the module classes list to use
     * @return the default implementation
     * @see #getDefaultImplementation(IModuleClassesList, IDynamicClassLoader)
     */
    public static IStoreLoadFacade getDefaultImplementation(final IModuleClassesList list) {
        return (IStoreLoadFacade) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IModuleClassesList.class}, list);
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param list - the module classes list to use
     * @param loader - the class loader to use for 
     * @return the default implementation
     */
    public static IStoreLoadFacade getDefaultImplementation(final IModuleClassesList list, IDynamicClassLoader loader) {
        return (IStoreLoadFacade) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IModuleClassesList.class, IDynamicClassLoader.class}, list, loader);
    }

    /**
     * Loads a flow chart from a xml file. If this method fails it reports an error Warning.
     * 
     * @param xmlFile - the given xml file
     * @return the loaded flow chart or {@code null} if loading was not successful.
     */
    IFlowChart loadFlowChart(File xmlFile);

    /**
     * Stores a flow chart to a xml file. If this method fails it reports an error Warning.
     * 
     * @param xmlFile - the given xml file
     * @param flowDom - the flow DOM
     * @return whether storing was not successful.
     */
    boolean storeFlowChart(File xmlFile, IDOM flowDom);

    /**
     * Gets the pair of module classes list and class loader.
     * 
     * @return the pair of module classes list and class loader or {@code null} if no list is set
     */
    Pair<IModuleClassesList, IDynamicClassLoader> getListLoaderPair();

    /**
     * Gets a new {@link IModuleClassesList}.
     * 
     * @param tmpDirectory - the temporary directory where to store the extracted classes
     * @param jars - the jar files
     * @return a new IModuleClassesList
     */
    IModuleClassesList getNewModuleClassesList(File tmpDirectory, File... jars);

    /**
     * Gets a new {@link IDynamicClassLoader}.
     * 
     * @param tmpDirectory - the temporary directory where to store the extracted classes
     * @return a new IDynamicClassLoader
     */
    IDynamicClassLoader getNewDynamicClassLoader(File tmpDirectory);
}