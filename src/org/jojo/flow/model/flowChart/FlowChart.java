package org.jojo.flow.model.flowChart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jojo.flow.model.data.Pair;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.StdArrow;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.storeLoad.DOM;

public class FlowChart extends FlowChartElement{
    private final List<FlowModule> modules;
    private final List<Connection> connections;
    
    public FlowChart(final int id) {
        super(id);
        this.modules = new ArrayList<>();
        this.connections = new ArrayList<>();
    }
    
    /*TODO add constr. with final DOM dom*/
    
    public void addModule(final FlowModule module) {
        this.modules.add(module);
        notifyObservers(module);
    }
    
    public void addConnection(final Connection connection) {
        this.connections.add(connection);
        notifyObservers(connection);
    }
    
    public boolean removeModule(final FlowModule module) {
        final boolean ret = this.modules.remove(module);
        if (ret) {
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
        notifyObservers(module);
        return true;
    }
    
    public boolean removeConnection(final Connection connection) {
        final boolean ret = this.connections.remove(connection);
        if (ret) {
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

    @Override
    public DOM getDOM() {
        // TODO Auto-generated method stub
        return null;
    }
}
