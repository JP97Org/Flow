package org.jojo.flow.model.storeLoad;

import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jojo.flow.model.Warning;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOM { //TODO DOM is used by FCEs for saving; loading is done directly by a document parser
    public static final String NAME_OTHERS = "Others";
    
    private final Document document;
    private final Node parent;
    
    public DOM(final Document document, final Node parent) {
        this.document = Objects.requireNonNull(document);
        this.parent = Objects.requireNonNull(parent);
    }
    
    public static Document getDocumentForCreatingElements() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = dbf.newDocumentBuilder();
            Document doc = builder.newDocument();
            return doc;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            new Warning(null, e.getMessage(), true).reportWarning();
            return null;
        }
    }
    
    protected void addElement(final Element element) {
        this.parent.appendChild(element);
    }
    
    protected Document getDocument() {
        return this.document;
    }
    
    protected Node getParentNode() {
        return this.parent;
    }
    
    protected void append(final Node elem) {
        getParentNode().appendChild(elem);
    }
    
    public void setOthers(final DOMable others) {
        appendCustomDOM(NAME_OTHERS, others);
    }
    
    public void appendCustomDOM(final String name, final DOMable domable) {
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        elem.appendChild(domable.getDOM().getParentNode());
        append(elem);
    }
}
