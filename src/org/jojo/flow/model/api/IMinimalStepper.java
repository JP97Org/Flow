package org.jojo.flow.model.api;

import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Time;

public interface IMinimalStepper extends IAPI {
    void performSimulationStep(Time<Fraction> time) throws ModuleRunException;
}
