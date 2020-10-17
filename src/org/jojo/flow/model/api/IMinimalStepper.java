package org.jojo.flow.model.api;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Time;
import org.jojo.flow.model.simulation.ModuleRunException;

public interface IMinimalStepper extends IAPI {
    void performSimulationStep(Time<Fraction> time) throws ModuleRunException;
}
