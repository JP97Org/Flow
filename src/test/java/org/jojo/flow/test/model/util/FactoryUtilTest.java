package org.jojo.flow.test.model.util;

import static org.jojo.flow.model.util.FactoryUtil.*;

import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.jupiter.api.*;
import java.util.Map;

public class FactoryUtilTest {
    private Map<Class<?>, Class<?>> map;
    
    @BeforeEach
    public void setUp() {
        this.map = null;
    }
    
    @Test
    public void mapTest() {
        this.map = getApiToDefaultImplementationMap();
        Assertions.assertTrue(this.map.isEmpty());
        initialize();
        Assertions.assertTrue(this.map.isEmpty());
        this.map = getApiToDefaultImplementationMap();
        Assertions.assertFalse(this.map.isEmpty());
        Assertions.assertTrue(putDefaultImplementationMapping(IConnection.class, DefaultArrow.class));
        Assertions.assertFalse(this.map.containsKey(IConnection.class));
        this.map = getApiToDefaultImplementationMap();
        Assertions.assertTrue(this.map.containsKey(IConnection.class));
        final long before = this.map.values().stream().filter(c -> c.equals(MockModule.class)).count();
        Assertions.assertFalse(putDefaultImplementationMapping(IConnection.class, MockModule.class));
        this.map = getApiToDefaultImplementationMap();
        Assertions.assertEquals(before, this.map.values().stream().filter(c -> c.equals(MockModule.class)).count());
    }
}
