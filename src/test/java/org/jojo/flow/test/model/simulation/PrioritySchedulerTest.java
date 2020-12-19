package org.jojo.flow.test.model.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.simulation.PriorityScheduler;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.*;

public class PrioritySchedulerTest {
    private PriorityScheduler sched;
    private List<IFlowModule> modules;
    private List<IFlowModule> modulesOrdered;
    
    @Before
    public void setUp() {
        this.sched = new PriorityScheduler();
        this.modules = new ArrayList<>();
        this.modules.add(DynamicObjectLoader.loadModule(MockModule.class.getName(), 0));
        this.modules.add(DynamicObjectLoader.loadModule(MockModule.class.getName(), 1));
        this.modules.add(DynamicObjectLoader.loadModule(MockModule.class.getName(), 2));
        this.modules.add(DynamicObjectLoader.loadModule(MockModule.class.getName(), 3));
        this.modules.add(DynamicObjectLoader.loadModule(MockModule.class.getName(), 4));
        this.modules.get(0).getExternalConfig().setPriority(30); //0
        this.modules.get(1).getExternalConfig().setPriority(20); //1
        this.modules.get(2).getExternalConfig().setPriority(10); //2
        this.modules.get(3).getExternalConfig().setPriority(40); //3
        this.modules.get(4).getExternalConfig().setPriority(20); //4
        // correct ordering: 3, 0, 4, 1, 2
        this.modulesOrdered = new ArrayList<>();
        this.modulesOrdered.add(this.modules.get(3));
        this.modulesOrdered.add(this.modules.get(0));
        this.modulesOrdered.add(this.modules.get(4));
        this.modulesOrdered.add(this.modules.get(1));
        this.modulesOrdered.add(this.modules.get(2));
    }
    
    @Test
    public void getScheduleTest() {
        List<IFlowModule> schedule = this.sched.getSchedule(this.modules);
        Assert.assertTrue(schedule != null);
        Assert.assertTrue(schedule.size() == this.modules.size());
        Assert.assertEquals(Arrays.asList(this.modulesOrdered.stream().map(m -> m.getId()).toArray()),
                Arrays.asList(schedule.stream().map(m -> m.getId()).toArray()));
        Assert.assertEquals(this.modulesOrdered, schedule);
        Assert.assertArrayEquals(new int[] {40, 30, 20, 20, 10},
                schedule.stream().mapToInt(m -> m.getExternalConfig().getPriority()).toArray());
        
        schedule = this.sched.getSchedule(new ArrayList<>());
        Assert.assertTrue(schedule != null);
        Assert.assertTrue(schedule.isEmpty());
    }
}
