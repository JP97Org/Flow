package org.jojo.flow.test.model.data;

import java.util.Base64;
import org.jojo.flow.model.data.IllegalUnitOperationException;
import org.jojo.flow.model.data.RawDataSet;
import org.junit.*;

public class RawDataSetTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final RawDataSet raw = new RawDataSet(new byte[] {0, 1, 2});
        Assert.assertEquals(null, raw.getBasicType());
        Assert.assertEquals(Base64.getEncoder().encodeToString(new byte[] {0, 1, 2}), raw.toString());
        
        Assert.assertArrayEquals(new byte[] {0, 1, 2}, raw.getData());
        Assert.assertEquals(null, raw.getUnitSignature());
        Assert.assertEquals(3, raw.getSizes()[0]);
    }
}
