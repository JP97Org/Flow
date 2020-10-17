package org.jojo.flow.model.api;

public interface IScalar<T extends Number> extends IBasicCheckable {
    public static <K extends Number> IScalar<K> getDefaultImplementation(final Unit<K> unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IScalar<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {Unit.class}, unit);
        return ret;
    }
    
    Unit<T> getScalar();
}
