package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.model.api.IStepper;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public abstract class Stepper implements IStepper {
    private final Scheduler scheduler;
    
    public Stepper(final Scheduler scheduler) {
        this.scheduler = Objects.requireNonNull(scheduler);
    }
    
    @Override
    public abstract void stepForward(Time<Fraction> time) throws ModuleRunException;
    
    @Override
    public void stepReal(Time<Double> time) throws ModuleRunException {
        stepForward(Time.of(time.toFractionUnit()));
    }
    
    @Override
    public abstract void stepForward() throws ModuleRunException;
    @Override
    public abstract void stepOnce() throws ModuleRunException; //TODO this and all other step methods calling this method clears all modules' warning lists
    
    @Override
    public abstract void unpause();
    @Override
    public abstract void pause();
    @Override
    public abstract boolean isPaused();
    @Override
    public abstract void reset() throws FlowException;
    
    @Override
    public abstract Frequency<Fraction> getFrequency();
    @Override
    public abstract int getStepCount();
    @Override
    public abstract Time<Fraction> getTimePassed();
    
    protected Scheduler getScheduler() {
        return this.scheduler;
    }
}
