package org.jojo.flow.test.model.data;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.ScalarDataSet;
import org.junit.jupiter.api.*;

public class ScalarDataSetTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final ScalarDataSet<Double> scalar = new ScalarDataSet<>(0.01525, UnitSignature.FARAD);
        Assertions.assertEquals(BasicType.DOUBLE, scalar.getBasicType());
        Assertions.assertEquals("0.01525 m^-2 * kg^-1 * s^4 * A^2", scalar.toString());
        
        Assertions.assertEquals(0.01525, scalar.getScalar().value.doubleValue(), 0);
        Assertions.assertEquals(UnitSignature.FARAD, scalar.getUnitSignature());
        
        final ScalarDataSet<Double> scalar2 = new ScalarDataSet<Double>(Unit.getDoubleConstant(15.25)
                .multiply(Unit.MILLI
                        .multiply(UnitSignature.FARAD)
                        .toDoubleUnit()));
        Assertions.assertEquals(BasicType.DOUBLE, scalar2.getBasicType());
        Assertions.assertEquals("0.01525 m^-2 * kg^-1 * s^4 * A^2", scalar2.toString());
        
        Assertions.assertEquals(0.01525, scalar2.getScalar().value.doubleValue(), 0);
        Assertions.assertEquals(UnitSignature.FARAD, scalar2.getUnitSignature());
        
        Assertions.assertTrue(scalar.equals(scalar2));
    }
}
