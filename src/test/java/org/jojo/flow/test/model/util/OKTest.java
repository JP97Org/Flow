package org.jojo.flow.test.model.util;

import static org.jojo.flow.model.util.OK.*;

import org.jojo.flow.exc.ParsingException;
import org.junit.*;

public class OKTest {
   
    @Before
    public void setUp() {
        
    }
    
    @Test
    public void okTest() throws ParsingException {
        Assert.assertTrue(ok(true, ERR_MSG_NULL));
        Assert.assertTrue(ok(x -> true, ""));
        Assert.assertFalse(ok(x -> false, ""));
    }
    
    @Test(expected=ParsingException.class)
    public void notOkTestOne() throws ParsingException {
        ok(false, ERR_MSG_NULL);
    }
    
    @Test(expected=ParsingException.class)
    public void notOkTestTwo() throws ParsingException {
        ok(x -> Integer.parseInt(x), "ABC");
    }
}
