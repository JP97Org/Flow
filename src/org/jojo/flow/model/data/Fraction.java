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
        this(value, 1L);
    }
    
    public Fraction(final long numerator, final long denominator) throws IllegalArgumentException{
        this.numerator = numerator;
        this.denominator = denominator;
        if (denominator == 0) {
            throw new IllegalArgumentException("denominator must not be 0");
        }
    }
    
    public Fraction(final double value) {
        this(value, "%f");
    }
    
    public Fraction(final double value, final int decimalPlaces) {
        this(value, "%." + decimalPlaces + "f");
    }
    
    private Fraction(final double value, final String formatString) {
        final String valStr = String.format(formatString, value);
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
        long signum = 1L;
        if (numeratorStr.toString().startsWith("-") || numeratorStr.toString().startsWith("+")) {
            signum = numeratorStr.toString().startsWith("-") ? -1L : 1L;
            numeratorStr.deleteCharAt(0);
        }
        this.numerator = signum * Long.parseLong(toLongString(numeratorStr));
        this.denominator = pow(10, pow);
    }

    private static long pow(long base, long pow) {
        int ret = 1;
        for (long i = 0; i < base; i++) {
            ret *= base;
        }
        return ret;
    }
    
    private static String toLongString(final StringBuilder builder) {
        final String longMax = Long.valueOf(Long.MAX_VALUE).toString();
        final int maxLen = longMax.length();
        if (builder.length() >= maxLen) {
            if (builder.length() == maxLen) {
                final int maxFirstDigit = Integer.parseInt("" + longMax.charAt(0));
                final int builderFirstDigit = Integer.parseInt("" + builder.charAt(0));
                if (builderFirstDigit >= maxFirstDigit) {
                    final String builderStr = builder.toString();
                    builder.delete(0, maxLen);
                    long longValue;
                    try {
                        longValue = Long.parseLong(builderStr);
                    } catch (NumberFormatException e) {
                        longValue = Long.MAX_VALUE;
                    }
                    builder.append(longValue);
                }
            } else {
                final String subStr = builder.substring(0, maxLen);
                builder.delete(0, builder.length());
                builder.append(toLongString(new StringBuilder(subStr))); 
            }
        }
        return builder.toString();
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
        final long kgv = kgv(this.denominator, other.denominator);
        return new Fraction((this.numerator * kgv) / this.denominator 
                + (other.numerator * kgv) / other.denominator, kgv);
    }
    
    private static long kgv(long a, long b) {
        return Math.abs(a * b) / ggt(a, b); 
    }
    
    private static long ggt(final long aArg, final long bArg) {
        long signum = (long)Math.signum((double)aArg * bArg);
        signum = signum != 0 ? signum : (aArg != 0 ? (long)Math.signum((double)aArg) : (long)Math.signum((double)bArg));
        long a = Math.abs(aArg) < 0 ? Long.MAX_VALUE : Math.abs(aArg);
        long b = Math.abs(bArg) < 0 ? Long.MAX_VALUE : Math.abs(bArg);
        while (b != 0) {
            final long h = a % b;
            a = b;
            b = h;
        }
        return signum * a;
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
            return (this.numerator == frac.numerator && this.denominator == frac.denominator);
        }
        return false;
    }
}
