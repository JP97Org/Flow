package org.jojo.flow.model.storeLoad;

import java.awt.Point;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PointDOM extends DOM {
    public static final String NAME = "Point";
    public static final String NAME_NAME = "name";
    public static final String NAME_X = "x";
    public static final String NAME_Y = "y";
    
    private PointDOM(Document document, Element parent) {
        super(document, parent);
    }
    
    public static PointDOM of(final String name, final Point point) {
        Objects.requireNonNull(point);
        final Document doc = getDocumentForCreatingElements();
        final Element parent = doc.createElement(NAME);
        parent.setAttribute(NAME_NAME, Objects.requireNonNull(name));
        final PointDOM ret = new PointDOM(doc, parent);
        ret.appendInt(NAME_X, point.x);
        ret.appendInt(NAME_Y, point.y);
        return ret;
    }
}
