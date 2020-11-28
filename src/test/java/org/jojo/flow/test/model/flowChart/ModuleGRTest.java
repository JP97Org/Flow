package org.jojo.flow.test.model.flowChart;

import java.util.Arrays;

import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IModuleGR;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.junit.*;

public class ModuleGRTest {
    private IFlowModule mock;
    private IModuleGR gr;

    @Before
    public void setUp() {
        this.mock = DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
        this.gr = (IModuleGR) mock.getGraphicalRepresentation();
    }

    @Test
    public void get() {
        Assert.assertTrue(mock.getGraphicalRepresentation() == gr);
        Assert.assertTrue(gr.getModule() == mock);
        Assert.assertEquals(4, gr.getCorners().length);
        Assert.assertTrue(Arrays.stream(gr.getCorners()).allMatch(c -> c != null));
        Assert.assertTrue(gr.getInfoText() != null);
        Assert.assertTrue(gr.getScale() == 1.);
        Assert.assertTrue(gr.isDOMValid(gr.getDOM()));
    }
    
    @Test
    public void scale() {
        Assert.assertTrue(gr.getScale() == 1.);
        gr.setScale(2.);
        Assert.assertTrue(gr.getScale() == 2.);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void scaleExc() {
        gr.setScale(-5.);
    }

    @Test
    public void rotate() {
        final IFlowModule mockTwo = DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 200);
        final IModuleGR grTwo = (IModuleGR) mockTwo.getGraphicalRepresentation();
        Assert.assertTrue(gr != grTwo);
        Assert.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assert.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assert.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assert.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        gr.rotateLeft();
        gr.rotateLeft();
        gr.rotateLeft();
        gr.rotateLeft();
        Assert.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assert.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assert.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assert.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        gr.rotateRight();
        gr.rotateRight();
        gr.rotateRight();
        gr.rotateRight();
        Assert.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assert.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assert.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assert.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        gr.rotateLeft();
        gr.rotateRight();
        Assert.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assert.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assert.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assert.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        gr.rotateRight();
        gr.rotateLeft();
        Assert.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assert.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assert.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assert.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        
        gr.rotateLeft();
        Assert.assertEquals(grTwo.getHeight(), gr.getWidth());
        Assert.assertEquals(grTwo.getWidth(), gr.getHeight());
        Assert.assertEquals(grTwo.getCorners()[1], gr.getPosition());
        Assert.assertEquals(grTwo.getCorners()[0], gr.getCorners()[3]);
        Assert.assertEquals(grTwo.getCorners()[1], gr.getCorners()[0]);
        Assert.assertEquals(grTwo.getCorners()[2], gr.getCorners()[1]);
        Assert.assertEquals(grTwo.getCorners()[3], gr.getCorners()[2]);
        gr.rotateRight();
        Assert.assertEquals(grTwo.getHeight(), gr.getHeight());
        Assert.assertEquals(grTwo.getWidth(), gr.getWidth());
        Assert.assertEquals(grTwo.getPosition(), gr.getPosition());
        Assert.assertArrayEquals(grTwo.getCorners(), gr.getCorners());
        
        gr.rotateRight();
        Assert.assertEquals(grTwo.getHeight(), gr.getWidth());
        Assert.assertEquals(grTwo.getWidth(), gr.getHeight());
        Assert.assertEquals(grTwo.getCorners()[3], gr.getPosition());
        Assert.assertEquals(grTwo.getCorners()[0], gr.getCorners()[1]);
        Assert.assertEquals(grTwo.getCorners()[1], gr.getCorners()[2]);
        Assert.assertEquals(grTwo.getCorners()[2], gr.getCorners()[3]);
        Assert.assertEquals(grTwo.getCorners()[3], gr.getCorners()[0]);
    }
}
