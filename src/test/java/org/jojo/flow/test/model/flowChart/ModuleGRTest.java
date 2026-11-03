package org.jojo.flow.test.model.flowChart;

import java.util.Arrays;

import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IModuleGR;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.junit.jupiter.api.*;

public class ModuleGRTest {
    private IFlowModule mock;
    private IModuleGR gr;

    @BeforeEach
    public void setUp() {
        this.mock = DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
        this.gr = (IModuleGR) mock.getGraphicalRepresentation();
    }

    @Test
    public void get() {
        Assertions.assertTrue(mock.getGraphicalRepresentation() == gr);
        Assertions.assertTrue(gr.getModule() == mock);
        Assertions.assertEquals(4, gr.getCorners().length);
        Assertions.assertTrue(Arrays.stream(gr.getCorners()).allMatch(c -> c != null));
        Assertions.assertTrue(gr.getInfoText() != null);
        Assertions.assertTrue(gr.getScale() == 1.);
        Assertions.assertTrue(gr.isDOMValid(gr.getDOM()));
    }
    
    @Test
    public void scale() {
        Assertions.assertTrue(gr.getScale() == 1.);
        gr.setScale(2.);
        Assertions.assertTrue(gr.getScale() == 2.);
    }
    
    @Test
    public void scaleExc() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> gr.setScale(-5.));
    }

    @Test
    public void rotate() {
        final IFlowModule mockTwo = DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 200);
        final IModuleGR grTwo = (IModuleGR) mockTwo.getGraphicalRepresentation();
        Assertions.assertTrue(gr != grTwo);
        Assertions.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assertions.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assertions.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assertions.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        gr.rotateLeft();
        gr.rotateLeft();
        gr.rotateLeft();
        gr.rotateLeft();
        Assertions.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assertions.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assertions.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assertions.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        gr.rotateRight();
        gr.rotateRight();
        gr.rotateRight();
        gr.rotateRight();
        Assertions.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assertions.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assertions.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assertions.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        gr.rotateLeft();
        gr.rotateRight();
        Assertions.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assertions.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assertions.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assertions.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        gr.rotateRight();
        gr.rotateLeft();
        Assertions.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assertions.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assertions.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assertions.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        
        gr.rotateLeft();
        Assertions.assertEquals(grTwo.getHeight(), gr.getWidth());
        Assertions.assertEquals(grTwo.getWidth(), gr.getHeight());
        Assertions.assertEquals(grTwo.getCorners()[1], gr.getPosition());
        Assertions.assertEquals(grTwo.getCorners()[0], gr.getCorners()[3]);
        Assertions.assertEquals(grTwo.getCorners()[1], gr.getCorners()[0]);
        Assertions.assertEquals(grTwo.getCorners()[2], gr.getCorners()[1]);
        Assertions.assertEquals(grTwo.getCorners()[3], gr.getCorners()[2]);
        gr.rotateRight();
        Assertions.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assertions.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assertions.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assertions.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        
        gr.rotateRight();
        Assertions.assertEquals(grTwo.getHeight(), gr.getWidth());
        Assertions.assertEquals(grTwo.getWidth(), gr.getHeight());
        Assertions.assertEquals(grTwo.getCorners()[3], gr.getPosition());
        Assertions.assertEquals(grTwo.getCorners()[0], gr.getCorners()[1]);
        Assertions.assertEquals(grTwo.getCorners()[1], gr.getCorners()[2]);
        Assertions.assertEquals(grTwo.getCorners()[2], gr.getCorners()[3]);
        Assertions.assertEquals(grTwo.getCorners()[3], gr.getCorners()[0]);
    }
}
