package org.jojo.flow.model.api;

/**
 * This interface represents a fraction, i.e. a number consisting of numerator and denominator.
 * Both of which are of type {@code long}.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IFraction extends IAPI {
    
    /**
     * Gets a fraction default implementation with the given value.
     * 
     * @param value - the given value
     * @return a fraction default implementation with the given value
     */
    public static IFraction getDefaultImplementation(final long value){
        return (IFraction) IAPI.defaultImplementationOfThisApi(new Class<?>[] {long.class}, value);
    }
    
    /**
     * Gets a fraction default implementation with the given value.
     * 
     * @param numerator - the numerator of the value
     * @param denominator - the denominator of the value (must not be 0)
     * @return a fraction default implementation with the given value
     */
    public static IFraction getDefaultImplementation(final long numerator, final long denominator){
        return (IFraction) IAPI.defaultImplementationOfThisApi(new Class<?>[] {long.class, long.class}, numerator, denominator);
    }
    
    /**
     * Gets a fraction default implementation representing the given value as a fraction.
     * 
     * @param value - the given value
     * @return a fraction default implementation representing the given value as a fraction
     */
    public static IFraction getDefaultImplementation(final double value){
        return (IFraction) IAPI.defaultImplementationOfThisApi(new Class<?>[] {double.class}, value);
    }
    
    /**
     * Gets a fraction default implementation representing the given value cut at the given
     * decimal place as a fraction. The decimal places are treated like this:
     * {@code String.format(Locale.US, "%." + decimalPlaces + "f", value);}, 
     * then the returned value is parsed as a {@code double} and transformed to a fraction 
     * like in {@link #getDefaultImplementation(double)}.
     * 
     * @param value - the given value
     * @param decimalPlaces - the given decimal place
     * @return Gets a fraction default implementation representing the given value as a fraction
     * @see String#format(String, Object...)
     */
    public static IFraction getDefaultImplementation(final double value, final int decimalPlaces){
        return (IFraction) IAPI.defaultImplementationOfThisApi(new Class<?>[] {double.class, int.class}, value, decimalPlaces);
    }
    
    /**
     * Gets the numerator.
     * 
     * @return the numerator
     */
    long getNumerator();
    
    /**
     * Gets the denominator.
     * 
     * @return the denominator (cannot be 0)
     */
    public long getDenominator();
    
    /**
     * Gets the int value next to this fraction.
     * 
     * @return {@code (int)longValue()}
     * @see #longValue()
     */
    int intValue();

    /**
     * Gets the long value next to this fraction.
     * 
     * @return {@code Math.round(doubleValue())}
     * @see #doubleValue()
     * @see Math#round(double)
     */
    long longValue();

    /**
     * Gets the float value next to this fraction.
     * 
     * @return {@code (float)doubleValue()}
     * @see #doubleValue()
     */
    float floatValue();

    /**
     * Gets the double value next to this fraction.
     * 
     * @return {@code (double)getNumerator() / (double)getDenominator()}
     * @see #getNumerator()
     * @see #getDenominator()
     */
    double doubleValue();

    /**
     * Adds another fraction.
     * 
     * @param other - the other fraction
     * @return the sum of this fraction and the other one
     */
    public IFraction add(final IFraction other);
    
    /**
     * Subtracts another fraction.
     * 
     * @param other - the other fraction
     * @return the difference of this fraction and the other one
     */
    public IFraction subtract(final IFraction other);
    
    /**
     * Multiplies another fraction.
     * 
     * @param other - the other fraction
     * @return the product of this fraction and the other one
     */
    public IFraction multiply(final IFraction other);
    
    /**
     * Divides another fraction.
     * 
     * @param other - the other fraction (must not be equal to {@code (new Fraction(0L))})
     * @return the division result of this fraction and the other one
     */
    public IFraction divide(final IFraction other);
    
    /**
     * Cancels.
     * 
     * @return the cancelled fraction
     */
    public IFraction cancel();
    
    
    /**
     * Gets the string representation using this format: {@code getNumerator() + " / " + getDenominator();}.
     * 
     * @return the string representation using this format: {@code getNumerator() + " / " + getDenominator();}
     */
    @Override
    String toString();
}
