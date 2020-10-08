package org.jojo.flow.model.storeLoad;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FlowChartDOM extends DOM {
    protected static final String NAME = "FlowChart";
    
    protected static final String NAME_MODULES = "Modules";
    protected static final String NAME_CONNECTIONS = "Connections";
    
    private final Element modules;
    private final Element connections;
    
    public FlowChartDOM(final Document document, final Node parent) {
        super(document, parent);
        this.modules = document.createElement(NAME_MODULES);
        this.connections = document.createElement(NAME_CONNECTIONS);
        addElement(this.modules);
        addElement(this.connections);
    }

    public void addModule(final ModuleDOM module) {
        this.modules.appendChild(module.getParentNode());
    }
    
    public void addConnection(final ConnectionDOM connection) {
        this.connections.appendChild(connection.getParentNode());
    }
}
