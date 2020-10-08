package org.jojo.flow.model.flowChart.connections;

import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.RigidPin;
import org.jojo.flow.model.storeLoad.ConnectionDOM;
import org.jojo.flow.model.storeLoad.DOM;

public class RigidConnection extends Connection {
    public RigidConnection(final int id, final RigidPin asFromPin, final RigidPin asToPin, final String name) throws ConnectionException {
        super(id, asFromPin.getOutputPin(), name);
        addToPin(asToPin.getInputPin());
    }

    @Override
    public String getInfo() {
        return getFromPin().getDefaultData() == null 
                ? "rigid connection without default data in from pin" : "default data in from pin: " + getFromPin().getDefaultData();
    }

    @Override
    protected boolean connectionMatchesPins() {
        return getFromPin().getModulePinImp() instanceof RigidPin 
                && getToPins().stream().allMatch(x -> x.getModulePinImp() instanceof RigidPin);
    }

    @Override
    protected boolean checkDataTypes() {
        return true; //no data type checking for rigid pins at the moment
    }

    @Override
    public GraphicalRepresentation getGraphicalRepresentation() {
        // TODO implement
        return null;
    }

    @Override
    public InternalConfig serializeInternalConfig() {
        return null; // no internal config exists
    }

    @Override
    public void restoreSerializedInternalConfig(InternalConfig internalConfig) {
        // no internal config exists
    }

    @Override
    public String serializeSimulationState() {
        // TODO implement
        return null;
    }

    @Override
    public void restoreSerializedSimulationState(String simulationState) {
        // TODO implement
    }

    @Override
    public DOM getDOM() {
        final ConnectionDOM dom = new ConnectionDOM();
        dom.setName(getName());
        dom.setID(getId());
        dom.setClassName(getClass().getName());
        dom.setGraphicalRepresentation(getGraphicalRepresentation());
        dom.setFromPin(getFromPin());
        dom.setToPins(getToPins());
        return dom;
    }

    @Override
    public void restoreFromDOM(DOM dom) {
        // TODO Auto-generated method stub
        
    }

}
