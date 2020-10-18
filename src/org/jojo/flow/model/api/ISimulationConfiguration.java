package org.jojo.flow.model.api;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public interface ISimulationConfiguration extends IAPI {

    Frequency<Fraction> getStepperFrequency();

    void setStepperFrequency(Frequency<Fraction> stepperFrequency);

    Time<Double> getTimeout();

    void setTimeout(Time<Double> timeout);

    boolean isRealtime();

    void setRealtime(boolean isRealtime);
}