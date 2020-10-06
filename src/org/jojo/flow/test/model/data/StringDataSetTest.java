package org.jojo.flow.test.model.data;

import org.jojo.flow.model.data.IllegalUnitOperationException;
import org.jojo.flow.model.data.StringDataSet;
import org.junit.Test;
import org.junit.Assert;

public class StringDataSetTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final StringDataSet s = new StringDataSet("Test 012");
        Assert.assertEquals(null, s.getBasicType());
        Assert.assertEquals("Test 012", s.toString());
        
        Assert.assertEquals(null, s.getUnitSignature());
        Assert.assertEquals("Test 012".length(), s.getSizes()[0]);
    }
}
