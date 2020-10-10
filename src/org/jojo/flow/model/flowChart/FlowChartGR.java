package org.jojo.flow.model.flowChart;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.model.flowChart.connections.ConnectionGR;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.storeLoad.ConnectionDOM;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;
import org.jojo.flow.model.storeLoad.FlowChartDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModuleDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;
import org.jojo.flow.model.storeLoad.PointDOM;

import static org.jojo.flow.model.storeLoad.OK.ok;

public class FlowChartGR extends FlowChartElementGR {
    private final List<ModuleGR> modules;
    private final List<ConnectionGR> connections;
    private Point absOriginPoint;
    private boolean isRasterEnabled;
    
    private FlowChart fc;
    
    public FlowChartGR() {
        super(new Point(0, 0));
        this.modules = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.absOriginPoint = new Point(0,0);
        this.isRasterEnabled = true;
    }
    
    protected void setFlowChart(final FlowChart fc) {
        this.fc = fc;
    }
    
    public FlowChart getFlowChart() {
        return this.fc;
    }
    
    public void addModule(final ModuleGR moduleGR) {
        this.modules.add(Objects.requireNonNull(moduleGR));
        notifyObservers(moduleGR);
    }
    
    public void addConnection(final ConnectionGR connectionGR) {
        this.connections.add(connectionGR);
    }
    
    public boolean removeModule(final ModuleGR moduleGR) {
        final boolean ret = this.modules.remove(Objects.requireNonNull(moduleGR));
        if (ret) {
            notifyObservers(moduleGR);
        }
        return ret;
    }
    
    public boolean removeModule(final int index) {
        if (index >= this.modules.size()) {
            return false;
        }
        final ModuleGR module = this.modules.get(index);
        this.modules.remove(index);
        notifyObservers(module);
        return true;
    }
    
    public boolean removeConnection(final ConnectionGR connectionGR) {
        final boolean ret = this.connections.remove(Objects.requireNonNull(connectionGR));
        if (ret) {
            notifyObservers(connectionGR);
        }
        return ret;
    }
    
    public boolean removeConnection(final int index) {
        if (index >= this.connections.size()) {
            return false;
        }
        final ConnectionGR con = this.connections.get(index);
        this.connections.remove(index);
        notifyObservers(con);
        return true;
    }
    
    @Override
    public int getHeight() {
        final int modMax = Math.max(this.modules
                            .stream()
                            .map(m -> m.getCorners())
                            .flatMap(p -> Arrays.stream(p))
                            .mapToInt(p -> p.x)
                            .max().orElse(0),
                            this.modules
                            .stream()
                            .map(m -> m.getModule())
                            .map(m -> m.getAllModulePins())
                            .flatMap(p -> p.stream())
                            .map(p -> p.getGraphicalRepresentation())
                            .mapToInt(g -> g.getPosition().y + g.getHeight())
                            .max().orElse(0));
        final int conMax = this.connections
                            .stream()
                            .mapToInt(c -> c.getHeight())
                            .max().orElse(0);
        return Math.max(modMax, conMax);
    }
    
    @Override
    public int getWidth() {
        final int modMax = Math.max(this.modules
                .stream()
                .map(m -> m.getCorners())
                .flatMap(p -> Arrays.stream(p))
                .mapToInt(p -> p.y)
                .max().orElse(0),
                this.modules
                    .stream()
                    .map(m -> m.getModule())
                    .map(m -> m.getAllModulePins())
                    .flatMap(p -> p.stream())
                    .map(p -> p.getGraphicalRepresentation())
                    .mapToInt(g -> g.getPosition().x + g.getWidth())
                    .max().orElse(0));
        final int conMax = this.connections
                .stream()
                .mapToInt(c -> c.getWidth())
                .max().orElse(0);
        return Math.max(modMax, conMax);
    }

    public Point getAbsOriginPoint() {
        return this.absOriginPoint;
    }

    public void setAbsOriginPoint(final Point absOriginPoint) {
        this.absOriginPoint = Objects.requireNonNull(absOriginPoint);
        notifyObservers(absOriginPoint);
    }

    public boolean isRasterEnabled() {
        return this.isRasterEnabled;
    }

    public void setRasterEnabled(boolean isRasterEnabled) {
        this.isRasterEnabled = isRasterEnabled;
        notifyObservers(isRasterEnabled);
    }

    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendCustomPoint("absOriginPoint", this.absOriginPoint);
        dom.appendString("isRasterEnabled", "" + this.isRasterEnabled);
        dom.appendList(FlowChartDOM.NAME_MODULES, this.modules);
        dom.appendList(FlowChartDOM.NAME_CONNECTIONS, this.connections);
        return dom;
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            this.modules.clear();
            this.connections.clear();
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM absDom = (DOM)domMap.get("absOriginPoint");
            this.absOriginPoint = PointDOM.pointOf(absDom);
            final DOM reDom = (DOM)domMap.get("isRasterEnabled");
            final String reDomStr = reDom.elemGet();
            this.isRasterEnabled = Boolean.parseBoolean(reDomStr);
            final DOM connectionsDom = (DOM)domMap.get(FlowChartDOM.NAME_CONNECTIONS);
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                final DOM conDom = (DOM) conObj;
                final DOM cnDom = (DOM) (conDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME));
                final String conToLoad = cnDom.elemGet();
                final GraphicalRepresentation connection = DynamicClassLoader.loadGR(conToLoad);
                connection.restoreFromDOM(conDom);
                this.connections.add((ConnectionGR) connection);
            }
            final DOM modulesDom = (DOM)domMap.get(FlowChartDOM.NAME_MODULES);
            final Map<String, Object> modulesMap = modulesDom.getDOMMap();
            for (final var modObj : modulesMap.values()) {
                final DOM modDom = (DOM) modObj;
                final DOM cnDom = (DOM) (modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME));
                final String moduleToLoad = cnDom.elemGet();
                final GraphicalRepresentation module = DynamicClassLoader.loadGR(moduleToLoad);
                module.restoreFromDOM(modDom);
                this.modules.add((ModuleGR) module);
            }
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "FCE_GR " + OK.ERR_MSG_DOM_NOT_VALID, getFlowChart());
            final Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get("absOriginPoint") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM absDom = (DOM)domMap.get("absOriginPoint");
            ok(d -> PointDOM.pointOf(d), absDom);
            ok(domMap.get("isRasterEnabled") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM reDom = (DOM)domMap.get("isRasterEnabled");
            final String reDomStr = reDom.elemGet();
            ok(reDomStr != null, OK.ERR_MSG_NULL);
            ok(s -> Boolean.parseBoolean(s), reDomStr);
            final DOM connectionsDom = (DOM)domMap.get(FlowChartDOM.NAME_CONNECTIONS);
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                ok(conObj instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM conDom = (DOM) conObj;
                ok(conDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM cnDom = (DOM) (conDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME));
                final String conToLoad = cnDom.elemGet();
                ok(conToLoad != null, OK.ERR_MSG_NULL);
                final ConnectionGR connection = ok(c -> (ConnectionGR)DynamicClassLoader.loadGR(c), conToLoad);
                ok(connection.isDOMValid(conDom), "ConnectionGR " + OK.ERR_MSG_DOM_NOT_VALID);
            }
            ok(domMap.get(FlowChartDOM.NAME_MODULES) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM modulesDom = (DOM)domMap.get(FlowChartDOM.NAME_MODULES);
            final Map<String, Object> modulesMap = modulesDom.getDOMMap();
            for (final var modObj : modulesMap.values()) {
                ok(modObj instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM modDom = (DOM) modObj;
                ok(modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM cnDom = (DOM) (modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME));
                final String moduleToLoad = cnDom.elemGet();
                ok(moduleToLoad != null, OK.ERR_MSG_NULL);
                final ModuleGR module = ok(m -> (ModuleGR)DynamicClassLoader.loadGR(m), moduleToLoad);
                ok(module.isDOMValid(modDom), "ModuleGR " + OK.ERR_MSG_DOM_NOT_VALID);
            }
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(getFlowChart()).reportWarning();
            return false;
        }
    }
}
