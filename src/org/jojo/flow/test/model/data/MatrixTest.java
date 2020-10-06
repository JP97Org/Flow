package org.jojo.flow.test.model.data;

import org.jojo.flow.model.data.BasicType;
import org.jojo.flow.model.data.IllegalUnitOperationException;
import org.jojo.flow.model.data.Matrix;
import org.jojo.flow.model.data.UnitSignature;
import org.junit.*;

public class MatrixTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final Matrix<Double> matrix = new Matrix<>(new Double[][] {{0.1, 0.2}, {0.3, 0.4}}, UnitSignature.FARAD);
        Assert.assertEquals(BasicType.DOUBLE, matrix.getBasicType());
        Assert.assertEquals("[[0.1, 0.2], [0.3, 0.4]] m^-2 * kg^-1 * s^4 * A^2", matrix.toString());
        
        Assert.assertArrayEquals(new Double[][] {{0.1, 0.2}, {0.3, 0.4}}, matrix.getMatrix().first);
        Assert.assertEquals(UnitSignature.FARAD, matrix.getMatrix().second);
    }
}
