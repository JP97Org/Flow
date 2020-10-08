package org.jojo.flow.model.storeLoad;

import java.awt.Point;
import java.util.Objects;

public class GraphicalRepresentationDOM extends DOM {
    public static final String NAME = "GR";
    public static final String NAME_CLASSNAME = "ClassName";
    public static final String NAME_POSITION = "position";
    public static final String NAME_HEIGHT = "Height";
    public static final String NAME_WIDTH = "Width";
    
    public GraphicalRepresentationDOM() {
        super(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements().createElement(NAME));
    }

    public void setClassName(final String className) {
        appendString(NAME_CLASSNAME, className);
    }
    
    public void setPosition(final Point point) {
        appendCustomPoint(NAME_POSITION, Objects.requireNonNull(point));
    }
    
    public void setHeight(final int height) {
        appendInt(NAME_HEIGHT, height);
    }
    
    public void setWidth(final int width) {
        appendInt(NAME_WIDTH, width);
    }
    
    public void appendCustomPoint(final String name, final Point point) {
        appendCustomDOM(PointDOM.of(Objects.requireNonNull(name), Objects.requireNonNull(point)));
    }
}
