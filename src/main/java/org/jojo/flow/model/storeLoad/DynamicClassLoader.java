package org.jojo.flow.model.storeLoad;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IDynamicClassLoader;

public class DynamicClassLoader extends ClassLoader implements IDynamicClassLoader {
    private static final String CLASS_STR = ".class";
    private static final String CLASS_REGEX_STR = Pattern.quote(CLASS_STR);
    
    private URLClassLoader usedLoader;

    private final Map<String, Class<?>> loaded;

    public DynamicClassLoader() {
        this(new URLClassLoader(new URL[] {}, DynamicClassLoader.class.getClassLoader()));
    }

    public DynamicClassLoader(final URLClassLoader usedLoader) {
        super(Objects.requireNonNull(usedLoader));
        this.usedLoader = usedLoader;
        this.loaded = new HashMap<>();
    }

    @Override
    public List<Class<?>> loadClasses(final File jarFile) throws ClassNotFoundException, IOException {
        final List<String> binaryNames = getClassNames(jarFile);
        final Map<String, Class<?>> ret = new HashMap<>();
        if (!binaryNames.isEmpty()) {
            addToClasspath(jarFile);
            for (final String binaryName : binaryNames) {
                final Class<?> loadedClass = loadClass(binaryName, jarFile);
                ret.put(loadedClass.getName(), loadedClass);
            }
        }
        return new ArrayList<>(ret.values());
    }

    private Class<?> loadClass(final String name, final File jarFile) throws ClassNotFoundException, IOException {
        return getClassLoaderToUse().loadClass(name);
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (this.loaded.containsKey(name)) {
            return this.loaded.get(name);
        }
        final Class<?> alreadyDefined = getAlreadyDefinedClass(name);
        if (alreadyDefined != null) {
            return alreadyDefined;
        }
        final Class<?> ret = getClassLoaderToUse().loadClass(name);
        this.loaded.put(name, ret);
        return ret;
    }
    
    private Class<?> getAlreadyDefinedClass(final String name) {
        Class<?> alreadyDefined = findLoadedClass(name);
        if (alreadyDefined == null) {
            try {
                // lookup if the parent class loader of the used loader has already loaded the class
                alreadyDefined = Class.forName(name, true, getClassLoaderToUse().getParent());
            } catch (ClassNotFoundException e) {
                // class is not already defined, that is ok
            }
        }
        return alreadyDefined;
    }
    
    @Override
    public List<String> getClassNames(final File jarFile) throws IOException {
        final List<String> classNames = new ArrayList<>();
        
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            if (!entry.isDirectory()) {
                final String className = entry.getName().replaceAll(Pattern.quote(File.separator), ".");
                // if the entry is a class file, add the class name
                if (className.endsWith(CLASS_STR)) {
                    classNames.add(className.replaceAll(CLASS_REGEX_STR, ""));
                }
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        return classNames;
    }

    private void addToClasspath(final File file) {
        try {
            URL url = file.toURI().toURL();

            URLClassLoader classLoader = getClassLoaderToUse();
            final List<URL> urls = new ArrayList<>(Arrays.asList(classLoader.getURLs()));
            urls.add(url);
            if (urls.stream().anyMatch(u -> u == null)) {
                new Warning(null, "an url is null", true).reportWarning();
                return;
            }
            this.usedLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), classLoader.getParent());
        } catch (SecurityException | IOException e) {
            new Warning(null, "could not add file \"" + file + "\" to class path, exception= " + e.toString(), true)
                    .reportWarning();
        }
    }

    private URLClassLoader getClassLoaderToUse() {
        return this.usedLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this;
    }
}
