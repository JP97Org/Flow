package org.jojo.flow.model.flowChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jojo.flow.model.data.Pair;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.StdArrow;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InternalConfig;

public class FlowChart extends FlowChartElement{
    private final List<FlowModule> modules;
    private final List<Connection> connections;
    
    public FlowChart() {
        this.modules = new ArrayList<>();
        this.connections = new ArrayList<>();
    }
    
    /*TODO add constr. with final DOM dom*/
    
    public void addModule(final FlowModule module) {
        this.modules.add(module);
    }
    
    public void addConnection(final Connection connection) {
        this.connections.add(connection);
    }
    
    public boolean removeModule(final FlowModule module) {
        return this.modules.remove(module);
    }
    
    public boolean removeModule(final int index) {
        if (index >= this.modules.size()) {
            return false;
        }
        this.modules.remove(index);
        return true;
    }
    
    public boolean removeConnection(final Connection connection) {
        return this.connections.remove(connection);
    }
    
    public boolean removeConnection(final int index) {
        if (index >= this.connections.size()) {
            return false;
        }
        this.connections.remove(index);
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
        final SortedSet<FlowModule> moduleSet = new TreeSet<>(moduleList);
        final FlowModule moduleZero = moduleList.get(0);
        
        // find all roots
        final SortedSet<FlowModule> visitedModules = new TreeSet<>();
        final SortedSet<FlowModule> roots = new TreeSet<>();
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
        SortedMap<FlowModule, List<FlowModule>> modulesInCorrectOrder = new TreeMap<>();
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

    private SortedSet<FlowModule> setComplement(final SortedSet<FlowModule> notOf, final SortedSet<FlowModule> of) {
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
        final SortedSet<FlowModule> setOfModulesSorted = new TreeSet<>();
        for (final FlowModule module : levelMap.keySet()) {
            final int level = levelMap.get(module);
            setOfModulesSorted.add(module);
            highestLevel = level > highestLevel ? level : highestLevel;
        }
        final List<FlowModule> listOfModulesSorted = new ArrayList<>(setOfModulesSorted);
        return new Pair<>(visited, listOfModulesSorted);
    }

    @Override
    public GraphicalRepresentation getGraphicalRepresentation() {
        // TODO implement
        return null;
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
}
