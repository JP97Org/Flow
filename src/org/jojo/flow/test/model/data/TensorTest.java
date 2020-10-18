package org.jojo.flow.test.model.data;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.Tensor;
import org.junit.*;

public class TensorTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final Tensor<Double> tensor = new Tensor<>(new Double[][][] {{{0.1, 0.2}, {0.3, 0.4}}}, UnitSignature.FARAD);
        Assert.assertEquals(BasicType.DOUBLE, tensor.getBasicType());
        Assert.assertEquals("[[[0.1, 0.2], [0.3, 0.4]]] m^-2 * kg^-1 * s^4 * A^2", tensor.toString());
        
        Assert.assertArrayEquals(new Double[][][] {{{0.1, 0.2}, {0.3, 0.4}}}, tensor.getTensor().first);
        Assert.assertEquals(UnitSignature.FARAD, tensor.getTensor().second);
    }
}
