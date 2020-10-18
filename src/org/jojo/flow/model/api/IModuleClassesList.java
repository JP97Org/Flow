package org.jojo.flow.model.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IModuleClassesList extends IAPI {
    public static IModuleClassesList getDefaultImplementation(final IDynamicClassLoader loader, final File... jarFiles) {
        return (IModuleClassesList) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IDynamicClassLoader.class, File[].class}, loader, jarFiles);
    }
    
    public static IModuleClassesList getDefaultImplementation(final IDynamicClassLoader loader, 
            final boolean isLoadingAll, final File... jarFiles) {
        return (IModuleClassesList) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IDynamicClassLoader.class, boolean.class, File[].class}, loader, isLoadingAll, jarFiles);
    }

    IDynamicClassLoader getClassLoader();

    IModuleClassesList addJarFile(File jarFile) throws ClassNotFoundException, IOException;

    IModuleClassesList loadJarFile(File jarFile) throws ClassNotFoundException, IOException;

    IModuleClassesList loadAll() throws ClassNotFoundException, IOException;

    List<Class<? extends IFlowModule>> getModuleClassesList();
}