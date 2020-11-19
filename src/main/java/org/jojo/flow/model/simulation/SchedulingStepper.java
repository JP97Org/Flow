package org.jojo.flow.model.simulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IScheduler;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public class SchedulingStepper extends Stepper {
    private static final long OVERHEAD_TIME = 5;
    private final IFlowChart flowChart;
    private final Time<Fraction> explicitTimeStep;

    private boolean paused;
    private Time<Fraction> timeStep;
    private int stepCount;
    private Time<Fraction> timePassed;
    
    private final boolean isRealtime; // do not use realtime if time step < 100ms

    public SchedulingStepper(final IFlowChart flowChart, final IScheduler scheduler, 
            final Time<Fraction> explicitTimeStep, final boolean isRealtime) throws FlowException {
        super(scheduler);
        this.flowChart = Objects.requireNonNull(flowChart);
        this.explicitTimeStep = explicitTimeStep;
        if (explicitTimeStep != null && explicitTimeStep.equals(Time.getFractionConstant(new Fraction(0)))) {
            throw new IllegalArgumentException("time step must not be 0");
        }
        this.paused = true;
        this.isRealtime = isRealtime;
        reset();
    }
    
    private void setTimeStep() throws FlowException {
        if (this.explicitTimeStep != null) {
            this.timeStep = this.explicitTimeStep;
            return;
        }
        
        try {
            this.timeStep = Time.of(Unit.getFractionConstant(new Fraction(1)).divide(getMaxFrequency()));
            // check if time step is a time
            this.timeStep.add(Unit.getFractionConstant(new Fraction(0)).multiply(UnitSignature.SECOND));
        } catch (IllegalUnitOperationException e) {
            throw new FlowException(e, this.flowChart);
        } catch (IllegalArgumentException e) {
            throw new FlowException(e, this.flowChart);
        }
    }

    private Frequency<Fraction> getMaxFrequency() {
        Frequency<Fraction> maxFrequency = Frequency.getFractionConstant(new Fraction(0));
        maxFrequency = this.flowChart.getModules().stream().map(x -> x.getFrequency())
                .max(new Comparator<Frequency<Fraction>>() {
                    @Override
                    public int compare(Frequency<Fraction> f1, Frequency<Fraction> f2) {
                        return Double.valueOf(f1.value.doubleValue()).compareTo(f2.value.doubleValue());
                    }
                }).orElse(maxFrequency);
        return maxFrequency.value.getNumerator() == 0
                ? Frequency.getFractionConstant(new Fraction(1))
                : maxFrequency;
    }

    @Override
    public void performSimulationStep(final Time<Fraction> time) throws ModuleRunException {
        stepForward(time);
    }

    @Override
    public void stepForward(final Time<Fraction> time) throws ModuleRunException {
        final Time<Fraction> timeBefore = this.timePassed;
        try {
            while (this.timePassed.subtract(timeBefore).value.doubleValue() < time.value.doubleValue()) {
                stepOnce();
            }
        } catch (IllegalUnitOperationException e) {
            // should not happen
        	new Warning(null, e.toString(), true).reportWarning();
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
        final long baseTime = System.currentTimeMillis();
        final List<IFlowModule> allModules = this.flowChart.getModules();
        final List<IFlowModule> modulesToStep = allModules
                .stream()
                .filter(x -> ((this.stepCount % getStep(x)) == 0))
                .collect(Collectors.toList());
        
        final List<IFlowModule> schedule = getScheduler().getSchedule(modulesToStep);
        for (final IFlowModule module : schedule) {
            if (Thread.interrupted()) {
                throw new ModuleRunException(new Warning(module, ModuleRunException.MOD_RUN_EXC_STR + "interrupted", true));
            }
            
            ModuleRunException exc = null;
            (new ArrayList<>(module.getWarnings())).stream().forEach(w -> module.warningResolved(w));
            final Thread runThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        module.run();
                    } catch (Exception e) {
                        resetSafely();
                        new Warning(module, ModuleRunException.MOD_RUN_EXC_STR + e.getMessage()).reportWarning();
                    }
                }
            });
            runThread.start();
            try {
                runThread.join();
            } catch (InterruptedException e) {
                exc = exc == null ? new ModuleRunException(new Warning(module, ModuleRunException.MOD_RUN_EXC_STR + "interrupt with message: " +  e.getMessage(), true)) : exc;
            }
            if (!module.getWarnings().isEmpty()) {
                exc = new ModuleRunException(module.getLastWarning());
            }
            if (exc != null) {
                throw exc;
            }
        }
        
        if (this.isRealtime) {
            final long timeStep = Math.round(this.timeStep.value.doubleValue() * 1000.);
            final long timeDifference = System.currentTimeMillis() - baseTime;
            final long timeToSleep = timeStep - (timeDifference + OVERHEAD_TIME);
            if (timeToSleep > 0) {
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    new Warning(flowChart, "realtime sleep interrupted, please set higher timeout or deactivate realtime", false).reportWarning();
                }
            }
        }
        
        this.stepCount++;
        try {
            this.timePassed = Time.of(this.timePassed.add(this.timeStep));
        } catch (IllegalUnitOperationException e) {
            // should not happen
        	new Warning(null, e.toString(), true).reportWarning();
            e.printStackTrace();
        }
    }

    private int getStep(final IFlowModule x) {
        try {
            final Time<Fraction> moduleStep = Time.of(Unit.getFractionConstant(new Fraction(1)).divide(x.getFrequency()));
            final Fraction frac = moduleStep.divide(this.timeStep).value;
            final int ret = (int) (frac.getNumerator() / frac.getDenominator());
            return ret == 0 ? 1 : ret;
        } catch (IllegalUnitOperationException | IllegalArgumentException e) {
            // should not happen
        	new Warning(null, e.toString(), true).reportWarning();
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void pause() {
        this.paused = true;
    }
    
    @Override
    public void unpause() {
        this.paused = false;
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
        this.timePassed = Time.getFractionConstant(new Fraction(0));
    }
    
    private void resetSafely() {
        try {
            reset();
        } catch (FlowException e) {
            // should not happen
        	new Warning(null, e.toString(), true).reportWarning();
            e.printStackTrace();
        }
    }

    @Override
    public Frequency<Fraction> getFrequency() {
        try {
            return Frequency.of(Unit.getFractionConstant(new Fraction(1)).divide(this.timeStep));
        } catch (IllegalUnitOperationException | IllegalArgumentException e) {
            // should not happen
        	new Warning(null, e.toString(), true).reportWarning();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getStepCount() {
        return this.stepCount;
    }

    @Override
    public Time<Fraction> getTimePassed() {
        return this.timePassed;
    }
}
