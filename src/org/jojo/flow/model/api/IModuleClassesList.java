package org.jojo.flow.model.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;
import org.jojo.flow.model.storeLoad.ModuleClassesList;

public interface IModuleClassesList extends IAPI {

    DynamicClassLoader getClassLoader();

    ModuleClassesList addJarFile(File jarFile) throws ClassNotFoundException, IOException;

    ModuleClassesList loadJarFile(File jarFile) throws ClassNotFoundException, IOException;

    ModuleClassesList loadAll() throws ClassNotFoundException, IOException;

    List<Class<? extends FlowModule>> getModuleClassesList();
}