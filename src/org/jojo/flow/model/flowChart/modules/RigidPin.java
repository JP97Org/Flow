package org.jojo.flow.model.flowChart.modules;

public class RigidPin extends ModulePinImp {
    private final InputPin inputPin;
    private final OutputPin outputPin;
    
    public RigidPin(final FlowModule module) {
        super(module, null);
        this.inputPin = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }
    
    public InputPin getInputPin() {
        return this.inputPin;
    }
    
    public OutputPin getOutputPin() {
        return this.outputPin;
    }
}
