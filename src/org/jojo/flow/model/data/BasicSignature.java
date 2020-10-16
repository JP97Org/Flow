package org.jojo.flow.model.data;

import java.util.Arrays;

import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.UnitSignature;

public class BasicSignature extends DataSignature {
    /**
     * 
     */
    private static final long serialVersionUID = -8430601521540368970L;
    private final BasicSignatureComponentSignature[] components;
    
    public BasicSignature(final BasicCheckable data) {
        super(data.getDataId());
        this.components = new BasicSignatureComponentSignature[BasicSignatureComponents.values().length];
        this.components[BasicSignatureComponents.BASIC_TYPE.index] = data.getBasicType() == null ? null : new BasicTypeDataSignature(data.getBasicType());
        this.components[BasicSignatureComponents.SIZES.index] = new SizesDataSignature(data.getSizes());
        this.components[BasicSignatureComponents.UNIT.index] = data.getUnitSignature() == null ? null : new UnitDataSignature(data.getUnitSignature()); 
    }
    
    private BasicSignature(final BasicSignature toCopy) {
        super(toCopy.getDataId());
        this.components = Arrays.stream(toCopy.components)
                .map(x -> x == null ? null : x.getCopy())
                .toArray(BasicSignatureComponentSignature[]::new);
    }
    
    private BasicSignature(final int dataId, final BasicSignatureComponentSignature[] components) {
        super(dataId);
        this.components = components;
    }

    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            if (!isChecking() || !((DataSignature)other).isChecking()) {
                return true;
            }
            return Arrays.equals(this.components, ((BasicSignature) other).components);
        }
        return false;
    }

    @Override
    public DataSignature getCopy() {
        return new BasicSignature(this);
    }

    @Override
    public DataSignature getComponent(int index) {
        return this.components[index];
    }

    @Override
    public int size() {
        return this.components.length;
    }

    @Override
    public String toString() {
        return toStringDs() + Arrays.toString(this.components);
    }
    
    @Override
    public DataSignature ofString(final String info) {
        //TODO schauen ob das so passt mit dem deaktivieren
        final String prepared = info.substring(1, info.length() - 1);
        final String[] elems = prepared.split(",\\s");
        final BasicSignatureComponentSignature[] componentsLocal = new BasicSignatureComponentSignature[elems.length];
        final boolean isSizesCheckingDeactivated = prepared.startsWith(new DontCareDataSignature().getNameOfDataId());
        prepareElems(elems);
        if (elems.length != BasicSignatureComponents.values().length) {
            return null;
        }
        componentsLocal[BasicSignatureComponents.BASIC_TYPE.index] = 
                (BasicSignatureComponentSignature) new BasicTypeDataSignature(BasicType.INT)
                    .ofString(elems[BasicSignatureComponents.BASIC_TYPE.index]);
        componentsLocal[BasicSignatureComponents.SIZES.index] = 
                (BasicSignatureComponentSignature) new SizesDataSignature(new int[] {NO_SIZES})
                    .ofString(elems[BasicSignatureComponents.SIZES.index]);
        componentsLocal[BasicSignatureComponents.UNIT.index] = 
                (BasicSignatureComponentSignature) new UnitDataSignature(UnitSignature.NO_UNIT)
                    .ofString(elems[BasicSignatureComponents.UNIT.index]); 
        if (isSizesCheckingDeactivated) {
            componentsLocal[BasicSignatureComponents.SIZES.index].deactivateChecking();
        }
        return new BasicSignature(getDataId(), componentsLocal);
    }
   
    private static void prepareElems(final String[] elems) {
        for(int i = 0; i < elems.length; i++) {
            elems[i] = elems[i].equals("null") ? "null" : elems[i].replaceFirst("[^|]*\\|\\s", "");
        }
    }
}
