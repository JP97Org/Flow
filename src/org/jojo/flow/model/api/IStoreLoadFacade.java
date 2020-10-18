package org.jojo.flow.model.api;

import java.io.File;

import org.jojo.flow.model.data.Pair;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;
import org.jojo.flow.model.storeLoad.ModuleClassesList;

public interface IStoreLoadFacade extends IAPI {

    FlowChart loadFlowChart(File xmlFile);

    boolean storeFlowChart(File xmlFile, DOM flowDom);

    Pair<ModuleClassesList, DynamicClassLoader> getListLoaderPair();

    ModuleClassesList getNewModuleClassesList(File tmpDirectory, File... jars);

    DynamicClassLoader getNewDynamicClassLoader(File tmpDirectory);
}