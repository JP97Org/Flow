package org.jojo.flow.test.model.simulation;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.exc.TimeoutException;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.ISimulation;
import org.jojo.flow.model.api.ISimulationConfiguration;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;
import org.jojo.flow.model.simulation.Simulation;
import org.jojo.flow.model.simulation.SimulationConfiguration;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.*;

public class SimulationTest {
    private IFlowChart fc;
    private ISimulationConfiguration config;
    private ISimulation simulation;
    
    @Before
    public void setUp() throws FlowException {
        this.fc = DynamicObjectLoader.loadEmptyFlowChart(0);
        for (int i = 0; i < 10; i++) {
            final var mod = (MockModule) DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), i + 1);
            mod.getExternalConfig().setPriority(i + 1);
            mod.setFrequency(Frequency.getFractionConstant(new Fraction(i + 1)));
            mod.setSlp(10);
            this.fc.addModule(mod);
        }
        this.config = new SimulationConfiguration(Time.getDoubleConstant(2), false);
        this.simulation = new Simulation(this.fc, this.config);
    }
    
    @Test
    public void stepOnceTest() throws ModuleRunException, TimeoutException, FlowException {
        this.simulation.getStepper().unpause();
        this.simulation.stepOnce();
        this.simulation.getStepper().pause();
        Assert.assertTrue(this.simulation.getStepper().isPaused());
        Assert.assertEquals(1, this.simulation.getStepper().getStepCount());
        Assert.assertEquals(Time.getFractionConstant(new Fraction(1L, 10L)), 
                this.simulation.getStepper().getTimePassed());
    }
    
    @Test(expected=TimeoutException.class)
    public void stepOnceTimeoutTest() throws ModuleRunException, TimeoutException, FlowException {
        ((MockModule)this.fc.getModules().get(9)).setSlp(5000);
        this.simulation.getStepper().unpause();
        this.simulation.stepOnce();
    }
    
    @Test
    public void stepForwardTest() throws InterruptedException {
        final Time<Fraction> twoSeconds = Time.getFractionConstant(new Fraction(2));
        this.simulation.stepForward(twoSeconds);
        for (int i = 0; !isCountElapsed(20);) {
            if (!isCountElapsed(19)) {
                System.out.println("Step-Count= " + this.simulation.getStepper().getStepCount());
                Thread.sleep(50);
            } else if (i == 0) {
                System.out.println("Step-Count= " + this.simulation.getStepper().getStepCount());
                i++;
            }
        }
        Thread.sleep(200);
        this.simulation.pause();
        Thread.sleep(50);
        Assert.assertTrue(this.simulation.getStepper().isPaused());
        Assert.assertEquals(20, this.simulation.getStepper().getStepCount());
        Assert.assertEquals(twoSeconds, this.simulation.getStepper().getTimePassed());
        Assert.assertTrue(this.fc.getWarnings().isEmpty());
        System.out.println(this.fc.getWarnings());
    }
    
    @Test
    public void stepForwardTimeoutTest() throws InterruptedException {
        final Time<Fraction> twoSeconds = Time.getFractionConstant(new Fraction(2));
        ((MockModule)this.fc.getModules().get(9)).setSlp(5000);
        this.simulation.stepForward(twoSeconds);
        Thread.sleep(2500);
        Assert.assertFalse(this.fc.getWarnings().isEmpty());
        Assert.assertTrue(this.fc.getWarnings().stream()
                .map(w -> w.toString())
                .anyMatch(s -> s.contains("timeout")));
        System.out.println(this.fc.getWarnings());
        this.simulation.forceStop();
    }
    
    @Test
    public void simulationTest() throws InterruptedException {
        Assert.assertTrue(this.fc.getWarnings().isEmpty());
        this.simulation.start();
        for (int i = 0; !isCountElapsed(20);) {
            if (!isCountElapsed(19)) {
                System.out.println("Step-Count= " + this.simulation.getStepper().getStepCount());
                Thread.sleep(50);
            } else if (i == 0) {
                System.out.println("Step-Count= " + this.simulation.getStepper().getStepCount());
                i++;
            }
        }
        this.simulation.pause();
        System.out.println("Step-Count= " + this.simulation.getStepper().getStepCount());
        Thread.sleep(50);
        Assert.assertTrue(this.simulation.getStepper().isPaused());
        Assert.assertTrue(this.simulation.getStepper().getStepCount() == 20 ||  this.simulation.getStepper().getStepCount() == 21);
        Assert.assertTrue(this.fc.getWarnings().isEmpty());
        System.out.println(this.fc.getWarnings());
    }
    
    @Test
    public void simulationTimeoutTest() throws InterruptedException {
        ((MockModule)this.fc.getModules().get(9)).setSlp(5000);
        this.simulation.start();
        Thread.sleep(2500);
        Assert.assertFalse(this.fc.getWarnings().isEmpty());
        Assert.assertTrue(this.fc.getWarnings().stream()
                .map(w -> w.toString())
                .anyMatch(s -> s.contains("timeout")));
        System.out.println(this.fc.getWarnings());
        this.simulation.forceStop();
    }
    
    private synchronized boolean isCountElapsed(int count) {
        return this.simulation.getStepper().getStepCount() >= count;
    }
}
