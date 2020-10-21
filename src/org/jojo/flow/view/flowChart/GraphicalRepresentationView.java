package org.jojo.flow.view.flowChart;

import java.awt.Graphics2D;
import java.util.Objects;

import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.view.Observer;

public abstract class GraphicalRepresentationView extends Observer {
    private final GraphicalRepresentation gr;
    private final Graphics2D graphics;
    
    public GraphicalRepresentationView(final GraphicalRepresentation gr, final Graphics2D graphics) {
        super(Objects.requireNonNull(gr));
        this.gr = gr;
        startObservingMainSubject();
        this.graphics = Objects.requireNonNull(graphics);
    }
    
    public GraphicalRepresentation getGraphicalRepresentation() {
        return this.gr;
    }
    
    public abstract Graphics2D draw();
    public abstract void delete();
    
    protected Graphics2D getGraphics() {
        return this.graphics;
    }
}
