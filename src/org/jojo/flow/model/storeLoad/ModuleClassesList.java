package org.jojo.flow.model.storeLoad;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.flow.model.api.IDynamicClassLoader;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IModuleClassesList;
import org.jojo.flow.model.flowChart.modules.FlowModule;

public class ModuleClassesList implements IModuleClassesList {
    private final IDynamicClassLoader loader;
    private final List<File> jarFiles;
    private final List<Class<? extends IFlowModule>> moduleClassesList;
    private final boolean isLoadingAll;
    
    public ModuleClassesList(final IDynamicClassLoader loader, final File... jarFiles) {
        this.loader = Objects.requireNonNull(loader);
        this.jarFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(jarFiles)));
        this.moduleClassesList = new ArrayList<>();
        this.isLoadingAll = false;
    }
    
    public ModuleClassesList(final IDynamicClassLoader loader, final boolean isLoadingAll, final File... jarFiles) throws ClassNotFoundException, IOException {
        this.loader = Objects.requireNonNull(loader);
        this.jarFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(jarFiles)));
        this.moduleClassesList = new ArrayList<>();
        this.isLoadingAll = isLoadingAll;
        if (isLoadingAll) {
            loadAll();
        }
    }
    
    @Override
    public IDynamicClassLoader getClassLoader() {
        return this.loader;
    }
    
    @Override
    public IModuleClassesList addJarFile(final File jarFile) throws ClassNotFoundException, IOException {
        this.jarFiles.add(jarFile);
        if (this.isLoadingAll) {
            load(jarFile);
        }
        return this;
    }
    
    @Override
    public IModuleClassesList loadJarFile(final File jarFile) throws ClassNotFoundException, IOException {
        this.jarFiles.add(jarFile);
        load(jarFile);
        return this;
    }

    @Override
    public IModuleClassesList loadAll() throws ClassNotFoundException, IOException {
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
    
    @Override
    public List<Class<? extends IFlowModule>> getModuleClassesList() {
        return new ArrayList<Class<? extends IFlowModule>>(this.moduleClassesList);
    }
}
