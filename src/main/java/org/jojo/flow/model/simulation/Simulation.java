package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.exc.TimeoutException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.ISimulation;
import org.jojo.flow.model.api.ISimulationConfiguration;
import org.jojo.flow.model.api.IStepper;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public class Simulation implements ISimulation {
    private final IFlowChart flowChart;
    private ISimulationConfiguration config;
    private IStepper stepper;
    
    private boolean isRunning;
    
    private Thread simThread;
    private Thread stepThread;
    
    public Simulation(final IFlowChart flowChart, final ISimulationConfiguration config) {
        this.flowChart = Objects.requireNonNull(flowChart);
        this.config = Objects.requireNonNull(config);
        initStepper();
        this.isRunning = false;
    }
    
    private void initStepper() {
        final Frequency<Fraction> frequency = this.config.getStepperFrequency();
        Time<Fraction> timeStep;
        try {
            timeStep = frequency == null 
                    ? null : Time.of(Unit.getFractionConstant(new Fraction(1)).divide(frequency));
            this.stepper = new SchedulingStepper(this.flowChart, new PriorityScheduler(), timeStep, config.isRealtime());
        } catch (IllegalArgumentException | FlowException e) {
            // should not happen
            new Warning(null, e.toString(), true).reportWarning();
        }
    }
    
    @Override
    public synchronized void start() {
        this.isRunning = true;
        this.stepper.unpause();
        this.simThread = new Thread(new Runnable() { 
            @Override
            public void run() {
                while (!stepper.isPaused()) {
                    try {
                        stepOnce();
                    } catch (FlowException e) {
                        //e.getWarning().reportWarning(); // already reported in exception creation
                    }
                }
                isRunning = false;
            }});
        this.simThread.start();
    }
    
    @Override
    public void stepForward(final Time<Fraction> time) {
        final Frequency<Fraction> frequency = this.stepper.getFrequency();
        try {
            final long count = (long) time.multiply(frequency).value.doubleValue();
            this.isRunning = true;
            this.stepper.unpause();
            this.simThread = new Thread(new Runnable() { 
                @Override
                public void run() {
                    for (long i = 0; i < count; i++) {
                        try {
                            stepOnce();
                        } catch (FlowException e) {
                            //e.getWarning().reportWarning(); // already reported in exception creation
                        }
                    }
                    stepper.pause();
                    isRunning = false;
                }
            });
            this.simThread.start();
        } catch (IllegalUnitOperationException e) {
            // should not happen
            new Warning(null, e.toString(), true).reportWarning();
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void stepOnce() throws ModuleRunException, TimeoutException, FlowException {
        this.stepThread = new Thread(this.stepper);
        stepThread.start();
        final double timeoutMillisD = config.getTimeout().value * 1000.;
        long timeoutMillis = (long)timeoutMillisD;
        int timeoutNanos = (int)((timeoutMillisD - timeoutMillis) * 1000000.);
        timeoutNanos = timeoutMillis == 0 && timeoutNanos == 0 ? 1 : timeoutNanos;
        timeoutMillis = timeoutMillis == 0 ? 1 : timeoutMillis + (timeoutNanos >= 500000 ? 1 : 0);
        try {
            long base = System.currentTimeMillis();
            long now = 0;
            TimeoutException timeoutExc = null;
            this.stepThread.join(timeoutMillis);
            now = System.currentTimeMillis() - base;
            long delay = timeoutMillis - now;
            if (delay <= 0) {
                timeoutExc = new TimeoutException(new Warning(this.flowChart, "a timeout occured, timeout= " + timeoutMillis + "ms", true));
            }
            if (!this.stepper.isPaused() && stepThread.isAlive() && timeoutExc != null) {
                forceStop();
                stepThread.join(timeoutMillis);
                throw timeoutExc;
            } else if (this.flowChart.getWarnings()
                    .stream()
                    .anyMatch(x -> x.getDescription().contains(ModuleRunException.MOD_RUN_EXC_STR))) {
                throw new ModuleRunException(this.flowChart.getWarnings()
                        .stream()
                        .filter(x -> x.getDescription().contains(ModuleRunException.MOD_RUN_EXC_STR))
                        .findFirst().orElse(null));
            }
        } catch (InterruptedException e) {
            throw new FlowException(new Warning(this.flowChart, e.toString(), true));
        }
    }
    
    @Override
    public void stop() throws FlowException, InterruptedException {
        this.stepper.reset();
        if (this.simThread != null && this.simThread.isAlive()) {
            this.simThread.join();
        }
        this.isRunning = false;
    }
    
    @Override
    public void forceStop() {
        this.stepper.pause();
        if (this.stepThread != null && this.stepThread.isAlive()) {
            this.stepThread.interrupt();
        }
        if (this.simThread != null && this.simThread.isAlive()) {
            this.simThread.interrupt();
        }
        this.isRunning = false;
    }

    @Override
    public void pause() throws InterruptedException {
        this.stepper.pause();
        if (this.simThread != null && this.simThread.isAlive()) {
            this.simThread.join();
        }
    }
    
    @Override
    public boolean isRunning() {
        return this.isRunning;
    }
    
    @Override
    public ISimulationConfiguration getConfig() throws IllegalStateException {
        if (this.isRunning) {
            throw new IllegalStateException("simulation must not be running when config should be changed");
        }
        return this.config;
    }
    
    @Override
    public void setConfig(final ISimulationConfiguration config) throws IllegalStateException {
        if (this.isRunning) {
            throw new IllegalStateException("simulation must not be running when config should be changed");
        }
        this.config = Objects.requireNonNull(config);
        reloadStepper();
    }
    
    @Override
    public IStepper getStepper() {
        return this.stepper;
    }
    
    @Override
    public void reloadStepper() {
        initStepper();
    }
}
