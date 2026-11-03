package org.jojo.flow.test.model.data;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.data.StringDataSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class StringDataSetTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final StringDataSet s = new StringDataSet("Test 012");
        Assertions.assertEquals(null, s.getBasicType());
        Assertions.assertEquals("Test 012", s.toString());
        
        Assertions.assertEquals(null, s.getUnitSignature());
        Assertions.assertEquals("Test 012".length(), s.getSizes()[0]);
    }
}
