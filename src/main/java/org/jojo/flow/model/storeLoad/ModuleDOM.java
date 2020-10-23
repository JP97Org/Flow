package org.jojo.flow.model.storeLoad;

import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.api.IExternalConfig;
import org.jojo.flow.model.api.IInternalConfig;
import org.jojo.flow.model.api.IModulePin;
import org.w3c.dom.Element;

public class ModuleDOM extends FlowChartElementDOM {
    public static final String NAME_CLASSNAME = "ClassName";
    public static final String NAME = "Module";
    public static final String NAME_NAME = "name";
    public static final String NAME_PINS = "Pins";
    
    public ModuleDOM() {
        super(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements().createElement(NAME));
    }

    public void setClassName(String className) {
        appendString(NAME_CLASSNAME, className);
    }
    
    public void setName(final String name) {
        ((Element)getParentNode()).setAttribute(NAME_NAME, Objects.requireNonNull(name));
    }
    
    public void setInternalConfig(final IInternalConfig config) {
        appendCustomDOM(config);
    }
    
    public void setExternalConfig(final IExternalConfig iExternalConfig) {
        appendCustomDOM(iExternalConfig);
    }
    
    public void setPins(final List<IModulePin> list) {
        appendList(NAME_PINS, list);
    }
}
