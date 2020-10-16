package org.jojo.flow.api;

public interface IFraction extends IAPI {
    public static IFraction getDefaultImplementation(final long value){
        return (IFraction) IAPI.defaultImplementationOfThisApi(new Class<?>[] {long.class}, value);
    }
    
    public static IFraction getDefaultImplementation(final long numerator, final long denominator){
        return (IFraction) IAPI.defaultImplementationOfThisApi(new Class<?>[] {long.class, long.class}, numerator, denominator);
    }
    
    public static IFraction getDefaultImplementation(final double value){
        return (IFraction) IAPI.defaultImplementationOfThisApi(new Class<?>[] {double.class}, value);
    }
    
    public static IFraction getDefaultImplementation(final double value, final int decimalPlaces){
        return (IFraction) IAPI.defaultImplementationOfThisApi(new Class<?>[] {double.class, int.class}, value, decimalPlaces);
    }
    
    long getNumerator();
    
    public long getDenominator();
    
    int intValue();

    long longValue();

    float floatValue();

    double doubleValue();

    public IFraction add(final IFraction other);
    
    public IFraction subtract(final IFraction other);
    
    public IFraction multiply(final IFraction other);
    
    public IFraction divide(final IFraction other);
    
    @Override
    String toString();
}
