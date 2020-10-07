package org.jojo.flow.model.simulation;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.Unit;

public interface IStepper {
    void performSimulationStep(Unit<Fraction> time) throws ModuleRunException;
}
