package org.jojo.flow.model.api;

/**
 * This interface represents a scalar, i.e. a numeric value with a unit. 
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T>
 */
public interface IScalar<T extends Number> extends IBasicCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param <K> - the generic numeric type
     * @param unit - the unit (value and unit signature)
     * @return the default implementation
     */
    public static <K extends Number> IScalar<K> getDefaultImplementation(final Unit<K> unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IScalar<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {Unit.class}, unit);
        return ret;
    }
    
    /**
     * Gets the scalar.
     * 
     * @return the scalar
     */
    Unit<T> getScalar();
}
