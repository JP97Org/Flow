package org.jojo.flow.model.flowChart.modules;

public abstract class ModulePin {
    private final ModulePinImp imp;
    
    public ModulePin(final ModulePinImp imp) {
        this.imp = imp;
    }
    
    //TODO
    
    public ModulePinImp getModulePinImp() {
        return this.imp;
    }
}
