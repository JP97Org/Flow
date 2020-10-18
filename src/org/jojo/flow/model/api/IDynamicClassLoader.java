package org.jojo.flow.model.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IDynamicClassLoader extends IAPI {

    List<Class<?>> loadClasses(File jarFile) throws ClassNotFoundException, IOException;

    Class<?> loadClass(String name) throws ClassNotFoundException;
}