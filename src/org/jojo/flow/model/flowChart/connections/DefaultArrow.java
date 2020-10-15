package org.jojo.flow.model.flowChart.connections;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.awt.Shape;
import java.io.IOException;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;
import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;

import static org.jojo.flow.model.storeLoad.OK.ok;

public class DefaultArrow extends Connection {
    private DataSignature dataType;
    private Data data;
    private GraphicalRepresentation gr;
    
    public DefaultArrow(final int id, final OutputPin fromPin, final InputPin toPin, final String name) throws ConnectionException {
        super(id, fromPin, name);
        this.dataType = ((DefaultPin)fromPin.getModulePinImp()).getCheckDataSignature();
        this.data = null;
        addToPin(toPin);
        this.gr = new DefaultArrowGR((DefaultOutputPinGR)(fromPin.getGraphicalRepresentation()), 
                (DefaultInputPinGR)(toPin.getGraphicalRepresentation()), 
                (Shape)null); //TODO get arrow shape
    }
    
    public Data getData() {
        return this.data;
    }
    
    public boolean putData(final Data data) {
        if (data == null || data.hasSameType(this.dataType)) {
            this.data = data;
            notifyObservers(data);
            return true;
        }
        return false;
    }
    
    public DataSignature getDataSignature() {
        return this.dataType;
    }
    
    /**
     * Sets a data signature to the arrow which matches the data signature at the moment,
     * but must be completely checking.
     * @param signature - the given data signature which must be recursively checking
     * @return whether putting the new data signature was successful
     */
    public boolean putDataSignature(final DataSignature signature) {
        if (this.dataType.equals(Objects.requireNonNull(signature)) && signature.isCheckingRecursive()) {
            this.dataType = signature;
            notifyObservers(signature);
            return true;
        }
        return false;
    }

    @Override
    public String getInfo() {
        return this.data == null ? "" + this.dataType : "" + this.data;
    }

    @Override
    protected boolean connectionMatchesPins() {
        return getFromPin().getModulePinImp() instanceof DefaultPin 
                && getToPins().stream().allMatch(x -> x.getModulePinImp() instanceof DefaultPin);
    }

    @Override
    protected boolean checkDataTypes() {
        if (connectionMatchesPins()) {
            final DefaultPin fromImp = (DefaultPin) getFromPin().getModulePinImp();
            return getToPins()
                    .stream()
                    .allMatch(x -> fromImp.getCheckDataSignature()
                            .equals(((DefaultPin)x.getModulePinImp()).getCheckDataSignature()));
        }
        return false;
    }

    @Override
    public GraphicalRepresentation getGraphicalRepresentation() {
        return this.gr;
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
        final DOM dom = super.getDOM();
        dom.appendString("dataType", this.dataType.toString());
        try {
            dom.appendString("data", this.data == null ? "null" : this.data.toSerializedString());
        } catch (ClassNotFoundException | IOException e) {
            // should not happen
            e.printStackTrace(); //TODO maybe warn
        }
        return dom;
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM dataTypeDom = (DOM)domMap.get("dataType");
            final String dataTypeStr = dataTypeDom.elemGet();
            this.dataType = DataSignature.of(dataTypeStr);
            final DOM dataDom = (DOM)domMap.get("data");
            final String dataStr = dataDom.elemGet();
            try {
                this.data = dataStr.equals("null") ? null : Data.ofSerializedString(dataStr);
            } catch (ClassNotFoundException | IOException e) {
                // should not happen
                e.printStackTrace();
            }
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "Connection " + OK.ERR_MSG_DOM_NOT_VALID);
            final Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get("dataType") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM dataTypeDom = (DOM)domMap.get("dataType");
            final String dataTypeStr = dataTypeDom.elemGet();
            ok(dataTypeStr != null, OK.ERR_MSG_NULL);
            ok(s -> DataSignature.of(s), dataTypeStr);
            ok(domMap.get("data") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM dataDom = (DOM)domMap.get("data");
            final String dataStr = dataDom.elemGet();
            ok(dataStr != null, OK.ERR_MSG_NULL);
            final Exception exc = ok(s -> {
                    try {
                        @SuppressWarnings("unused") // only possible object creation is checked
                        final Data data = s.equals("null") ? null : Data.ofSerializedString(s);
                        return null;
                    } catch (ClassNotFoundException | IOException e) {
                        return e;
                    }
                }, dataStr);
            if (exc != null) {
                throw new ParsingException(new Warning(null, exc.toString(), true));
            }
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(this).reportWarning();
            return false;
        }
    }

    @Override
    public String toString() {
        return "ID= " + getId() + " | " + "DefaultArrow from \"" + (getFromPin().getModule() != null ? getFromPin().getModule().getId() : "null") 
                + "\" to \"" + getToPins().stream()
                            .map(p -> p.getModule() != null ? p.getModule().getId() : "null")
                            .collect(Collectors.toList()) + "\"";
    }
}
