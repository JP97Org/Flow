package org.jojo.flow.model.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IModuleClassesList extends IAPI {

    IDynamicClassLoader getClassLoader();

    IModuleClassesList addJarFile(File jarFile) throws ClassNotFoundException, IOException;

    IModuleClassesList loadJarFile(File jarFile) throws ClassNotFoundException, IOException;

    IModuleClassesList loadAll() throws ClassNotFoundException, IOException;

    List<Class<? extends IFlowModule>> getModuleClassesList();
}