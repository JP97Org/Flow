package org.jojo.flow.test.model.data;

import java.util.Base64;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.data.RawDataSet;
import org.junit.jupiter.api.*;

public class RawDataSetTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final RawDataSet raw = new RawDataSet(new byte[] {0, 1, 2});
        Assertions.assertEquals(null, raw.getBasicType());
        Assertions.assertEquals(Base64.getEncoder().encodeToString(new byte[] {0, 1, 2}), raw.toString());
        
        Assertions.assertArrayEquals(new byte[] {0, 1, 2}, raw.getData());
        Assertions.assertEquals(null, raw.getUnitSignature());
        Assertions.assertEquals(3, raw.getSizes()[0]);
    }
}
