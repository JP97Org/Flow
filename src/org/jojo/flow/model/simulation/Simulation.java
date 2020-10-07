package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.Unit;
import org.jojo.flow.model.flowChart.FlowChart;

public class Simulation {
    private final FlowChart flowChart;
    private SimulationConfiguration config;
    private Stepper stepper;
    
    private boolean isRunning;
    
    private Thread stepThread;
    
    public Simulation(final FlowChart flowChart, final SimulationConfiguration config) throws FlowException {
        this.flowChart = flowChart;
        this.config = Objects.requireNonNull(config);
        initStepper();
        this.isRunning = false;
    }
    
    private void initStepper() throws FlowException {
        final Unit<Fraction> frequency = this.config.getStepperFrequency();
        final Unit<Fraction> timeStep = frequency == null 
                ? null : Unit.getFractionConstant(new Fraction(1)).divide(frequency);
        this.stepper = new SchedulingStepper(this.flowChart, new PriorityScheduler(), timeStep);
    }
    
    public void start() throws ModuleRunException, TimeoutException, FlowException {
        this.isRunning = true;
        while (!this.stepper.isPaused()) {
            stepOnce();
        }
        this.isRunning = false;
    }

    public synchronized void stepOnce() throws ModuleRunException, TimeoutException, FlowException {
        this.stepThread = new Thread(this.stepper);
        stepThread.start();
        final long timeoutMillis = (long)(config.getTimeout().value * 1000.);
        try {
            stepThread.join(timeoutMillis);
            if (stepThread.isAlive()) {
                throw new TimeoutException(new Warning(this.flowChart, "a timeout occured, timeout= " + timeoutMillis + "ms", true));
            } else if (this.flowChart.getWarnings()
                    .stream()
                    .anyMatch(x -> x.getDescription().contains(ModuleRunException.MOD_RUN_EXC_STR))) {
                throw new ModuleRunException(this.flowChart.getWarnings()
                        .stream()
                        .filter(x -> x.getDescription().contains(ModuleRunException.MOD_RUN_EXC_STR))
                        .findFirst().orElse(null));
            }
        } catch (InterruptedException e) {
            throw new FlowException(e, this.flowChart);
        }
    }
    
    public void stop() throws FlowException {
        this.stepper.reset();
    }
    
    @SuppressWarnings("deprecation")
    public void forceStop() {
        pause();
        if (this.stepThread != null && this.stepThread.isAlive()) {
            this.stepThread.stop();
        }
    }

    public void pause() {
        this.stepper.pause();
    }
    
    public boolean isRunning() {
        return this.isRunning;
    }
    
    public SimulationConfiguration getConfig() throws IllegalStateException {
        if (this.isRunning) {
            throw new IllegalStateException("simulation must not be running when config should be changed");
        }
        return this.config;
    }
    
    public void setConfig(final SimulationConfiguration config) throws IllegalStateException {
        if (this.isRunning) {
            throw new IllegalStateException("simulation must not be running when config should be changed");
        }
        this.config = Objects.requireNonNull(config);
    }
}
