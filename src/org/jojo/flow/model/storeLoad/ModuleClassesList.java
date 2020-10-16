package org.jojo.flow.model.storeLoad;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.flow.model.flowChart.modules.FlowModule;

public class ModuleClassesList {
    private final DynamicClassLoader loader;
    private final List<File> jarFiles;
    private final List<Class<? extends FlowModule>> moduleClassesList;
    private final boolean isLoadingAll;
    
    public ModuleClassesList(final DynamicClassLoader loader, final File... jarFiles) {
        this.loader = Objects.requireNonNull(loader);
        this.jarFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(jarFiles)));
        this.moduleClassesList = new ArrayList<>();
        this.isLoadingAll = false;
    }
    
    public ModuleClassesList(final DynamicClassLoader loader, final boolean isLoadingAll, final File... jarFiles) throws ClassNotFoundException, IOException {
        this.loader = Objects.requireNonNull(loader);
        this.jarFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(jarFiles)));
        this.moduleClassesList = new ArrayList<>();
        this.isLoadingAll = isLoadingAll;
        if (isLoadingAll) {
            loadAll();
        }
    }
    
    public DynamicClassLoader getClassLoader() {
        return this.loader;
    }
    
    public ModuleClassesList addJarFile(final File jarFile) throws ClassNotFoundException, IOException {
        this.jarFiles.add(jarFile);
        if (this.isLoadingAll) {
            load(jarFile);
        }
        return this;
    }
    
    public ModuleClassesList loadJarFile(final File jarFile) throws ClassNotFoundException, IOException {
        this.jarFiles.add(jarFile);
        load(jarFile);
        return this;
    }

    public ModuleClassesList loadAll() throws ClassNotFoundException, IOException {
        for (final File file : this.jarFiles) {
            load(file);
        }
        return this;
    }
    
    @SuppressWarnings("unchecked")
    private void load(final File jarFile) throws ClassNotFoundException, IOException {
        this.moduleClassesList.addAll(this.loader.loadClasses(jarFile).stream()
                .filter(c -> FlowModule.class.isAssignableFrom(c))
                .map(c -> (Class<? extends FlowModule>)c)
                .collect(Collectors.toList()));
    }
    
    public List<Class<? extends FlowModule>> getModuleClassesList() {
        return new ArrayList<Class<? extends FlowModule>>(this.moduleClassesList);
    }
}
