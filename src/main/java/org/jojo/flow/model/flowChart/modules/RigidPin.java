package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.api.IRigidPin;

public class RigidPin extends ModulePinImp implements IRigidPin {
    private final InputPin inputPin;
    private final OutputPin outputPin;
    
    public RigidPin(final FlowModule module, final RigidPinGR gr) {
        super(module, null);
        this.inputPin = new InputPin(this, gr);
        this.outputPin = new OutputPin(this, gr);
    }
    
    @Override
    public InputPin getInputPin() {
        return this.inputPin;
    }
    
    @Override
    public OutputPin getOutputPin() {
        return this.outputPin;
    }
    
    @Override
    public String toString() {
        return "RigidPin with defaultData= " + getDefaultData();
    }
}
