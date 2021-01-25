package org.jojo.flow.test.model.simulation;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;
import org.jojo.flow.model.simulation.PriorityScheduler;
import org.jojo.flow.model.simulation.SchedulingStepper;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.*;

public class SchedulingStepperTest {
    private IFlowChart fc;
    private SchedulingStepper stepper;
    
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
        this.stepper = new SchedulingStepper(this.fc, new PriorityScheduler(), null, false);
    }
    
    @Test
    public void getFrequencyTest() {
        Assert.assertEquals(Frequency.getFractionConstant(new Fraction(10)), this.stepper.getFrequency());
    }
    
    @Test
    public void stepOnceTest() throws ModuleRunException {
        this.stepper.stepOnce();
        Assert.assertEquals(1, this.stepper.getStepCount());
        Assert.assertEquals(Time.getFractionConstant(new Fraction(1L, 10L)), this.stepper.getTimePassed());
    }
    
    @Test
    public void performSimulationStepTest() throws ModuleRunException {
        final Time<Fraction> twoSeconds = Time.getFractionConstant(new Fraction(2));
        this.stepper.performSimulationStep(twoSeconds);
        Assert.assertEquals(20, this.stepper.getStepCount());
        Assert.assertEquals(twoSeconds, this.stepper.getTimePassed());
    }
    
    @Test
    public void stepForwardTest() throws ModuleRunException, InterruptedException {
        final Time<Fraction> twoSeconds = Time.getFractionConstant(new Fraction(2));
        this.stepper.unpause();
        for (int i = 0; i < 20; i++) {
            this.stepper.stepForward();
        }
        this.stepper.pause();
        Assert.assertTrue(this.stepper.isPaused());
        Assert.assertEquals(20, this.stepper.getStepCount());
        Assert.assertEquals(twoSeconds, this.stepper.getTimePassed());
    }

    @Test
    public void resetTest() throws FlowException {
        Assert.assertEquals(0, this.stepper.getStepCount());
        Assert.assertEquals(Time.getFractionConstant(new Fraction(0L, 10L)), this.stepper.getTimePassed());
        this.stepper.stepOnce();
        Assert.assertEquals(1, this.stepper.getStepCount());
        Assert.assertEquals(Time.getFractionConstant(new Fraction(1L, 10L)), this.stepper.getTimePassed());
        this.stepper.reset();
        Assert.assertEquals(0, this.stepper.getStepCount());
        Assert.assertEquals(Time.getFractionConstant(new Fraction(0L, 10L)), this.stepper.getTimePassed());
    }
    
    @Test
    public void startThreadTest() throws InterruptedException {
        final Time<Fraction> twoSeconds = Time.getFractionConstant(new Fraction(2));
        this.stepper.unpause();
        while (!isCountElapsed(20)) {
            final Thread stepperThread = new Thread(this.stepper);
            stepperThread.start();
            for (int i = 0; i < 1000; i++) {
                if (i % 500 == 0) {
                    System.out.println("doing other stuff while stepper thread is running...");
                }
            }
            stepperThread.join();
            Assert.assertFalse(stepperThread.isAlive());
        }
        this.stepper.pause();
        Thread.sleep(50);
        Assert.assertTrue(this.stepper.isPaused());
        Assert.assertEquals(20, this.stepper.getStepCount());
        Assert.assertEquals(twoSeconds, this.stepper.getTimePassed());
    }

    private synchronized boolean isCountElapsed(int count) {
        return this.stepper.getStepCount() >= count;
    }
}
