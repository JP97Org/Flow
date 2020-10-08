package org.jojo.flow.model.storeLoad;

import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConnectionDOM extends DOM {
    public static final String NAME = "Connection";
    public static final String NAME_INDEX = "Index";
    public static final String NAME_CLASSNAME = "ClassName";
    public static final String NAME_GR = "GR";
    public static final String NAME_FROM_PIN = "PinFrom";
    public static final String NAME_TO_PINS = "PinsTo";
    
    public ConnectionDOM(Document document, Element parent) {
        super(document, parent);
    }
    
    public void setIndex(final int index) {
        final var elem = getDocument().createElement(NAME_INDEX);
        elem.appendChild(getDocument().createTextNode("" + index));
        append(elem);
    }
    
    public void setClassName(final String className) {
        final var elem = getDocument().createElement(NAME_CLASSNAME);
        elem.appendChild(getDocument().createTextNode(Objects.requireNonNull(className)));
        append(elem);
    }
    
    public void setGraphicalRepresentation(final GraphicalRepresentation gr) {
        appendCustomDOM(NAME_GR, gr);
    }
    
    public void setFromPin(final OutputPin fromPin) {
        appendCustomDOM(NAME_FROM_PIN, fromPin);
    }
    
    public void setToPins(final List<InputPin> toPins) {
        final var elem = getDocument().createElement(NAME_TO_PINS);
        toPins.forEach(p -> elem.appendChild(p.getDOM().getParentNode()));
        append(elem);
    }
}
