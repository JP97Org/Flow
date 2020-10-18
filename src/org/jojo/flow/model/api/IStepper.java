package org.jojo.flow.model.api;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public interface IStepper extends IMinimalStepper, Runnable {

    void stepForward(Time<Fraction> time) throws ModuleRunException;

    void stepReal(Time<Double> time) throws ModuleRunException;

    void stepForward() throws ModuleRunException;

    void stepOnce() throws ModuleRunException; //TODO this and all other step methods calling this method clears all modules' warning lists

    void unpause();

    void pause();

    boolean isPaused();

    void reset() throws FlowException;

    Frequency<Fraction> getFrequency();

    int getStepCount();

    Time<Fraction> getTimePassed();
}