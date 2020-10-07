package org.jojo.flow.model.data;

import java.util.Objects;

public class Fraction extends Number {
    /**
     * 
     */
    private static final long serialVersionUID = -3779771532811597900L;
    private final long numerator;
    private final long denominator;
    
    public Fraction(final long value) {
        this(value, 1);
    }
    
    public Fraction(final long numerator, final long denominator) throws IllegalArgumentException{
        this.numerator = numerator;
        this.denominator = denominator;
        if (denominator == 0) {
            throw new IllegalArgumentException("denominator must not be 0");
        }
    }
    
    public Fraction(final double value) {
        final String valStr = String.format("%f", value);
        StringBuilder numeratorStr = new StringBuilder();
        boolean beforePoint = true;
        int pow = 0;
        for (final char c : valStr.toCharArray()) {
            if (c != '.') {
                numeratorStr.append(c);
                if (!beforePoint) {
                    pow++;
                }
            } else {
                beforePoint = false;
            }
        }
        
        this.numerator = Long.parseLong(numeratorStr.toString());
        this.denominator = pow(10, pow);
    }

    private long pow(long base, long pow) {
        int ret = 1;
        for (long i = 0; i < base; i++) {
            ret *= base;
        }
        return ret;
    }

    public long getNumerator() {
        return this.numerator;
    }
    
    public long getDenominator() {
        return this.denominator;
    }
    
    @Override
    public int intValue() {
        return (int) longValue();
    }

    @Override
    public long longValue() {
        return Math.round(doubleValue());
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        return (double)this.numerator / (double)this.denominator;
    }

    public Fraction add(final Fraction other) {
        final long kgv = this.denominator * other.denominator; //TODO find better kgv
        return new Fraction((this.numerator * kgv) / this.denominator 
                + (other.numerator * kgv) / other.denominator, kgv);
    }
    
    public Fraction subtract(final Fraction other) {
        return add(new Fraction((-1) * other.getNumerator(), getDenominator()));
    }
    
    public Fraction multiply(final Fraction other) {
        return new Fraction(this.numerator * other.numerator, this.denominator * other.denominator);
    }
    
    public Fraction divide(final Fraction other) {
        return multiply(new Fraction(other.denominator, other.numerator));
    }
    
    public int hashCode() {
        return Objects.hash(this.numerator, this.denominator);
    }
    
    public boolean equals(final Object other) {
        if (other != null && other instanceof Fraction) {
            final Fraction frac = (Fraction)other;
            return (this.numerator == frac.numerator && this.denominator == frac.denominator)
                        || String.format("%.9f", Double.valueOf(doubleValue())).equals(
                                String.format("%.9f", Double.valueOf(frac.doubleValue())));
        }
        return false;
    }
}
