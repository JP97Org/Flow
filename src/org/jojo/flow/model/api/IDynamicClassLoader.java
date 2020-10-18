package org.jojo.flow.model.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IDynamicClassLoader extends IAPI {
    public static IDynamicClassLoader getDefaultImplementation(final File tmpDirForJarExtraction) {
        return (IDynamicClassLoader) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {File.class}, tmpDirForJarExtraction);
    }
    
    public static IDynamicClassLoader getDefaultImplementation(final ClassLoader parent, final File tmpDirForJarExtraction) {
        return (IDynamicClassLoader) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {ClassLoader.class, File.class}, parent, tmpDirForJarExtraction);
    }

    List<Class<?>> loadClasses(File jarFile) throws ClassNotFoundException, IOException;

    Class<?> loadClass(String name) throws ClassNotFoundException;
    
    ClassLoader getClassLoader();
}