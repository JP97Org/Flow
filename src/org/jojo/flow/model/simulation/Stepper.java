package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.Unit;

public abstract class Stepper implements IStepper, Runnable {
    private final Scheduler scheduler;
    
    public Stepper(final Scheduler scheduler) {
        this.scheduler = Objects.requireNonNull(scheduler);
    }
    
    public abstract void stepForward(Unit<Fraction> time) throws ModuleRunException;
    
    public void stepReal(Unit<Double> time) throws ModuleRunException {
        stepForward(time.toFractionUnit());
    }
    
    public abstract void stepForward() throws ModuleRunException;
    public abstract void stepOnce() throws ModuleRunException;
    
    public abstract void pause();
    public abstract boolean isPaused();
    public abstract void reset() throws FlowException;
    
    public abstract Unit<Fraction> getFrequency();
    public abstract int getStepCount();
    public abstract Unit<Fraction> getTimePassed();
    
    protected Scheduler getScheduler() {
        return this.scheduler;
    }
}
