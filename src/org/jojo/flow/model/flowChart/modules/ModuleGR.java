package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;
import java.awt.Window;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.Icon;

import org.jojo.flow.model.flowChart.FlowChartElementGR;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;
import org.jojo.flow.model.storeLoad.PointDOM;

public abstract class ModuleGR extends FlowChartElementGR {
    private final FlowModule module;
    
    private double scale;
    private boolean isIconTextAllowed;
    private String iconText;
    private boolean hasInternalConfig;
    private int height;
    private int width;
    
    private final Point[] corners = new Point[4]; // {<^,^>,v>,<v} , i.e. clockwise, starting upper-left
    
    public ModuleGR(final Point position, final FlowModule module, final int height,
            final int width, final String iconText) {
        super(position);
        this.module = module;
        this.scale = 1;
        this.isIconTextAllowed = iconText != null;
        this.iconText = iconText;
        this.hasInternalConfig = module.getInternalConfig() != null;
        this.height = height;
        this.width = width;

        setCorners();
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
    
    public abstract String getInfoText();
    public abstract Window getInternalConfigWindow();
    
    public final int getPriority() {
        return this.module.getExternalConfig().getPriority();
    }
    
    public final double getScale() {
        return this.scale;
    }
    
    public final void setScale(final double scale) {
        this.scale = scale;
        notifyObservers(scale);
    }
    
    public final void rotateLeft() {
        setPosition(this.corners[1]);
        final int oldHeight = this.height;
        this.height = this.width;
        this.width = oldHeight;
        setCorners();
        notifyObservers();
    }
    
    public final void rotateRight() {
        setPosition(this.corners[3]);
        final int oldHeight = this.height;
        this.height = this.width;
        this.width = oldHeight;
        setCorners();
        notifyObservers();
    }
    
    public final boolean hasInternalConfig() {
        return this.hasInternalConfig;
    }
    
    public final void setHasInternalConfig(final boolean hasInternalConfig) {
        this.hasInternalConfig = hasInternalConfig;
        notifyObservers(hasInternalConfig);
    }
    
    public final boolean isIconTextAllowed() {
        return this.isIconTextAllowed;
    }
    
    public final void setIsIconTextAllowed(final boolean isIconTextAllowed) {
        this.isIconTextAllowed = isIconTextAllowed;
        notifyObservers(isIconTextAllowed);
    }
    
    public final String getIconText() {
        return this.iconText;
    }
    
    public final void setIconText(final String iconText) {
        this.iconText = iconText;
        notifyObservers(iconText);
    }

    public final FlowModule getModule() {
        return this.module;
    }
    
    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = new GraphicalRepresentationDOM();
        dom.setClassName(getClass().getName());
        dom.setPosition(getPosition());
        dom.setHeight(getHeight());
        dom.setWidth(getWidth());
        dom.appendInt(ModulePinDOM.NAME_MODULE_ID, getModule().getId());
        dom.appendString("scale", "" + getScale());
        dom.appendString("isIconTextAllowed", "" + isIconTextAllowed());
        dom.appendString("iconText", "" + getIconText());
        dom.appendString("hasInternalConfig", "" + hasInternalConfig());
        dom.appendList("corners", Arrays
                .stream(getCorners())
                .map(c -> PointDOM.of("corner", c))
                .collect(Collectors.toList()));
        if (getLabel() != null) {
            dom.appendCustomDOM("label", getLabel());
        }
        return dom;
    }
    
    @Override
    public void restoreFromDOM(final DOM dom) {
        //TODO
    }
}
