package org.jojo.flow.model.api;

import java.io.File;

import org.jojo.flow.model.storeLoad.DOM;

public interface IStoreLoadFacade extends IAPI {

    IFlowChart loadFlowChart(File xmlFile);

    boolean storeFlowChart(File xmlFile, DOM flowDom);

    Pair<IModuleClassesList, IDynamicClassLoader> getListLoaderPair();

    IModuleClassesList getNewModuleClassesList(File tmpDirectory, File... jars);

    IDynamicClassLoader getNewDynamicClassLoader(File tmpDirectory);
}