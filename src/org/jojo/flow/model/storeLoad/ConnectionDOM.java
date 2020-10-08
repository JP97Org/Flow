package org.jojo.flow.model.storeLoad;

import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.w3c.dom.Element;

public class ConnectionDOM extends FlowChartElementDOM {
    public static final String NAME = "Connection";
    public static final String NAME_NAME = "name";
    public static final String NAME_CLASSNAME = "ClassName";
    public static final String NAME_FROM_PIN = "PinFrom";
    public static final String NAME_TO_PINS = "PinsTo";
    
    public ConnectionDOM() {
        super(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements().createElement(NAME));
    }
    
    public void setName(final String name) {
        ((Element)getParentNode()).setAttribute(NAME_NAME, Objects.requireNonNull(name));
    }
    
    public void setClassName(final String className) {
        appendString(NAME_CLASSNAME, className);
    }
    
    public void setGraphicalRepresentation(final GraphicalRepresentation gr) {
        appendCustomDOM(gr);
    }
    
    public void setFromPin(final OutputPin fromPin) {
        appendCustomDOM(fromPin);
    }
    
    public void setToPins(final List<InputPin> toPins) {
        appendList(NAME_TO_PINS, toPins);
    }
}
