package org.jojo.flow.model.flowChart;

import static org.jojo.flow.model.util.OK.ok;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.DOMStringUnion;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.IFlowChartGR;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IGraphicalRepresentation;
import org.jojo.flow.model.api.IModulePin;
import org.jojo.flow.model.api.Pair;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.ConnectionGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.storeLoad.ConnectionDOM;
import org.jojo.flow.model.storeLoad.FlowChartDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModuleDOM;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.OK;

public class FlowChart extends FlowChartElement implements IFlowChart{
    private final List<IFlowModule> modules;
    private final List<IConnection> connections;
    
    private final IFlowChartGR gr;
    
    public FlowChart(final int id, final IFlowChartGR gr) {
        super(id);
        this.modules = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.gr = gr;
        this.gr.setFlowChart(this);
    }
    
    @Override
    public void addModule(final IFlowModule module) {
        Objects.requireNonNull(module);
        this.modules.add(module);
        this.gr.addModule((ModuleGR) module.getGraphicalRepresentation());
        notifyObservers(module);
    }
    
    @Override
    public boolean addConnection(final IConnection connection) {
        Objects.requireNonNull(connection);
        final boolean ok = this.modules.containsAll(connection.getConnectedModules()) && connection.connect();
        if (ok) {
            this.connections.add(connection);
            this.gr.addConnection((ConnectionGR) connection.getGraphicalRepresentation());
            notifyObservers(connection);
        } else {
            final Warning lastWarning = connection.getLastWarning();
            if (lastWarning != null) {
                new Warning(lastWarning).setAffectedElement(this).reportWarning();
            }
        }
        return ok;
    }
    
    private boolean connectAll() {
        boolean ret = reconnect();
        ret &= removeDuplicatePins();
        ret &= reconnect();
        return ret;
    }
    
    private boolean reconnect() {
        boolean ret = true;
        for (final var c : this.connections) {
            ret &= c.reconnect();
        }
        return ret;
    }
    
    @Override
    public boolean removeModule(final IFlowModule module) {
        boolean ret = this.modules.remove(module);
        if (ret) {
            ret = this.gr.removeModule((ModuleGR) module.getGraphicalRepresentation());
            assert ret;
            notifyObservers(module);
        }
        return ret;
    }
    
    @Override
    public boolean removeConnection(final IConnection connection) {
        boolean ret = this.connections.remove(connection);
        if (ret) {
            ret = this.gr.removeConnection((ConnectionGR) connection.getGraphicalRepresentation());
            assert ret;
            notifyObservers(connection);
        }
        return ret;
    }
    
    @Override
    public List<IFlowModule> getModules() {
        final List<IFlowModule> ret = new ArrayList<>(this.modules);
        ret.sort(getIdComparator());
        return ret;
    }
    
    @Override
    public List<IConnection> getConnections() {
        final List<IConnection> ret = new ArrayList<>(this.connections);
        ret.sort(getIdComparator());
        return ret;
    }
    
    @Override
    public List<IDefaultArrow> getArrows() {
        return getConnections()
                .stream()
                .filter(c -> c instanceof DefaultArrow)
                .map(c -> (DefaultArrow)c)
                .collect(Collectors.toList());
    }
    
    @Override
    public IDefaultArrow validate() throws ValidationException {
        final List<IDefaultArrow> arrows = getArrows();
        for (final var arrow : arrows) {
            arrow.putData(null);
        }
        
        final List<IFlowModule> moduleList = getModules().stream().sorted().collect(Collectors.toList());
        if (moduleList.isEmpty()) {
            return null; // no modules --> valid
        }
        final Set<IFlowModule> moduleSet = new HashSet<>(moduleList);
        final IFlowModule moduleZero = moduleList.get(0);
        
        // find all roots
        final Set<IFlowModule> visitedModules = new HashSet<>();
        final Set<IFlowModule> roots = new HashSet<>();
        for (IFlowModule module = moduleZero; visitedModules.size() < moduleList.size(); 
                module = setComplement(visitedModules, moduleSet).stream().findFirst().orElse(null)
                /* module is element of modulelList but not of visitedModules  */) {
            final Pair<Set<IFlowModule>, Set<IFlowModule>> visitedModulesAndFoundRoots =
                    bfsDependency(module);
            visitedModules.addAll(visitedModulesAndFoundRoots.first);
            roots.addAll(visitedModulesAndFoundRoots.second);
        }
        
        // Now we have found all roots, we can now begin validating from the roots on
        // foreach root an own list
        final Map<IFlowModule, List<IFlowModule>> modulesInCorrectOrder = new HashMap<>();
        for(final IFlowModule module : roots) {
            final Pair<Set<IFlowModule>, List<IFlowModule>> visitedModulesAndSortedModuleList =
                    bfsAdjacency(module);
            modulesInCorrectOrder.put(module, visitedModulesAndSortedModuleList.second);
        }
        
        final int maxSize = modulesInCorrectOrder.values().stream().mapToInt(x -> x.size()).max().orElse(0);
        // the for-loops must be like this, because the lists must be validated "parallely"
        for (int i = 0; i < maxSize; i++) {
            for (final IFlowModule module : roots) {
                final List<IFlowModule> list = modulesInCorrectOrder.get(module);
                if (i < list.size()) {
                    final IDefaultArrow arrow = list.get(i).validate();
                    if (arrow != null) {
                        return arrow;
                    }
                }
            }
        }
        
        return null;
    }

    private SortedSet<IFlowModule> setComplement(final Set<IFlowModule> notOf, final Set<IFlowModule> of) {
        return new TreeSet<>(of.stream().filter(x -> !notOf.contains(x)).collect(Collectors.toSet()));
    }
    
    private static final boolean DEPENDENCY = true;
    private static final boolean ADJACENCY = !DEPENDENCY;
    
    private Pair<Set<IFlowModule>, Set<IFlowModule>> bfsDependency(final IFlowModule root) {
        final Set<IFlowModule> roots = new HashSet<>();
        final Pair<Set<IFlowModule>, List<IFlowModule>> bfsResult = bfs(root, DEPENDENCY, roots);
        return new Pair<>(bfsResult.first, roots);
    }

    private Pair<Set<IFlowModule>, List<IFlowModule>> bfsAdjacency(final IFlowModule root) {
        return bfs(root, ADJACENCY, new HashSet<>());
    }
    
    private Pair<Set<IFlowModule>, List<IFlowModule>> bfs(final IFlowModule root, 
                final boolean isDependencySearch, final Set<IFlowModule> highestLevelMarker) {
        // BFS and setting highest level marker
        final Map<IFlowModule, IFlowModule> parentMap = new HashMap<>(); // maps child -> parent
        final Map<IFlowModule, Integer> levelMap = new HashMap<>(); // maps module -> level
        Set<IFlowModule> q = new HashSet<>();
        q.add(root);
        parentMap.put(root, root);
        levelMap.put(root, 0);
        for (int level = 0; !q.isEmpty(); level++) {
            final Set<IFlowModule> qLocal = new HashSet<>();
            for(final IFlowModule module : q) {
                // scan module
                int adjFoundCount = 0;
                final List<IFlowModule> adjList = isDependencySearch 
                        ? module.getDefaultDependencyList() : module.getDefaultAdjacencyList();
                for (final IFlowModule adj : adjList) {
                    if (!parentMap.containsKey(adj)) { // unexplored
                        qLocal.add(adj);
                        levelMap.put(adj, level + 1);
                        parentMap.put(adj, module); // module is now parent of adj
                        adjFoundCount++;
                    }
                }
                if (adjFoundCount == 0) {
                    highestLevelMarker.add(module);
                }
            }
            q = qLocal;
        }
        
        // generating the return value
        int highestLevel = 0;
        final Set<IFlowModule> visited = parentMap.keySet();
        final List<IFlowModule> listOfModulesSorted = new ArrayList<>();
        for (final IFlowModule module : levelMap.keySet()) {
            final int level = levelMap.get(module);
            listOfModulesSorted.add(module);
            highestLevel = level > highestLevel ? level : highestLevel;
        }
        sort(listOfModulesSorted, levelMap);
        return new Pair<>(visited, listOfModulesSorted);
    }

    // sort by level and id
    private void sort(final List<IFlowModule> listOfModules, final Map<IFlowModule, Integer> levelMap) {
        List<Pair<IFlowModule, Integer>> pairList = new ArrayList<>();
        for (final IFlowModule module : listOfModules) {
            pairList.add(new Pair<>(module, levelMap.get(module)));
        }
        pairList.sort(new Comparator<Pair<IFlowModule, Integer>>() {
            @Override
            public int compare(final Pair<IFlowModule, Integer> p1, final Pair<IFlowModule, Integer> p2) {
                int ret = p1.second.compareTo(p2.second);
                if (ret == 0) {
                    ret = Integer.valueOf(p1.first.getId()).compareTo(p2.first.getId());
                }
                return ret;
            }
        });
        listOfModules.clear();
        listOfModules.addAll(pairList.stream().map(x -> x.first).collect(Collectors.toList()));
    }

    @Override
    public IGraphicalRepresentation getGraphicalRepresentation() {
        return this.gr;
    }

    @Override
    public String serializeSimulationState() {
        // TODO implement
        return null;
    }

    @Override
    public void restoreSerializedSimulationState(String simulationState) {
        // TODO implement
    }

    @Override
    public IDOM getDOM() {
        final FlowChartDOM ret = new FlowChartDOM();
        ret.setID(getId());
        this.modules.forEach(m -> ret.addModule(m));
        this.connections.forEach(c -> ret.addConnection(c));
        ret.setGraphicalRepresentation(getGraphicalRepresentation());
        return ret;
    }

    private boolean removeDuplicatePins() {
        final Map<IModulePin, IFlowModule> modulePins = new HashMap<>();
        this.modules.stream()
            .map(m -> m.getAllModulePins())
            .flatMap(l -> l.stream())
            .collect(Collectors.toList())
            .forEach(p -> modulePins.put(p, p.getModule()));
        final Map<IModulePin, List<IConnection>> connectionPins = new HashMap<>();
        final List<IModulePin> list = new ArrayList<>(Arrays.asList(this.connections.stream()
                                        .map(c -> c.getToPins())
                                        .flatMap(l -> l.stream())
                                        .toArray(ModulePin[]::new)));
        list.addAll(this.connections.stream()
                    .map(c -> c.getFromPin())
                    .collect(Collectors.toList()));
        list.forEach(p -> connectionPins.put(p, p.getConnections()));
        
        for (final IModulePin pin : modulePins.keySet()) {
            final IFlowModule module = modulePins.get(pin);
            assert module != null;
            if (connectionPins.containsKey(pin)) {
                final List<IConnection> connections = connectionPins.get(pin);
                for (final IConnection connection : connections) {
                    if (pin instanceof InputPin) {
                        final InputPin inPin = (InputPin)pin;
                        connection.removeToPin(inPin);
                        try {
                            IDataSignature beforeSign = null;
                            IDataSignature copy = null;
                            if (inPin.getModulePinImp() instanceof DefaultPin) {
                                final DefaultPin defaultPin = ((DefaultPin)inPin.getModulePinImp());
                                beforeSign = defaultPin.getCheckDataSignature();
                                copy = beforeSign.getCopy();
                                copy.deactivateChecking();
                                defaultPin.setCheckDataSignature(copy);
                                connection.addToPin(inPin);
                                defaultPin.setCheckDataSignature(beforeSign);
                            } else {
                                connection.addToPin(inPin);
                            }
                        } catch (FlowException e) {
                            // should not happen
                        	new Warning(null, e.toString(), true).reportWarning();
                            e.printStackTrace();
                        }
                    } else { // pin is OutputPin
                        final OutputPin outPin = (OutputPin)pin;
                        try {
                            IDataSignature beforeSign = null;
                            IDataSignature copy = null;
                            if (outPin.getModulePinImp() instanceof DefaultPin) {
                                final DefaultPin defaultPin = ((DefaultPin)outPin.getModulePinImp());
                                beforeSign = defaultPin.getCheckDataSignature();
                                copy = beforeSign.getCopy();
                                copy.deactivateChecking();
                                defaultPin.setCheckDataSignature(copy);
                                connection.setFromPin(outPin);
                                defaultPin.setCheckDataSignature(beforeSign);
                            } else {
                                connection.setFromPin(outPin);
                            }
                        } catch (FlowException e) {
                            // should not happen
                        	new Warning(null, e.toString(), true).reportWarning();
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return checkIDs() && checkConnectionsPinsModules();
    }

    private boolean checkIDs() {
        final List<IFlowChartElement> elements = new ArrayList<>(this.modules);
        elements.addAll(this.connections);
        elements.add(this);
        elements.add(GENERIC_ERROR_ELEMENT);
        final int[] ids = elements.stream().mapToInt(e -> e.getId()).toArray();
        return Arrays.stream(ids).count() == Arrays.stream(ids).distinct().count();
    }
    
    private boolean checkConnectionsPinsModules() {
        return this.connections
                .stream()
                .allMatch(c -> this.modules.containsAll(c.getConnectedModules()));
    }

    @Override
    public void restoreFromDOM(final IDOM dom) {
        final boolean isTest = getId() == Integer.MAX_VALUE;
        if (isDOMValid(dom)) {
            this.modules.clear();
            this.connections.clear();
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            final IDOM idDom = (IDOM)domMap.get(FlowChartDOM.NAME_ID).getValue();
            final String idStr = idDom.elemGet();
            final int id = Integer.parseInt(idStr);
            setId(id);
            final IDOM connectionsDom = (IDOM)domMap.get(FlowChartDOM.NAME_CONNECTIONS).getValue();
            final Map<String, DOMStringUnion> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj.isDOM()) {
                    final IDOM connnectionDom = (IDOM) conObj.getValue();
                    final IDOM cnDom = (IDOM) (connnectionDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME)).getValue();
                    final String conToLoad = cnDom.elemGet();
                    final Connection connection = (Connection) DynamicObjectLoader.loadConnection(conToLoad);
                    this.connections.add(connection);
                    final IDOM connectionIdDom = (IDOM)connnectionDom.getDOMMap().get(ConnectionDOM.NAME_ID).getValue();
                    connection.setId(Integer.parseInt(connectionIdDom.elemGet()));
                    connection.restoreFromDOM(connnectionDom);
                }
            }
            final IDOM modulesDom = (IDOM)domMap.get(FlowChartDOM.NAME_MODULES).getValue();
            final Map<String, DOMStringUnion> modulesMap = modulesDom.getDOMMap();
            for (final var modObj : modulesMap.values()) {
                if (modObj.isDOM()) {
                    final IDOM modDom = (IDOM) modObj.getValue();
                    final IDOM cnDom = (IDOM) (modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME).getValue());
                    final String moduleToLoad = cnDom.elemGet();
                    final FlowModule module = (FlowModule) DynamicObjectLoader.loadModule(moduleToLoad, 0);
                    this.modules.add(module);
                    final IDOM moduleIdDom = (IDOM)modDom.getDOMMap().get(ConnectionDOM.NAME_ID).getValue();
                    module.setId(Integer.parseInt(moduleIdDom.elemGet()));
                    module.restoreFromDOM(modDom);
                }
            }
            final IDOM grDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME).getValue();
            this.gr.restoreFromDOM(grDom);
            if (!isTest) {
                connectAll();
                notifyObservers();
            }
        }
    }

    @Override
    public boolean isDOMValid(final IDOM dom) {
        if (getId() == Integer.MAX_VALUE) {
            return true;
        }
        Objects.requireNonNull(dom);
        final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
        try {
            ok(domMap.get(FlowChartDOM.NAME_ID).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM idDom = (IDOM)domMap.get(FlowChartDOM.NAME_ID).getValue();
            final String idStr = idDom.elemGet();
            ok(idStr != null, OK.ERR_MSG_NULL);
            ok(domMap.get(FlowChartDOM.NAME_CONNECTIONS).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM connectionsDom = (IDOM)domMap.get(FlowChartDOM.NAME_CONNECTIONS).getValue();
            final Map<String, DOMStringUnion> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj.isDOM()) {
                    final IDOM connectionDom = (IDOM) conObj.getValue();
                    ok(connectionDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
                    final IDOM cnDom = (IDOM) (connectionDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME).getValue());
                    final String conToLoad = cnDom.elemGet();
                    ok(conToLoad != null, OK.ERR_MSG_NULL);
                    final Connection connection = ok(c -> (Connection) DynamicObjectLoader.loadConnection(c), conToLoad);
                    ok(connection.isDOMValid(connectionDom), "Connection " + OK.ERR_MSG_DOM_NOT_VALID);
                }
            }
            ok(domMap.get(FlowChartDOM.NAME_MODULES).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM modulesDom = (IDOM)domMap.get(FlowChartDOM.NAME_MODULES).getDOM();
            final Map<String, DOMStringUnion> modulesMap = modulesDom.getDOMMap();
            for (final var modObj : modulesMap.values()) {
                if (modObj.isDOM()) {
                    final IDOM modDom = (IDOM) modObj.getValue();
                    ok(modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
                    final IDOM cnDom = (IDOM) (modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME).getValue());
                    final String moduleToLoad = cnDom.elemGet();
                    ok(moduleToLoad != null, OK.ERR_MSG_NULL);
                    final FlowModule module = ok(m -> (FlowModule) DynamicObjectLoader.loadModule(m, 0), moduleToLoad);
                    ok(module.isDOMValid(modDom), "Module " + OK.ERR_MSG_DOM_NOT_VALID);
                }
            }
            ok(domMap.get(GraphicalRepresentationDOM.NAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM grDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME).getValue();
            ok(this.gr.isDOMValid(grDom), OK.ERR_MSG_DOM_NOT_VALID);
            final FlowChart test = new FlowChart(Integer.MAX_VALUE, new FlowChartGR());
            test.restoreFromDOM(dom);
            ok(test.connectAll(), "could not connect all connections");
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(this).reportWarning();
            return false;
        }
    }

    @Override
    public String toString() {
        return "ID= " + getId() + " | modules= " + getModules() + " | connections= " + getConnections();
    }
}
