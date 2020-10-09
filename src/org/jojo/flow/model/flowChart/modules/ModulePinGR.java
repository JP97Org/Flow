package org.jojo.flow.model.flowChart.modules;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;
import org.jojo.flow.model.storeLoad.PointDOM;

public abstract class ModulePinGR extends GraphicalRepresentation {
    private final int height;
    private final int width;
    
    private boolean isIconTextAllowed;
    private String iconText;
    
    private Point linePoint;
    private PinOrientation pinOrientation;
    
    public ModulePinGR(final Point position, final String iconText,
            final int height, final int width) {
        super(position);
        this.height = height;
        this.width = width;
        setIconTextAllowed(iconText != null);
        setIconText(iconText);
        setLinePoint(position);
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public boolean isIconTextAllowed() {
        return this.isIconTextAllowed;
    }

    public void setIconTextAllowed(final boolean isIconTextAllowed) {
        this.isIconTextAllowed = isIconTextAllowed;
        notifyObservers(isIconTextAllowed);
    }

    public String getIconText() {
        return this.iconText;
    }

    public void setIconText(final String iconText) {
        this.iconText = iconText;
        notifyObservers(iconText);
    }

    public Point getLinePoint() {
        return this.linePoint;
    }

    public void setLinePoint(final Point linePoint) {
        this.linePoint = Objects.requireNonNull(linePoint);
        notifyObservers(linePoint);
    }

    public PinOrientation getPinOrientation() {
        return this.pinOrientation;
    }

    public void setPinOrientation(PinOrientation pinOrientation) {
        this.pinOrientation = Objects.requireNonNull(pinOrientation);
        notifyObservers(pinOrientation);
    }
    
    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendString("isIconTextAllowed", "" + isIconTextAllowed());
        dom.appendString("iconText", "" + getIconText());
        dom.appendCustomPoint("linePoint", getLinePoint());
        dom.appendString("pinOrientation", getPinOrientation().toString());
        return null;
    }
    
    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            Map<String, Object> domMap = dom.getDOMMap();
            final DOM domIs = (DOM) domMap.get("isIconTextAllowed");
            final String str = domIs.elemGet();
            this.isIconTextAllowed = Boolean.parseBoolean(str);
            final DOM domIct = (DOM) domMap.get("iconText");
            this.iconText = domIct.elemGet();
            final DOM lp = (DOM) domMap.get("linePoint");
            this.linePoint = PointDOM.pointOf(lp);
            final DOM pOr = (DOM) domMap.get("pinOrientation");
            final String pOrName = pOr.elemGet();
            this.pinOrientation = PinOrientation.of(pOrName);
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID);
            Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get("isIconTextAllowed") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM domIs = (DOM) domMap.get("isIconTextAllowed");
            final String str = domIs.elemGet();
            ok(str != null, OK.ERR_MSG_NULL);
            ok(s -> Boolean.parseBoolean(s), str);
            ok(domMap.get("iconText") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM domIct = (DOM) domMap.get("iconText");
            final String iconText = domIct.elemGet();
            ok(iconText != null, OK.ERR_MSG_NULL);
            ok(domMap.get("linePoint") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM lp = (DOM) domMap.get("linePoint");
            ok(p -> PointDOM.pointOf(p), lp);
            ok(domMap.get("pinOrientation") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM pOr = (DOM) domMap.get("pinOrientation");
            final String pOrName = pOr.elemGet();
            ok(pOrName != null, OK.ERR_MSG_NULL);
            ok(PinOrientation.of(pOrName) != null, OK.ERR_MSG_NULL);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement((new ModelFacade()).getFlowChart()).reportWarning();
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), this.linePoint);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (getClass().equals(other.getClass())) {
            return this.linePoint.equals(((ModulePinGR)other).linePoint);
        }
        return false;
    }
}
