package org.jojo.flow.test.model.util;

import static org.jojo.flow.model.util.FactoryUtil.*;

import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.*;
import java.util.Map;

public class FactoryUtilTest {
    private Map<Class<?>, Class<?>> map;
    
    @Before
    public void setUp() {
        this.map = null;
    }
    
    @Test
    public void mapTest() {
        this.map = getApiToDefaultImplementationMap();
        Assert.assertTrue(this.map.isEmpty());
        initialize();
        Assert.assertTrue(this.map.isEmpty());
        this.map = getApiToDefaultImplementationMap();
        Assert.assertFalse(this.map.isEmpty());
        Assert.assertTrue(putDefaultImplementationMapping(IConnection.class, DefaultArrow.class));
        Assert.assertFalse(this.map.containsKey(IConnection.class));
        this.map = getApiToDefaultImplementationMap();
        Assert.assertTrue(this.map.containsKey(IConnection.class));
        final long before = this.map.values().stream().filter(c -> c.equals(MockModule.class)).count();
        Assert.assertFalse(putDefaultImplementationMapping(IConnection.class, MockModule.class));
        this.map = getApiToDefaultImplementationMap();
        Assert.assertEquals(before, this.map.values().stream().filter(c -> c.equals(MockModule.class)).count());
    }
}
