package org.jojo.flow.model.data;

abstract class BasicSignatureComponentSignature extends DataSignature {
    protected BasicSignatureComponentSignature(final int dataId) {
        super(dataId);
    }
    
    @Override
    public DataSignature getComponent(final int index) {
        return null;
    }
    
    @Override
    public int size() {
        return 0;
    }
}
