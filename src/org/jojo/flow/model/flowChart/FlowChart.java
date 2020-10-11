package org.jojo.flow.model.flowChart;

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

import org.jojo.flow.model.data.Pair;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.ConnectionException;
import org.jojo.flow.model.flowChart.connections.ConnectionGR;
import org.jojo.flow.model.flowChart.connections.StdArrow;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.storeLoad.ConnectionDOM;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;
import org.jojo.flow.model.storeLoad.FlowChartDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModuleDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;

import static org.jojo.flow.model.storeLoad.OK.ok;

public class FlowChart extends FlowChartElement{
    private final List<FlowModule> modules;
    private final List<Connection> connections;
    
    private final FlowChartGR gr;
    
    public FlowChart(final int id, final FlowChartGR gr) {
        super(id);
        this.modules = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.gr = gr;
        this.gr.setFlowChart(this);
    }
    
    public void addModule(final FlowModule module) {
        this.modules.add(module);
        this.gr.addModule((ModuleGR) module.getGraphicalRepresentation());
        notifyObservers(module);
    }
    
    public void addConnection(final Connection connection) {
        this.connections.add(connection);
        this.gr.addConnection((ConnectionGR) connection.getGraphicalRepresentation());
        notifyObservers(connection);
    }
    
    public boolean removeModule(final FlowModule module) {
        boolean ret = this.modules.remove(module);
        if (ret) {
            ret = this.gr.removeModule((ModuleGR) module.getGraphicalRepresentation());
            assert ret;
            notifyObservers(module);
        }
        return ret;
    }
    
    public boolean removeModule(final int index) {
        if (index >= this.modules.size()) {
            return false;
        }
        final FlowModule module = this.modules.get(index);
        this.modules.remove(index);
        boolean ok = this.gr.removeModule(index);
        assert ok;
        notifyObservers(module);
        return true;
    }
    
    public boolean removeConnection(final Connection connection) {
        boolean ret = this.connections.remove(connection);
        if (ret) {
            ret = this.gr.removeConnection((ConnectionGR) connection.getGraphicalRepresentation());
            assert ret;
            notifyObservers(connection);
        }
        return ret;
    }
    
    public boolean removeConnection(final int index) {
        if (index >= this.connections.size()) {
            return false;
        }
        final Connection con = this.connections.get(index);
        this.connections.remove(index);
        boolean ok = this.gr.removeConnection(index);
        assert ok;
        notifyObservers(con);
        return true;
    }
    
    public List<FlowModule> getModules() {
        return new ArrayList<>(this.modules);
    }
    
    public List<Connection> getConnections() {
        return new ArrayList<>(this.connections);
    }
    
    public StdArrow validate() throws ValidationException {
        final List<FlowModule> moduleList = getModules().stream().sorted().collect(Collectors.toList());
        if (moduleList.isEmpty()) {
            return null; // no modules --> valid
        }
        final Set<FlowModule> moduleSet = new HashSet<>(moduleList);
        final FlowModule moduleZero = moduleList.get(0);
        
        // find all roots
        final Set<FlowModule> visitedModules = new HashSet<>();
        final Set<FlowModule> roots = new HashSet<>();
        for (FlowModule module = moduleZero; visitedModules.size() < moduleList.size(); 
                module = setComplement(visitedModules, moduleSet).first()
                /* module is element of modulelList but not of visitedModules  */) {
            final Pair<Set<FlowModule>, Set<FlowModule>> visitedModulesAndFoundRoots =
                    bfsDependency(module);
            visitedModules.addAll(visitedModulesAndFoundRoots.first);
            roots.addAll(visitedModulesAndFoundRoots.second);
        }
        
        // Now we have found all roots, we can now begin validating from the roots on
        // foreach root an own list
        final Map<FlowModule, List<FlowModule>> modulesInCorrectOrder = new HashMap<>();
        for(final FlowModule module : roots) {
            final Pair<Set<FlowModule>, List<FlowModule>> visitedModulesAndSortedModuleList =
                    bfsAdjacency(module);
            modulesInCorrectOrder.put(module, visitedModulesAndSortedModuleList.second);
        }
        
        final int maxSize = modulesInCorrectOrder.values().stream().mapToInt(x -> x.size()).max().orElse(0);
        // the for-loops must be like this, because the lists must be validated "parallely"
        for (int i = 0; i < maxSize; i++) {
            for (final FlowModule module : roots) {
                final List<FlowModule> list = modulesInCorrectOrder.get(module);
                if (i < list.size()) {
                    final StdArrow arrow = list.get(i).validate();
                    if (arrow != null) {
                        return arrow;
                    }
                }
            }
        }
        
        return null;
    }

    private SortedSet<FlowModule> setComplement(final Set<FlowModule> notOf, final Set<FlowModule> of) {
        return new TreeSet<>(of.stream().filter(x -> !notOf.contains(x)).collect(Collectors.toSet()));
    }
    
    private static final boolean DEPENDENCY = true;
    private static final boolean ADJACENCY = !DEPENDENCY;
    
    private Pair<Set<FlowModule>, Set<FlowModule>> bfsDependency(final FlowModule root) {
        final Set<FlowModule> roots = new HashSet<>();
        final Pair<Set<FlowModule>, List<FlowModule>> bfsResult = bfs(root, DEPENDENCY, roots);
        return new Pair<>(bfsResult.first, roots);
    }

    private Pair<Set<FlowModule>, List<FlowModule>> bfsAdjacency(final FlowModule root) {
        return bfs(root, ADJACENCY, new HashSet<>());
    }
    
    private Pair<Set<FlowModule>, List<FlowModule>> bfs(final FlowModule root, 
                final boolean isDependencySearch, final Set<FlowModule> highestLevelMarker) {
        // BFS and setting highest level marker
        final Map<FlowModule, FlowModule> parentMap = new HashMap<>(); // maps child -> parent
        final Map<FlowModule, Integer> levelMap = new HashMap<>(); // maps module -> level
        Set<FlowModule> q = new HashSet<>();
        q.add(root);
        parentMap.put(root, root);
        levelMap.put(root, 0);
        for (int level = 0; !q.isEmpty(); level++) {
            final Set<FlowModule> qLocal = new HashSet<>();
            for(final FlowModule module : q) {
                // scan module
                int adjFoundCount = 0;
                final List<FlowModule> adjList = isDependencySearch 
                        ? module.getStdDependencyList() : module.getStdAdjacencyList();
                for (final FlowModule adj : adjList) {
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
        final Set<FlowModule> visited = parentMap.keySet();
        final List<FlowModule> listOfModulesSorted = new ArrayList<>();
        for (final FlowModule module : levelMap.keySet()) {
            final int level = levelMap.get(module);
            listOfModulesSorted.add(module);
            highestLevel = level > highestLevel ? level : highestLevel;
        }
        sort(listOfModulesSorted, levelMap);
        return new Pair<>(visited, listOfModulesSorted);
    }

    // sort by level and id
    private void sort(final List<FlowModule> listOfModules, final Map<FlowModule, Integer> levelMap) {
        List<Pair<FlowModule, Integer>> pairList = new ArrayList<>();
        for (final FlowModule module : listOfModules) {
            pairList.add(new Pair<>(module, levelMap.get(module)));
        }
        pairList.sort(new Comparator<Pair<FlowModule, Integer>>() {
            @Override
            public int compare(final Pair<FlowModule, Integer> p1, final Pair<FlowModule, Integer> p2) {
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
    public GraphicalRepresentation getGraphicalRepresentation() {
        return this.gr;
    }

    @Override
    public InternalConfig serializeInternalConfig() {
        return null; // no internal config exists
    }

    @Override
    public void restoreSerializedInternalConfig(InternalConfig internalConfig) {
        // no internal config exists
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
    public DOM getDOM() {
        final FlowChartDOM ret = new FlowChartDOM();
        ret.setID(getId());
        this.modules.forEach(m -> ret.addModule(m));
        this.connections.forEach(c -> ret.addConnection(c));
        ret.setGraphicalRepresentation(getGraphicalRepresentation());
        return ret;
    }

    private void removeDuplicatePins() {
        final Map<ModulePin, FlowModule> modulePins = new HashMap<>();
        this.modules.stream()
            .map(m -> m.getAllModulePins())
            .flatMap(l -> l.stream())
            .collect(Collectors.toList())
            .forEach(p -> modulePins.put(p, p.getModule()));
        final Map<ModulePin, List<Connection>> connectionPins = new HashMap<>();
        final List<ModulePin> list = new ArrayList<>(Arrays.asList(this.connections.stream()
                                        .map(c -> c.getToPins())
                                        .flatMap(l -> l.stream())
                                        .toArray(ModulePin[]::new)));
        list.addAll(this.connections.stream()
                    .map(c -> c.getFromPin())
                    .collect(Collectors.toList()));
        list.forEach(p -> connectionPins.put(p, p.getConnections()));
        
        for (final ModulePin pin : modulePins.keySet()) {
            final FlowModule module = modulePins.get(pin);
            assert module != null;
            if (connectionPins.containsKey(pin)) {
                final List<Connection> connections = connectionPins.get(pin);
                for (final Connection connection : connections) {
                    if (pin instanceof InputPin) {
                        final InputPin inPin = (InputPin)pin;
                        connection.removeToPin(inPin);
                        try {
                            connection.addToPin(inPin);
                        } catch (ConnectionException e) {
                            // should not happen
                            e.printStackTrace();
                        }
                    } else { // pin is OutputPin
                        final OutputPin outPin = (OutputPin)pin;
                        try {
                            connection.setFromPin(outPin);
                        } catch (ConnectionException e) {
                            // should not happen
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            this.modules.clear();
            this.connections.clear();
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM idDom = (DOM)domMap.get(FlowChartDOM.NAME_ID);
            final String idStr = idDom.elemGet();
            final int id = Integer.parseInt(idStr);
            setId(id);
            final DOM connectionsDom = (DOM)domMap.get(FlowChartDOM.NAME_CONNECTIONS);
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj instanceof DOM) {
                    final DOM conDom = (DOM) conObj;
                    final DOM cnDom = (DOM) (conDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME));
                    final String conToLoad = cnDom.elemGet();
                    final Connection connection = DynamicObjectLoader.loadConnection(conToLoad);
                    connection.restoreFromDOM(conDom);
                    this.connections.add(connection);
                }
            }
            final DOM modulesDom = (DOM)domMap.get(FlowChartDOM.NAME_MODULES);
            final Map<String, Object> modulesMap = modulesDom.getDOMMap();
            for (final var modObj : modulesMap.values()) {
                if (modObj instanceof DOM) {
                    final DOM modDom = (DOM) modObj;
                    final DOM cnDom = (DOM) (modDom.getDOMMap().get(ModuleDOM.NAME_CLASSNAME));
                    final String moduleToLoad = cnDom.elemGet();
                    final FlowModule module = DynamicObjectLoader.loadModule(moduleToLoad);
                    module.restoreFromDOM(modDom);
                    this.modules.add(module);
                }
            }
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            this.gr.restoreFromDOM(grDom);
            removeDuplicatePins();
            notifyObservers();
        }
    }

    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        final Map<String, Object> domMap = dom.getDOMMap();
        try {
            ok(domMap.get(FlowChartDOM.NAME_ID) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM idDom = (DOM)domMap.get(FlowChartDOM.NAME_ID);
            final String idStr = idDom.elemGet();
            ok(idStr != null, OK.ERR_MSG_NULL);
            ok(domMap.get(FlowChartDOM.NAME_CONNECTIONS) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM connectionsDom = (DOM)domMap.get(FlowChartDOM.NAME_CONNECTIONS);
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj instanceof DOM) {
                    final DOM conDom = (DOM) conObj;
                    ok(conDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                    final DOM cnDom = (DOM) (conDom.getDOMMap().get(ConnectionDOM.NAME_CLASSNAME));
                    final String conToLoad = cnDom.elemGet();
                    ok(conToLoad != null, OK.ERR_MSG_NULL);
                    final Connection connection = ok(c -> DynamicObjectLoader.loadConnection(c), conToLoad);
                    ok(connection.isDOMValid(conDom), "Connection " + OK.ERR_MSG_DOM_NOT_VALID);
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
                    final FlowModule module = ok(m -> DynamicObjectLoader.loadModule(m), moduleToLoad);
                    ok(module.isDOMValid(modDom), "Module " + OK.ERR_MSG_DOM_NOT_VALID);
                }
            }
            ok(domMap.get(GraphicalRepresentationDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            ok(this.gr.isDOMValid(grDom), OK.ERR_MSG_DOM_NOT_VALID);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(this).reportWarning();
            return false;
        }
    }
}
