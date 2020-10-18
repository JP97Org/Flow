package org.jojo.flow.model.api;

import java.io.File;

import org.jojo.flow.model.storeLoad.DOM;

public interface IStoreLoadFacade extends IAPI {
    public static IStoreLoadFacade getDefaultImplementation() {
        return (IStoreLoadFacade) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }
    
    public static IStoreLoadFacade getDefaultImplementation(final IModuleClassesList list) {
        return (IStoreLoadFacade) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IModuleClassesList.class}, list);
    }
    
    public static IStoreLoadFacade getDefaultImplementation(final IModuleClassesList list, IDynamicClassLoader loader) {
        return (IStoreLoadFacade) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IModuleClassesList.class, IDynamicClassLoader.class}, list, loader);
    }

    IFlowChart loadFlowChart(File xmlFile);

    boolean storeFlowChart(File xmlFile, DOM flowDom);

    Pair<IModuleClassesList, IDynamicClassLoader> getListLoaderPair();

    IModuleClassesList getNewModuleClassesList(File tmpDirectory, File... jars);

    IDynamicClassLoader getNewDynamicClassLoader(File tmpDirectory);
}