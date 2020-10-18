package org.jojo.flow.model.flowChart;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.api.IConnectionGR;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowChartGR;
import org.jojo.flow.model.api.IModuleGR;
import org.jojo.flow.model.flowChart.connections.ConnectionGR;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.storeLoad.ConnectionDOM;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;
import org.jojo.flow.model.storeLoad.FlowChartDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModuleDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.PointDOM;

import static org.jojo.flow.model.storeLoad.OK.ok;

public class FlowChartGR extends FlowChartElementGR implements IFlowChartGR {
    private final List<IModuleGR> modules;
    private final List<IConnectionGR> connections;
    private Point absOriginPoint;
    private boolean isRasterEnabled;
    
    private IFlowChart fc;
    
    public FlowChartGR() {
        super(new Point(0, 0));
        this.modules = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.absOriginPoint = new Point(0,0);
        this.isRasterEnabled = true;
    }
    
    public void setFlowChart(final IFlowChart fc) {
        this.fc = fc;
    }
    
    @Override
    public IFlowChart getFlowChart() {
        return this.fc;
    }
    
    @Override
    public void addModule(final IModuleGR moduleGR) {
        this.modules.add(Objects.requireNonNull(moduleGR));
        notifyObservers(moduleGR);
    }
    
    @Override
    public void addConnection(final IConnectionGR connectionGR) {
        this.connections.add(connectionGR);
    }
    
    @Override
    public boolean removeModule(final IModuleGR moduleGR) {
        final boolean ret = this.modules.remove(Objects.requireNonNull(moduleGR));
        if (ret) {
            notifyObservers(moduleGR);
        }
        return ret;
    }
    
    @Override
    public boolean removeModule(final int index) {
        if (index >= this.modules.size()) {
            return false;
        }
        final IModuleGR module = this.modules.get(index);
        this.modules.remove(index);
        notifyObservers(module);
        return true;
    }
    
    @Override
    public boolean removeConnection(final IConnectionGR connectionGR) {
        final boolean ret = this.connections.remove(Objects.requireNonNull(connectionGR));
        if (ret) {
            notifyObservers(connectionGR);
        }
        return ret;
    }
    
    @Override
    public boolean removeConnection(final int index) {
        if (index >= this.connections.size()) {
            return false;
        }
        final IConnectionGR con = this.connections.get(index);
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

    @Override
    public Point getAbsOriginPoint() {
        return this.absOriginPoint;
    }

    @Override
    public void setAbsOriginPoint(final Point absOriginPoint) {
        this.absOriginPoint = Objects.requireNonNull(absOriginPoint);
        notifyObservers(absOriginPoint);
    }

    @Override
    public boolean isRasterEnabled() {
        return this.isRasterEnabled;
    }

    @Override
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
                if (conObj instanceof DOM) {
                    final DOM connnectionDom = (DOM) conObj;
                    final DOM cnDom = (DOM) (connnectionDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME));
                    final String conToLoad = cnDom.elemGet();
                    final GraphicalRepresentation connection = DynamicObjectLoader.loadGR(conToLoad);
                    connection.restoreFromDOM(connnectionDom);
                    this.connections.add((ConnectionGR) connection);
                }
            }
            final DOM modulesDom = (DOM)domMap.get(FlowChartDOM.NAME_MODULES);
            final Map<String, Object> modulesMap = modulesDom.getDOMMap();
            for (final var modObj : modulesMap.values()) {
                if (modObj instanceof DOM) {
                    final DOM modDom = (DOM) modObj;
                    final DOM cnDom = (DOM) (modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME));
                    final String moduleToLoad = cnDom.elemGet();
                    final GraphicalRepresentation module = DynamicObjectLoader.loadGR(moduleToLoad);
                    module.restoreFromDOM(modDom);
                    this.modules.add((ModuleGR) module);
                }
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
                if (conObj instanceof DOM) {
                    final DOM connectionDom = (DOM) conObj;
                    ok(connectionDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                    final DOM cnDom = (DOM) (connectionDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME));
                    final String conToLoad = cnDom.elemGet();
                    ok(conToLoad != null, OK.ERR_MSG_NULL);
                    final ConnectionGR connection = ok(c -> (ConnectionGR)DynamicObjectLoader.loadGR(c), conToLoad);
                    ok(connection.isDOMValid(connectionDom), "ConnectionGR " + OK.ERR_MSG_DOM_NOT_VALID);
                }
            }
            ok(domMap.get(FlowChartDOM.NAME_MODULES) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM modulesDom = (DOM)domMap.get(FlowChartDOM.NAME_MODULES);
            final Map<String, Object> modulesMap = modulesDom.getDOMMap();
            for (final var modObj : modulesMap.values()) {
                if (modObj instanceof DOM) {
                    final DOM modDom = (DOM) modObj;
                    ok(modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                    final DOM cnDom = (DOM) (modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME));
                    final String moduleToLoad = cnDom.elemGet();
                    ok(moduleToLoad != null, OK.ERR_MSG_NULL);
                    final ModuleGR module = ok(m -> (ModuleGR)DynamicObjectLoader.loadGR(m), moduleToLoad);
                    ok(module.isDOMValid(modDom), "ModuleGR " + OK.ERR_MSG_DOM_NOT_VALID);
                }
            }
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(getFlowChart()).reportWarning();
            return false;
        }
    }
}
