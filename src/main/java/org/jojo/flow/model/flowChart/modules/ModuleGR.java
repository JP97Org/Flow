package org.jojo.flow.model.flowChart.modules;

import static org.jojo.flow.model.util.OK.ok;

import java.awt.Point;
import java.awt.Window;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.Icon;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.DOMStringUnion;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IModuleGR;
import org.jojo.flow.model.flowChart.FlowChartElementGR;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;
import org.jojo.flow.model.storeLoad.PointDOM;
import org.jojo.flow.model.util.OK;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;

public abstract class ModuleGR extends FlowChartElementGR implements IModuleGR {
    private FlowModule module;
    
    private double scale;
    private boolean isIconTextAllowed;
    private String iconText;
    private int height;
    private int width;
    
    private final Point[] corners = new Point[4]; // {<^,^>,v>,<v} , i.e. clockwise, starting upper-left
    
    public ModuleGR(final Point position, final int height,
            final int width, final String iconText) {
        super(position);
        this.scale = 1;
        this.isIconTextAllowed = iconText != null;
        this.iconText = iconText;
        this.height = height;
        this.width = width;

        setCorners();
    }
    
    protected void setModule(final FlowModule module) {
        this.module = module;
    }
    
    @Override
    public void setModuleMock(final MockModule mock) {
        this.module = mock;
    }
    
    private void setCorners() {
        this.corners[0] = getPosition();
        this.corners[1] = new Point(this.corners[0].x + width, this.corners[0].y);
        this.corners[2] = new Point(this.corners[1].x, this.corners[1].y + height);
        this.corners[3] = new Point(this.corners[2].x - width, this.corners[2].y);
    }

    @Override
    public final int getHeight() {
        return this.height;
    }

    @Override
    public final int getWidth() {
        return this.width;
    }
    
    @Override
    public final Point[] getCorners() {
        return Arrays.stream(this.corners).toArray(Point[]::new);
    }
    
    @Override
    public final Icon getDefaultIcon() {
        return super.getDefaultIcon();
    }
    
    @Override
    public final void setDefaultIcon(final Icon defaultIcon) {
        super.setDefaultIcon(defaultIcon);
    }
    
    @Override
    public final Icon getSelectedIcon() {
        return super.getSelectedIcon();
    }
    
    @Override
    public final void setSelectedIcon(final Icon selectedIcon) {
        super.setSelectedIcon(selectedIcon);
    }
    
    @Override
    public abstract String getInfoText();
    @Override
    public abstract Window getInternalConfigWindow();
    
    @Override
    public final int getPriority() {
        return this.module.getExternalConfig().getPriority();
    }
    
    @Override
    public final double getScale() {
        return this.scale;
    }
    
    @Override
    public final void setScale(final double scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("scale must be > 0");
        }
        
        this.scale = scale;
        notifyObservers(scale);
    }
    
    @Override
    public final void rotateLeft() {
        rotate(1);
    }
    
    private void rotate(int add) {
        setPosition(this.corners[add]);
        final int oldHeight = this.height;
        this.height = this.width;
        this.width = oldHeight;
        cornerRotate(add);
        notifyObservers();
    }
    
    private void cornerRotate(int add) {
        final Point[] oldCorners = new Point[this.corners.length];
        System.arraycopy(this.corners, 0, oldCorners, 0, this.corners.length);
        for (int i = 0; i < this.corners.length; i++) {
            this.corners[i] = oldCorners[(i + add) % this.corners.length]; 
        }
    }
    
    @Override
    public final void rotateRight() {
        rotate(3);
    }
    
    @Override
    public final boolean isIconTextAllowed() {
        return this.isIconTextAllowed;
    }
    
    @Override
    public final void setIsIconTextAllowed(final boolean isIconTextAllowed) {
        this.isIconTextAllowed = isIconTextAllowed;
        notifyObservers(isIconTextAllowed);
    }
    
    @Override
    public final String getIconText() {
        return this.iconText;
    }
    
    @Override
    public final void setIconText(final String iconText) {
        if (iconText == null && isIconTextAllowed()) {
            throw new IllegalArgumentException("icon text must not be null if icon text is allowed");
        }
        
        this.iconText = iconText;
        notifyObservers(iconText);
    }

    @Override
    public final FlowModule getModule() {
        return this.module;
    }
    
    @Override
    public IDOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendInt(ModulePinDOM.NAME_MODULE_ID, getModule().getId());
        dom.appendString("scale", "" + getScale());
        dom.appendString("isIconTextAllowed", "" + isIconTextAllowed());
        dom.appendString("iconText", "" + getIconText());
        dom.appendList("corners", Arrays
                .stream(getCorners())
                .map(c -> PointDOM.of("corner", c))
                .collect(Collectors.toList()));
        return dom;
    }
    
    @Override
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            final IDOM modIdDom = (IDOM)domMap.get(ModulePinDOM.NAME_MODULE_ID).getValue();
            final String idStr = modIdDom.elemGet();
            final int id = Integer.parseInt(idStr);
            this.module = (FlowModule) new ModelFacade().getElementById(id);
            final IDOM scaleDom = (IDOM)domMap.get("scale").getValue();
            final String scaleStr = scaleDom.elemGet();
            this.scale = Double.parseDouble(scaleStr);
            final IDOM isIconTextAllowedDom = (IDOM)domMap.get("isIconTextAllowed").getValue();
            final String isIconTextAllowedStr = isIconTextAllowedDom.elemGet();
            this.isIconTextAllowed = Boolean.parseBoolean(isIconTextAllowedStr);
            if (this.isIconTextAllowed) {
                final IDOM iconTextDom = (IDOM)domMap.get("iconText").getValue();
                this.iconText = iconTextDom.elemGet() == null ? "" : iconTextDom.elemGet();
            }
            final IDOM hDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT).getValue();
            final String hStr = hDom.elemGet();
            this.height = Integer.parseInt(hStr);
            final IDOM wDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME_WIDTH).getValue();
            final String wStr = wDom.elemGet();
            this.width = Integer.parseInt(wStr);
            final IDOM cornersDom = (IDOM)domMap.get("corners").getValue();
            final Map<String, DOMStringUnion> cornersMap = cornersDom.getDOMMap();
            int i = 0;
            for (final var cornerObj : cornersMap.values()) {
                if (cornerObj.isDOM()) {
                    final IDOM cornerDom = (IDOM)cornerObj.getValue();
                    this.corners[i] = PointDOM.pointOf(cornerDom);
                    i++;
                }
            }
            assert i == 4;
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "FCE_GR " + OK.ERR_MSG_DOM_NOT_VALID, getModule());
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            ok(domMap.get(ModulePinDOM.NAME_MODULE_ID).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM modIdDom = (IDOM)domMap.get(ModulePinDOM.NAME_MODULE_ID).getValue();
            final String idStr = modIdDom.elemGet();
            ok(idStr != null, OK.ERR_MSG_NULL);
            final int id = ok(s -> Integer.parseInt(s), idStr);
            ok(i -> (FlowModule) new ModelFacade().getElementById(i), id);
            ok(domMap.get("scale").isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM scaleDom = (IDOM)domMap.get("scale").getValue();
            final String scaleStr = scaleDom.elemGet();
            ok(scaleStr != null, OK.ERR_MSG_NULL);
            ok(s -> Double.parseDouble(s), scaleStr);
            ok(domMap.get("isIconTextAllowed").isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM isIconTextAllowedDom = (IDOM)domMap.get("isIconTextAllowed").getValue();
            final String isIconTextAllowedStr = isIconTextAllowedDom.elemGet();
            ok(isIconTextAllowedStr != null, OK.ERR_MSG_NULL);
            final boolean isIconTextAllowed = ok(s -> Boolean.parseBoolean(s), isIconTextAllowedStr);
            if (isIconTextAllowed) {
                ok(domMap.get("iconText").isDOM(), OK.ERR_MSG_WRONG_CAST);
            }
            ok(domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM hDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT).getValue();
            final String hStr = hDom.elemGet();
            ok(hStr != null, OK.ERR_MSG_NULL);
            ok(s -> Integer.parseInt(s), hStr);
            ok(domMap.get(GraphicalRepresentationDOM.NAME_WIDTH).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM wDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME_WIDTH).getValue();
            final String wStr = wDom.elemGet();
            ok(wStr != null, OK.ERR_MSG_NULL);
            ok(s -> Integer.parseInt(s), wStr);
            ok(domMap.get("corners").isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM cornersDom = (IDOM)domMap.get("corners").getValue();
            final Map<String, DOMStringUnion> cornersMap = cornersDom.getDOMMap();
            int i = 0;
            for (final var cornerObj : cornersMap.values()) {
                if (cornerObj.isDOM()) {
                    final IDOM cornerDom = (IDOM)cornerObj.getValue();
                    ok(d -> PointDOM.pointOf(d), cornerDom);
                    i++;
                }
            }
            ok(i == 4, "Not 4 corners, corners count: " + i);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(getModule()).reportWarning();
            return false;
        }
    }
}
