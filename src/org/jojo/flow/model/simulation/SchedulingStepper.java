package org.jojo.flow.model.simulation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.IllegalUnitOperationException;
import org.jojo.flow.model.data.Unit;
import org.jojo.flow.model.data.UnitSignature;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.modules.FlowModule;

public class SchedulingStepper extends Stepper {
    private final FlowChart flowChart;
    private final Unit<Fraction> explicitTimeStep;

    private boolean paused;
    private Unit<Fraction> timeStep;
    private int stepCount;
    private Unit<Fraction> timePassed;

    public SchedulingStepper(final FlowChart flowChart, final Scheduler scheduler, final Unit<Fraction> explicitTimeStep) throws FlowException {
        super(scheduler);
        this.flowChart = flowChart;
        this.explicitTimeStep = explicitTimeStep;
        this.paused = false;
        reset();
    }
    
    private void setTimeStep() throws FlowException {
        if (this.explicitTimeStep != null) {
            this.timeStep = this.explicitTimeStep;
            return;
        }
        
        try {
            this.timeStep = Unit.getFractionConstant(new Fraction(1)).divide(getMaxFrequency());
            // check if time step is a time
            this.timeStep.add(Unit.getFractionConstant(new Fraction(0)).multiply(UnitSignature.SECOND));
        } catch (IllegalUnitOperationException e) {
            throw new FlowException(e, this.flowChart);
        } catch (ArithmeticException e) {
            throw new FlowException(e, this.flowChart);
        }
    }

    private Unit<Fraction> getMaxFrequency() {
        Unit<Fraction> maxFrequency = Unit.getFractionConstant(new Fraction(0)).multiply(UnitSignature.HERTZ);
        maxFrequency = this.flowChart.getModules().stream().map(x -> x.getFrequency())
                .max(new Comparator<Unit<Fraction>>() {
                    @Override
                    public int compare(Unit<Fraction> f1, Unit<Fraction> f2) {
                        return Double.valueOf(f1.value.doubleValue()).compareTo(f2.value.doubleValue());
                    }
                }).orElse(maxFrequency);
        return maxFrequency.value.intValue() == 0
                ? Unit.getFractionConstant(new Fraction(1)).multiply(UnitSignature.HERTZ)
                : maxFrequency;
    }

    @Override
    public void performSimulationStep(final Unit<Fraction> time) throws ModuleRunException {
        stepForward(time);
    }

    @Override
    public void stepForward(final Unit<Fraction> time) throws ModuleRunException {
        final Unit<Fraction> timeBefore = this.timePassed;
        try {
            while (this.timePassed.subtract(timeBefore).value.doubleValue() < time.value.doubleValue()) {
                stepOnce();
            }
        } catch (IllegalUnitOperationException e) {
            // should not happen
            e.printStackTrace();
        }
    }

    @Override
    public void stepForward() throws ModuleRunException {
        if (!isPaused()) {
            stepOnce();
        }
    }
    
    @Override
    public void run() {
        try {
            stepOnce();
        } catch (ModuleRunException e) {
            this.flowChart.reportWarning(e.getWarning());
        }
    }

    @Override
    public void stepOnce() throws ModuleRunException {
        final List<FlowModule> allModules = this.flowChart.getModules();
        final List<FlowModule> modulesToStep = allModules
                .stream()
                .filter(x -> ((this.stepCount % getStep(x)) == 0))
                .collect(Collectors.toList());
        
        final List<FlowModule> schedule = getScheduler().getSchedule(modulesToStep);
        for (final FlowModule module : schedule) {
            try {
                module.run();
            } catch (Exception e) {
                try {
                    reset();
                } catch (FlowException e1) {
                    // should not happen
                    e1.printStackTrace();
                }
                throw new ModuleRunException(new Warning(module, ModuleRunException.MOD_RUN_EXC_STR + e.getMessage(), true));
            }
        }
        this.stepCount++;
        try {
            this.timePassed = this.timePassed.add(this.timeStep);
        } catch (IllegalUnitOperationException e) {
            // should not happen
            e.printStackTrace();
        }
    }

    private int getStep(final FlowModule x) {
        try {
            final Unit<Fraction> moduleStep = Unit.getFractionConstant(new Fraction(1)).divide(x.getFrequency());
            final Fraction frac = moduleStep.divide(this.timeStep).value;
            final int ret = (int) (frac.getNumerator() / frac.getDenominator());
            return ret == 0 ? 1 : ret;
        } catch (IllegalUnitOperationException | ArithmeticException e) {
            // should not happen
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void pause() {
        this.paused = true;
    }

    @Override
    public boolean isPaused() {
        return this.paused;
    }

    @Override
    public void reset() throws FlowException {
        pause();
        setTimeStep();
        this.stepCount = 0;
        this.timePassed = Unit.getFractionConstant(new Fraction(0)).multiply(UnitSignature.SECOND);
    }

    @Override
    public Unit<Fraction> getFrequency() {
        try {
            return Unit.getFractionConstant(new Fraction(1)).divide(this.timeStep);
        } catch (IllegalUnitOperationException | ArithmeticException e) {
            // should not happen
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getStepCount() {
        return this.stepCount;
    }

    @Override
    public Unit<Fraction> getTimePassed() {
        return this.timePassed;
    }
}
