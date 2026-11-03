package org.jojo.flow.test.model.util;

import static org.jojo.flow.model.util.OK.*;

import org.jojo.flow.exc.ParsingException;
import org.junit.jupiter.api.*;

public class OKTest {
   
    @BeforeEach
    public void setUp() {
        
    }
    
    @Test
    public void okTest() throws ParsingException {
        Assertions.assertTrue(ok(true, ERR_MSG_NULL));
        Assertions.assertTrue(ok(x -> true, "").booleanValue());
        Assertions.assertFalse(ok(x -> false, "").booleanValue());
    }
    
    @Test
    public void notOkTestOne() {
        Assertions.assertThrows(ParsingException.class, () -> ok(false, ERR_MSG_NULL));
    }
    
    @Test
    public void notOkTestTwo() {
        Assertions.assertThrows(ParsingException.class, () -> ok(x -> Integer.parseInt(x), "ABC"));
    }
}
