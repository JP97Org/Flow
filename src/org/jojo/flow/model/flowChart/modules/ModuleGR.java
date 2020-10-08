package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;
import java.awt.Window;
import java.util.Arrays;

import javax.swing.Icon;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElementGR;

public abstract class ModuleGR extends FlowChartElementGR {
    private final FlowModule module;
    
    private double scale;
    private boolean isIconTextAllowed;
    private String iconText;
    private boolean hasInternalConfig;
    private int heigth;
    private int width;
    
    private final Point[] corners = new Point[4]; // {<^,^>,v>,<v} , i.e. clockwise, starting upper-left
    
    public ModuleGR(final Point position, final FlowModule module, final FlowChart flowChart,
            final int heigth, final int width, final String iconText) {
        super(position, flowChart);
        this.module = module;
        this.scale = 1;
        this.isIconTextAllowed = iconText != null;
        this.iconText = iconText;
        this.hasInternalConfig = module.getInternalConfig() != null;
        this.heigth = heigth;
        this.width = width;

        setCorners();
    }
    
    private void setCorners() {
        this.corners[0] = getPosition();
        this.corners[1] = new Point(this.corners[0].x + width, this.corners[0].y);
        this.corners[2] = new Point(this.corners[1].x, this.corners[1].y + heigth);
        this.corners[3] = new Point(this.corners[2].x - width, this.corners[2].y);
    }

    @Override
    public final int getHeigth() {
        return this.heigth;
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
        final int oldHeigth = this.heigth;
        this.heigth = this.width;
        this.width = oldHeigth;
        setCorners();
        notifyObservers();
    }
    
    public final void rotateRight() {
        setPosition(this.corners[3]);
        final int oldHeigth = this.heigth;
        this.heigth = this.width;
        this.width = oldHeigth;
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
}
