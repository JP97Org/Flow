package org.jojo.flow.model.storeLoad;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jojo.flow.model.Warning;

public class DynamicClassLoader extends ClassLoader {
    private final Map<String, File> externalClassesMap;
    private final File tmpDirForJarExtraction;
    
    public DynamicClassLoader(final File tmpDirForJarExtraction) {
        this(DynamicClassLoader.class.getClassLoader(), tmpDirForJarExtraction);
    }
    
    public DynamicClassLoader(final ClassLoader parent, final File tmpDirForJarExtraction) {
        super(Objects.requireNonNull(parent));
        this.externalClassesMap = new HashMap<>();
        this.tmpDirForJarExtraction = tmpDirForJarExtraction;
    }
    
    protected void putExternalClass(final String name, final File file) {
        this.externalClassesMap.put(name, file);
    }
    
    protected Map<String, File> getExternalClassesMap() {
        return new HashMap<>(this.externalClassesMap);
    }
    
    public List<Class<?>> loadClasses(final File jarFile) throws ClassNotFoundException, IOException {
        final List<File> unpacked = unpack(jarFile);
        if (unpacked.size() == 0) {
            return new ArrayList<>();
        }
        final String binaryName = binaryNameOf(unpacked.get(0).getAbsolutePath());
        putExternalClass(binaryName, jarFile);
        return loadClasses(binaryName, jarFile);
    }
    
    private List<Class<?>> loadClasses(final String name, final File file) throws ClassNotFoundException, IOException {
        if (!this.externalClassesMap.containsKey(name)) {
            return Arrays.asList(super.loadClass(name));
        } else {
            final List<Class<?>> ret = new ArrayList<>();
            final List<File> filesToLoad = new ArrayList<>();
            if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
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
        final String path = pathNameOf(name); 
        if (!this.externalClassesMap.containsKey(name)) {
            return super.loadClass(name);
        } else {
            try {
                final FileInputStream fis = new FileInputStream(new File(path)); 
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int data = fis.read();

                while (data != -1){
                    buffer.write(data);
                    data = fis.read();
                }
                fis.close();
                final byte[] classData = buffer.toByteArray();
            
                return defineClass(name, classData, 0, classData.length);
            } catch (IOException e) {
                new Warning(null, e.toString(), true).reportWarning();
                return null;
            }
        }
    }
    
    private String pathNameOf(final String name) {
        return this.tmpDirForJarExtraction.getAbsolutePath() + File.separator + name.replaceAll("\\.", File.separator) + ".class";
    }
    
    private String binaryNameOf(final String absPathName) {
        final String pathName = absPathName.replaceFirst(Pattern.quote(this.tmpDirForJarExtraction.getAbsolutePath() + File.separator), "");
        return pathName.replaceAll(Pattern.quote(File.separator), ".").replaceAll("\\.class", "");
    }
    
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    private List<File> unpack(final File jarFile) throws IOException {
        final List<File> files = new ArrayList<>();
        
        if (!this.tmpDirForJarExtraction.exists()) {
            this.tmpDirForJarExtraction.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            final String filePath = this.tmpDirForJarExtraction + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                File dir = new File(getDirOf(filePath));
                dir.mkdirs();
                extractFile(zipIn, filePath);
                if (filePath.endsWith(".class")) {
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
        
        return files;
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
}
