package org.jojo.flow.model.api;

public interface ITensor<T> extends IBasicCheckable {
    public static <K> ITensor<K> getDefaultImplementation(final K[][][] tensor, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (ITensor<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {Object[][][].class, UnitSignature.class}, tensor, unit);
        return ret;
    }
    
    Pair<T[][][], UnitSignature> getTensor();
}
