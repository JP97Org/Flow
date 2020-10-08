package org.jojo.flow.model.storeLoad;

import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.ExternalConfig;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ModuleDOM extends DOM {
    public static final String NAME = "Module";
    public static final String NAME_NAME = "name";
    public static final String NAME_INDEX = "Index";
    public static final String NAME_INT_CONFIG = "InternalConfig";
    public static final String NAME_EXT_CONFIG = "ExternalConfig";
    public static final String NAME_GR = "GR";
    public static final String NAME_PINS = "Pins";
    
    public ModuleDOM(final Document document, final Element parent) {
        super(document, parent);
    }
    
    public void setName(final String name) {
        ((Element)getParentNode()).setAttribute(NAME_NAME, Objects.requireNonNull(name));
    }

    public void setIndex(final int index) {
        final var elem = getDocument().createElement(NAME_INDEX);
        elem.appendChild(getDocument().createTextNode("" + index));
        append(elem);
    }
    
    public void setInternalConfig(final InternalConfig config) {
        appendCustomDOM(NAME_INT_CONFIG, config);
    }
    
    public void setExternalConfig(final ExternalConfig config) {
        appendCustomDOM(NAME_EXT_CONFIG, config);
    }
    
    public void setGraphicalRepresentation(final GraphicalRepresentation gr) {
        appendCustomDOM(NAME_GR, gr);
    }
    
    public void setPins(final List<ModulePin> pins) {
        final var elem = getDocument().createElement(NAME_PINS);
        pins.forEach(p -> elem.appendChild(p.getDOM().getParentNode()));
        append(elem);
    }
}
