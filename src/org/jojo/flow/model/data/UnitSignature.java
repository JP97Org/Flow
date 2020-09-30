package org.jojo.flow.model.data;

import java.util.Arrays;

public final class UnitSignature {
    public static final UnitSignature NO_UNIT = new UnitSignature();
    public static final UnitSignature METER = getBase(0);
    public static final UnitSignature KILOGRAMM = getBase(1);
    public static final UnitSignature SECOND = getBase(2);
    public static final UnitSignature AMPERE = getBase(3);
    public static final UnitSignature KELVIN = getBase(4);
    public static final UnitSignature MOLE = getBase(5);
    public static final UnitSignature CANDELA = getBase(6);
    
    public final int meter;
    public final int kilogramm;
    public final int second;
    public final int ampere;
    public final int kelvin;
    public final int mole;
    public final int candela;
    
    public UnitSignature() {
        meter = 0;
        kilogramm = 0;
        second = 0;
        ampere = 0;
        kelvin = 0;
        mole = 0;
        candela = 0;
    }
    
    public UnitSignature(final UnitSignature toCopy) {
        this(toCopy.getUnitSignatureArray());
    }
    
    public UnitSignature(final int... unitSignatureArray) throws IllegalArgumentException {
        if (unitSignatureArray.length != 7) {
            throw new IllegalArgumentException("there must be 7 ints for a correct unit");
        }
        
        meter = unitSignatureArray[0];
        kilogramm = unitSignatureArray[1];
        second = unitSignatureArray[2];
        ampere = unitSignatureArray[3];
        kelvin = unitSignatureArray[4];
        mole = unitSignatureArray[5];
        candela = unitSignatureArray[6];
    }
    
    private static UnitSignature getBase(final int index) {
        final int[] arr = new int[7];
        for (int i = 0; i < arr.length; i++) {
            if (i == index) {
                arr[i] = 1;
            } else {
                arr[i] = 0;
            }
        }
        return new UnitSignature(arr);
    }
    
    public UnitSignature add(final UnitSignature other) throws IllegalUnitOperationException {
        if (this.equals(other)) {
            return this;
        } else {
            throw new IllegalUnitOperationException(this + " is not equal to " + other);
        }
    }
    
    public UnitSignature subtract(final UnitSignature other) throws IllegalUnitOperationException {
        return add(other);
    }
    
    public UnitSignature multiply(UnitSignature other) {
        return new UnitSignature(addEach(other.getUnitSignatureArray()));
    }
    
    private int[] addEach(final int[] unitSignatureArray) {
        final int[] ret = getUnitSignatureArray();
        for (int i = 0; i < ret.length; i++) {
            ret[i] += unitSignatureArray[i];
        }
        return ret;
    }
    
    public UnitSignature divide(UnitSignature other) {
        return new UnitSignature(subtractEach(other.getUnitSignatureArray()));
    }
    
    private int[] subtractEach(final int[] unitSignatureArray) {
        final int[] ret = getUnitSignatureArray();
        for (int i = 0; i < ret.length; i++) {
            ret[i] -= unitSignatureArray[i];
        }
        return ret;
    }

    public int[] getUnitSignatureArray() {
        return new int[] {meter, kilogramm, second, ampere, kelvin, mole, candela};
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof UnitSignature) {
            final UnitSignature otherSig = (UnitSignature) other;
            return Arrays.equals(getUnitSignatureArray(), otherSig.getUnitSignatureArray());
        }
        return false;
    }
    
    @Override
    public String toString() {
        String ret = "";
        final int[] arr = getUnitSignatureArray();
        for (int i = 0; i < arr.length; i++) {
            final int exp = arr[i];
            if (exp != 0) {
                switch(i) {
                    case 0: ret += "m"; break;
                    case 1: ret += "kg"; break;
                    case 2: ret += "s"; break;
                    case 3: ret += "A"; break;
                    case 4: ret += "K"; break;
                    case 5: ret += "mol"; break;
                    case 6: ret += "cd"; break;
                    default: assert false;
                }
                ret += "^" + exp + " * ";
            }
        }
        return ret.endsWith(" * ") ? ret.substring(0, ret.length() - 3) : ret;
    }
}

