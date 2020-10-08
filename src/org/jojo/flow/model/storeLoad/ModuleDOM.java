package org.jojo.flow.model.storeLoad;

import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.ExternalConfig;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.ModulePin;
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
    
    public void setInternalConfig(final InternalConfig config) {
        appendCustomDOM(config);
    }
    
    public void setExternalConfig(final ExternalConfig config) {
        appendCustomDOM(config);
    }
    
    public void setGraphicalRepresentation(final GraphicalRepresentation gr) {
        appendCustomDOM(gr);
    }
    
    public void setPins(final List<ModulePin> pins) {
        appendList(NAME_PINS, pins);
    }
}
