package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;

import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;

public class RigidPinGR extends ModulePinGR {
    public RigidPinGR(final Point position, final String iconText,
                final int height, final int width) {
        super(position, iconText, height, width);
    }

    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = new GraphicalRepresentationDOM();
        dom.setClassName(getClass().getName());
        dom.setPosition(getPosition());
        dom.setHeight(getHeight());
        dom.setWidth(getWidth());
        dom.appendString("isIconTextAllowed", "" + isIconTextAllowed());
        dom.appendString("iconText", "" + getIconText());
        dom.appendCustomPoint("linePoint", getLinePoint());
        dom.appendString("pinOrientation", getPinOrientation().toString());
        return null;
    }
    
    @Override
    public void restoreFromDOM(DOM dom) {
        // TODO Auto-generated method stub
        
    }
}
