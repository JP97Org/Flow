package org.jojo.flow.test.model.data;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.Matrix;
import org.junit.jupiter.api.*;

public class MatrixTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final Matrix<Double> matrix = new Matrix<>(new Double[][] {{0.1, 0.2}, {0.3, 0.4}}, UnitSignature.FARAD);
        Assertions.assertEquals(BasicType.DOUBLE, matrix.getBasicType());
        Assertions.assertEquals("[[0.1, 0.2], [0.3, 0.4]] m^-2 * kg^-1 * s^4 * A^2", matrix.toString());
        
        Assertions.assertArrayEquals(new Double[][] {{0.1, 0.2}, {0.3, 0.4}}, matrix.getMatrix().first);
        Assertions.assertEquals(UnitSignature.FARAD, matrix.getMatrix().second);
    }
}
