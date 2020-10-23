package org.jojo.flow.test.model.data;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.ScalarDataSet;
import org.junit.*;

public class ScalarDataSetTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final ScalarDataSet<Double> scalar = new ScalarDataSet<>(0.01525, UnitSignature.FARAD);
        Assert.assertEquals(BasicType.DOUBLE, scalar.getBasicType());
        Assert.assertEquals("0.01525 m^-2 * kg^-1 * s^4 * A^2", scalar.toString());
        
        Assert.assertEquals(0.01525, scalar.getScalar().value.doubleValue(), 0);
        Assert.assertEquals(UnitSignature.FARAD, scalar.getUnitSignature());
        
        final ScalarDataSet<Double> scalar2 = new ScalarDataSet<Double>(Unit.getDoubleConstant(15.25)
                .multiply(Unit.MILLI
                        .multiply(UnitSignature.FARAD)
                        .toDoubleUnit()));
        Assert.assertEquals(BasicType.DOUBLE, scalar2.getBasicType());
        Assert.assertEquals("0.01525 m^-2 * kg^-1 * s^4 * A^2", scalar2.toString());
        
        Assert.assertEquals(0.01525, scalar2.getScalar().value.doubleValue(), 0);
        Assert.assertEquals(UnitSignature.FARAD, scalar2.getUnitSignature());
        
        Assert.assertTrue(scalar.equals(scalar2));
    }
}
