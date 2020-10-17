package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;
import org.jojo.flow.model.flowChart.FlowChart;

public class Simulation {
    private final FlowChart flowChart;
    private SimulationConfiguration config;
    private Stepper stepper;
    
    private boolean isRunning;
    
    private Thread simThread;
    private Thread stepThread;
    
    public Simulation(final FlowChart flowChart, final SimulationConfiguration config) {
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
        } catch (ArithmeticException | FlowException e) {
            // should not happen
            new Warning(null, e.toString(), true).reportWarning();
        }
    }
    
    public void start() throws ModuleRunException, TimeoutException, FlowException {
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
            while (stepThread.isAlive()) {
                long delay = timeoutMillis - now;
                if (delay <= 0) {
                    timeoutExc = new TimeoutException(new Warning(this.flowChart, "a timeout occured, timeout= " + timeoutMillis + "ms", true));
                    break;
                }
                wait(delay);
                now = System.currentTimeMillis() - base;
            }
            if (stepThread.isAlive() && timeoutExc != null) {
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
            throw new FlowException(e, this.flowChart);
        }
    }
    
    public void stop() throws FlowException {
        this.stepper.reset();
    }
    
    public void forceStop() {
        pause();
        if (this.stepThread != null && this.stepThread.isAlive()) {
            this.stepThread.interrupt();
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
        reloadStepper();
    }
    
    public void reloadStepper() {
        initStepper();
    }
}
