package org.jojo.flow.test.model.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            this.fc.addModule(mod);
        }
        this.stepper = new SchedulingStepper(this.fc, new PriorityScheduler(), null, false);
    }
    
    @Test
    public void stepOnceTest() throws ModuleRunException {
        this.stepper.stepOnce();
        Assert.assertEquals(1, this.stepper.getStepCount());
        Assert.assertEquals(Time.getFractionConstant(new Fraction(1L, 10L)), this.stepper.getTimePassed());
    }
    
    //TODO more tests
}
