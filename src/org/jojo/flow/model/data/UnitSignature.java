package org.jojo.flow.model.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;

public final class UnitSignature implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -929295978318467353L;

    public static final int NUM_BASE_UNITS = 7;
    
    public static final UnitSignature NO_UNIT = new UnitSignature();
    public static final UnitSignature RADIAN = NO_UNIT;
    public static final UnitSignature SQUARE_RADIAN = NO_UNIT;
    
    public static final UnitSignature METER = getBase(0);
    public static final UnitSignature KILOGRAMM = getBase(1);
    public static final UnitSignature SECOND = getBase(2);
    public static final UnitSignature AMPERE = getBase(3);
    public static final UnitSignature KELVIN = getBase(4);
    public static final UnitSignature MOLE = getBase(5);
    public static final UnitSignature CANDELA = getBase(6);
    
    public static final UnitSignature SQUARE_SECOND = SECOND.multiply(SECOND);
    public static final UnitSignature CUBE_SECOND = SQUARE_SECOND.multiply(SECOND);
    public static final UnitSignature TESSERACT_SECOND = CUBE_SECOND.multiply(SECOND);
    public static final UnitSignature SQUARE_METER = METER.multiply(METER);
    public static final UnitSignature CUBE_METER = SQUARE_METER.multiply(METER);
    
    public static final UnitSignature NEWTON = KILOGRAMM.multiply(METER).divide(SQUARE_SECOND);
    public static final UnitSignature NEWTON_METER = NEWTON.multiply(METER);
    public static final UnitSignature PASCAL = NEWTON.divide(SQUARE_METER);
    public static final UnitSignature JOULE = NEWTON_METER;
    public static final UnitSignature WATT = JOULE.divide(SECOND);
    public static final UnitSignature VOLT = WATT.divide(AMPERE);
    public static final UnitSignature OHM = VOLT.divide(AMPERE);
    public static final UnitSignature SIEMENS = NO_UNIT.divide(OHM);
    public static final UnitSignature HENRY = OHM.multiply(SECOND);
    public static final UnitSignature TESLA = VOLT.multiply(SECOND).divide(SQUARE_SECOND);
    public static final UnitSignature COULOMB = AMPERE.multiply(SECOND);
    public static final UnitSignature FARAD = COULOMB.divide(VOLT);
    public static final UnitSignature HERTZ = NO_UNIT.divide(SECOND);
    public static final UnitSignature WEBER = HENRY.multiply(AMPERE);
    public static final UnitSignature SIEVERT = JOULE.divide(KILOGRAMM);
    public static final UnitSignature GRAY = SIEVERT;
    public static final UnitSignature LUX = CANDELA.multiply(SQUARE_RADIAN).divide(SQUARE_METER);
    public static final UnitSignature KATAL = MOLE.divide(SECOND);
    public static final UnitSignature RADIAN_PER_SECOND = RADIAN.divide(SECOND);
    public static final UnitSignature METER_PER_SECOND = METER.divide(SECOND);
    public static final UnitSignature METER_PER_SQUARE_SECOND = METER.divide(SQUARE_SECOND);
    public static final UnitSignature METER_PER_CUBE_SECOND = METER.divide(CUBE_SECOND);
    public static final UnitSignature METER_PER_TESSERACT_SECOND = METER.divide(TESSERACT_SECOND);
    
    public final int meter;
    public final int kilogramm;
    public final int second;
    public final int ampere;
    public final int kelvin;
    public final int mole;
    public final int candela;
    
    public UnitSignature() {
        this.meter = 0;
        this.kilogramm = 0;
        this.second = 0;
        this.ampere = 0;
        this.kelvin = 0;
        this.mole = 0;
        this.candela = 0;
    }
    
    public UnitSignature(final UnitSignature toCopy) {
        this(toCopy.getUnitSignatureArray());
    }
    
    public UnitSignature(final int... unitSignatureArray) throws IllegalArgumentException {
        if (unitSignatureArray.length != NUM_BASE_UNITS) {
            throw new IllegalArgumentException("there must be " + NUM_BASE_UNITS + " ints for a correct unit");
        }
        
        this.meter = unitSignatureArray[0];
        this.kilogramm = unitSignatureArray[1];
        this.second = unitSignatureArray[2];
        this.ampere = unitSignatureArray[3];
        this.kelvin = unitSignatureArray[4];
        this.mole = unitSignatureArray[5];
        this.candela = unitSignatureArray[6];
    }
    
    private static UnitSignature getBase(final int index) {
        final int[] arr = new int[NUM_BASE_UNITS];
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
    public int hashCode() {
        return Arrays.hashCode(getUnitSignatureArray());
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
                switch (i) {
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
    

    public static UnitSignature ofString(final String unitAsString) {
        if (unitAsString.equals("")) {
            return NO_UNIT;
        }
        final int arr[] = NO_UNIT.getUnitSignatureArray();
        final String[] split = unitAsString.split(" * ");
        boolean isErr = false;
        for (int i = 0; !isErr && i < split.length; i++) {
            final String baseUnitStr = split[i];
            final String toPowOf = Pattern.quote("^");
            try {
                if (baseUnitStr.matches(".*" + toPowOf + ".*")) {
                    final String[] buSplit = baseUnitStr.split(toPowOf);
                    final String buName = buSplit[0];
                    final int exp = Integer.parseInt(buSplit[1]);
                    switch (buName) {
                        case "m": arr[0] = exp; break;
                        case "kg": arr[1] = exp; break;
                        case "s": arr[2] = exp; break;
                        case "A": arr[3] = exp; break;
                        case "K": arr[4] = exp; break;
                        case "mol": arr[5] = exp; break;
                        case "cd": arr[6] = exp; break;
                        default: 
                            //TODO maybe warn
                            isErr = true;
                    }
                } else {
                    //TODO maybe warn
                    isErr = false;
                }
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                isErr = true;
            }
        }
        return isErr ? null : new UnitSignature(arr);
    }
    
    public String toPrettyString() {
        return toPrettyString(true, false);
    }
    
    public String toPrettyString(final boolean isEnergy, final boolean isRadian) {
        String ret = toString();
        if (ret.equals(NEWTON.toString())) {
            ret = "N";
        } else if (ret.equals(NEWTON_METER.toString())) {
            ret = isEnergy ? "J" : "Nm";
        } else if (ret.equals(PASCAL.toString())) {
            ret = "Pa";
        } else if (ret.equals(WATT.toString())) {
            ret = "W";
        } else if (ret.equals(VOLT.toString())) {
            ret = "V";
        } else if (ret.equals(OHM.toString())) {
            ret = "" + '\u2126'; // big omega
        } else if (ret.equals(SIEMENS.toString())) {
            ret = "A * V^-1";
        } else if (ret.equals(HENRY.toString())) {
            ret = "H";
        } else if (ret.equals(TESLA.toString())) {
            ret = "T";
        } else if (ret.equals(COULOMB.toString())) {
            ret = "C";
        } else if (ret.equals(FARAD.toString())) {
            ret = "F";
        } else if (ret.equals(HERTZ.toString())) {
            ret = isRadian ? "rad * s^-1" : "Hz";
        } else if (ret.equals(WEBER.toString())) {
            ret = "Wb";
        } else if (ret.equals(SIEVERT.toString())) {
            ret = "Sv";
        } else if (ret.equals(LUX.toString())) {
            ret = "lx";
        } else if (ret.equals(KATAL.toString())) {
            ret = "kat";
        } else if (ret.equals(NO_UNIT.toString())) {
            ret = isRadian ? "rad" : ret;
        }
        return ret;
    }
}

