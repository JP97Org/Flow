package org.jojo.flow.test.model.data;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.Tensor;
import org.junit.jupiter.api.*;

public class TensorTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final Tensor<Double> tensor = new Tensor<>(new Double[][][] {{{0.1, 0.2}, {0.3, 0.4}}}, UnitSignature.FARAD);
        Assertions.assertEquals(BasicType.DOUBLE, tensor.getBasicType());
        Assertions.assertEquals("[[[0.1, 0.2], [0.3, 0.4]]] m^-2 * kg^-1 * s^4 * A^2", tensor.toString());
        
        Assertions.assertArrayEquals(new Double[][][] {{{0.1, 0.2}, {0.3, 0.4}}}, tensor.getTensor().first);
        Assertions.assertEquals(UnitSignature.FARAD, tensor.getTensor().second);
    }
}
