package org.jojo.flow.model.storeLoad;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
    private static final String META_INF_STR = "META-INF";
    private static final String MANIFEST_STR = "MANIFEST.MF";
    private static final String CLASS_PATH_STR = "Class-Path:";
    private static final String CLASS_STR = ".class";
    private static final String CLASS_REGEX_STR = Pattern.quote(CLASS_STR);
    private static final String ZIP_STR = ".zip";
    private static final String JAR_STR = ".jar";
    
    private URLClassLoader actualParent;

    private final Map<String, File> externalClassesMap;
    private final File tmpDirForJarExtraction;

    private final Map<String, Class<?>> loaded;

    public DynamicClassLoader(final File tmpDirForJarExtraction) {
        this(new URLClassLoader(new URL[] {}, DynamicClassLoader.class.getClassLoader()),
                Objects.requireNonNull(tmpDirForJarExtraction));
    }

    public DynamicClassLoader(final URLClassLoader parent, final File tmpDirForJarExtraction) {
        super(Objects.requireNonNull(parent));
        this.actualParent = parent;
        this.externalClassesMap = new HashMap<>();
        this.tmpDirForJarExtraction = Objects.requireNonNull(tmpDirForJarExtraction);
        this.loaded = new HashMap<>();
    }

    @Override
    public void putExternalClass(final String name, final File file) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(file);
        this.externalClassesMap.put(name, file);
    }

    @Override
    public Map<String, File> getExternalClassesMap() {
        return new HashMap<>(this.externalClassesMap);
    }

    @Override
    public List<Class<?>> loadClasses(final File jarFile) throws ClassNotFoundException, IOException {
        final List<String> binaryNames = getClassNames(jarFile);
        if (!binaryNames.isEmpty()) {
            final String binaryName = binaryNames.get(0);
            putExternalClass(binaryName, jarFile);
            return loadClasses(binaryName, jarFile);
        }
        return new ArrayList<>();
    }

    private List<Class<?>> loadClasses(final String name, final File file) throws ClassNotFoundException, IOException {
        if (!this.externalClassesMap.containsKey(name)) {
            return Arrays.asList(getActualParentClassLoader().loadClass(name));
        } else {
            final List<Class<?>> ret = new ArrayList<>();
            final List<File> filesToLoad = new ArrayList<>();
            if (file.getName().endsWith(JAR_STR)) {
                addToClasspath(file);
                final List<String> classNames = getClassNames(file);
                for (final var cn : classNames) {
                    ret.add(loadClass(cn));
                }
            } else if (file.getName().endsWith(ZIP_STR)) {
                filesToLoad.addAll(unpack(file));
            } else {
                filesToLoad.add(file);
            }
            for (final var fileToLoad : filesToLoad) {
                putExternalClass(binaryNameOf(fileToLoad.getAbsolutePath()), file);
                ret.add(loadClass(binaryNameOf(fileToLoad.getAbsolutePath())));
            }
            return ret;
        }
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (this.loaded.containsKey(name)) {
            return this.loaded.get(name);
        }
        if (!this.externalClassesMap.containsKey(name)) {
            return getActualParentClassLoader().loadClass(name);
        }
        final Class<?> alreadyDefined = getAlreadyDefinedClass(name);
        if (alreadyDefined != null) {
            return alreadyDefined;
        }

        final String path = pathNameOf(name);
        try {
            final FileInputStream fis = new FileInputStream(new File(path));
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = fis.read();

            while (data != -1) {
                buffer.write(data);
                data = fis.read();
            }
            fis.close();
            final byte[] classData = buffer.toByteArray();

            final Class<?> ret = defineClass(name, classData, 0, classData.length);
            this.loaded.put(name, ret);
            return ret;
        } catch (IOException e) {
            new Warning(null, e.toString(), true).reportWarning();
            return null;
        }
    }
    
    private Class<?> getAlreadyDefinedClass(final String name) {
        Class<?> alreadyDefined = findLoadedClass(name);
        if (alreadyDefined == null) {
            try {
                // lookup if the default class loader has already loaded the class
                alreadyDefined = Class.forName(name);
            } catch (ClassNotFoundException e) {
                // class is not already defined, that is ok
            }
        }
        return alreadyDefined;
    }

    private String pathNameOf(final String name) {
        return this.tmpDirForJarExtraction.getAbsolutePath() + File.separator + name.replaceAll("\\.", File.separator)
                + CLASS_STR;
    }

    @Override
    public String binaryNameOf(final String absPathName) {
        final String pathName = absPathName
                .replaceFirst(Pattern.quote(this.tmpDirForJarExtraction.getAbsolutePath() + File.separator), "");
        return pathName.replaceAll(Pattern.quote(File.separator), ".").replaceAll(CLASS_REGEX_STR, "");
    }

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    @Override
    public List<File> unpack(final File jarFile) throws IOException {
        final List<File> files = new ArrayList<>();

        if (!this.tmpDirForJarExtraction.exists()) {
            this.tmpDirForJarExtraction.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            final String filePath = this.tmpDirForJarExtraction.getAbsolutePath() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                File dir = new File(getDirOf(filePath));
                dir.mkdirs();
                extractFile(zipIn, filePath);
                if (filePath.endsWith(CLASS_STR)) {
                    files.add(new File(filePath));
                }
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        
        addDependencies(jarFile);
        
        return files;
    }
    
    private void unpackManifest(final File jarFile) throws IOException {
        if (!this.tmpDirForJarExtraction.exists()) {
            this.tmpDirForJarExtraction.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            final String filePath = this.tmpDirForJarExtraction.getAbsolutePath() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                if (filePath.endsWith(MANIFEST_STR)) {
                    File dir = new File(getDirOf(filePath));
                    dir.mkdirs();
                    extractFile(zipIn, filePath);
                }
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    
    // add dependencies of the given jar file to class path
    private void addDependencies(final File jarFile) throws IOException {
        unpackManifest(jarFile);
        final File manifestFile = new File(this.tmpDirForJarExtraction.getAbsolutePath() + File.separator 
                + META_INF_STR
                + File.separator + MANIFEST_STR);
        final BufferedReader reader = new BufferedReader(new FileReader(manifestFile));
        final List<String> lines = new ArrayList<>();
        String s = reader.readLine();
        final String start = CLASS_PATH_STR;
        boolean started = false;
        final StringBuilder builder = new StringBuilder();
        while (s != null) {
            if (s.startsWith(start)) {
                started = true;
            }
            if (started) {
                if (s.startsWith(start) || s.startsWith(" ")) {
                    builder.append(s.trim());
                } else {
                    lines.add(builder.toString());
                    lines.add(s);
                    started = false;
                }
            } else {
                lines.add(s);
            }
            s = reader.readLine();
        }
        reader.close();

        final String classPathLine = lines.stream().filter(k -> k.startsWith(start)).findFirst().orElse(start)
                .substring(start.length()).trim();
        if (!classPathLine.isEmpty()) {
            final String[] dependencies = classPathLine.split("\\s");
            Arrays.stream(dependencies)
                    .forEach(d -> addToClasspath(new File(getDirOf(jarFile.getAbsolutePath()) + File.separator + d)));
        }
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

            URLClassLoader classLoader = getActualParentClassLoader();
            final List<URL> urls = new ArrayList<>(Arrays.asList(classLoader.getURLs()));
            urls.add(url);
            if (urls.stream().anyMatch(u -> u == null)) {
                new Warning(null, "an url is null", true).reportWarning();
                return;
            }
            this.actualParent = new URLClassLoader(urls.toArray(new URL[urls.size()]), classLoader.getParent());
        } catch (SecurityException | IOException e) {
            new Warning(null, "could not add file \"" + file + "\" to class path, exception= " + e.toString(), true)
                    .reportWarning();
        }
    }

    private URLClassLoader getActualParentClassLoader() {
        return this.actualParent;
    }

    private String getDirOf(final String filePath) {
        final File file = new File(filePath);
        if (file.isDirectory()) {
            return filePath;
        }
        return file.getParent();
    }

    /**
     * Extracts a zip entry (file entry).
     * 
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(final ZipInputStream zipIn, final String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    @Override
    public ClassLoader getClassLoader() {
        return this;
    }
}
