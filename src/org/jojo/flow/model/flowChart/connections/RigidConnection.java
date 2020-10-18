package org.jojo.flow.model.flowChart.connections;

import java.util.List;
import java.util.stream.Collectors;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.api.IInternalConfig;
import org.jojo.flow.model.api.IRigidConnection;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.ListSizeException;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.RigidPin;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;

public class RigidConnection extends Connection implements IRigidConnection {
    private GraphicalRepresentation gr;
    
    public RigidConnection(final int id, final RigidPin asFromPin, final RigidPin asToPin, final String name) throws ConnectionException {
        super(id, asFromPin.getOutputPin(), name);
        addToPin(asToPin.getInputPin());
        this.gr = new RigidConnectionGR((RigidPinGR) asFromPin.getOutputPin().getGraphicalRepresentation(), (RigidPinGR)asToPin.getInputPin().getGraphicalRepresentation());
    }
    
    @Override
    public boolean connect() {
        boolean ret = super.connect();
        if (ret) {
            // adding reverse connection
            try {
                final InputPin fromInPin = ((RigidPin)getFromPin().getModulePinImp()).getInputPin();
                final List<OutputPin> toOutPins = getToPins()
                        .stream()
                        .map(p -> ((RigidPin)p.getModulePinImp()).getOutputPin())
                        .collect(Collectors.toList());
                for (final var out : toOutPins) {
                    if (!out.getConnections().contains(this)) {
                        ret &= out.addConnection(this);
                    }
                }
                if (!fromInPin.getConnections().isEmpty()) {
                    return ret && fromInPin.getConnections().get(0).equals(this);
                }
                
                ret &= fromInPin.addConnection(this);
            } catch (ListSizeException e) {
                // should not happen
                disconnect();
                new Warning(this, e.toString(), true).reportWarning();
                return false;
            }
        }
        return ret;
    }
    
    public void disconnect() {
        super.disconnect();
        final InputPin fromInPin = ((RigidPin)getFromPin().getModulePinImp()).getInputPin();
        final List<OutputPin> toOutPins = getToPins()
                .stream()
                .map(p -> ((RigidPin)p.getModulePinImp()).getOutputPin())
                .collect(Collectors.toList());
        
        fromInPin.removeConnection(this);
        for (final var out : toOutPins) {
            out.removeConnection(this);
        }
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
        return true; // no data type checking for rigid pins at the moment
    }

    @Override
    public GraphicalRepresentation getGraphicalRepresentation() {
        return this.gr;
    }

    @Override
    public IInternalConfig serializeInternalConfig() {
        return null; // no internal config exists
    }

    @Override
    public void restoreSerializedInternalConfig(IInternalConfig internalConfig) {
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
    public String toString() {
        return "ID= " + getId() + " | " + "RigidConnection from \"" + (getFromPin().getModule() != null ? getFromPin().getModule().getId() : "null") 
                + "\" to \"" + getToPins().stream()
                            .map(p -> p.getModule() != null ? p.getModule().getId() : "null")
                            .collect(Collectors.toList()) + "\"";
    }
}
