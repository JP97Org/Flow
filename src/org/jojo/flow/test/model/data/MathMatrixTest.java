package org.jojo.flow.test.model.data;

import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.IllegalUnitOperationException;
import org.jojo.flow.model.data.MathMatrix;
import org.junit.*;

public class MathMatrixTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final MathMatrix<Double> matrix = new MathMatrix<>(new Double[][] {{0.1, 0.2}, {0.3, 0.4}}, UnitSignature.FARAD);
        Assert.assertEquals(BasicType.DOUBLE, matrix.getBasicType());
        Assert.assertEquals("[[0.1, 0.2], [0.3, 0.4]] m^-2 * kg^-1 * s^4 * A^2", matrix.toString());
        
        Assert.assertArrayEquals(new Double[][] {{0.1, 0.2}, {0.3, 0.4}}, matrix.getMatrix().first);
        Assert.assertEquals(UnitSignature.FARAD, matrix.getMatrix().second);
        
        Assert.assertEquals("[[0.2, 0.4], [0.6, 0.8]] m^-2 * kg^-1 * s^4 * A^2", matrix.add(matrix).toString());
        Assert.assertEquals("[[0.5, 1.0], [1.5, 2.0]] m^-2 * kg^-1 * s^4 * A^2", matrix.multiply(Unit.getDoubleConstant(5)).toString());
        Assert.assertTrue(matrix.multiply(matrix).toString().matches("\\[\\[0\\.07, 0.100000.*\\], \\[0\\.15, 0\\.2200000.*\\]\\] m\\^-4 \\* kg\\^-2 \\* s\\^8 \\* A\\^4"));
        Assert.assertEquals(0.02, matrix.determinant(), 1E-6);
    }
}
