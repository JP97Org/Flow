package org.jojo.flow.model.simulation;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Time;

public interface IStepper {
    void performSimulationStep(Time<Fraction> time) throws ModuleRunException;
}
