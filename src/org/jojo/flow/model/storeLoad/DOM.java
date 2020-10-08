package org.jojo.flow.model.storeLoad;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jojo.flow.model.Warning;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOM implements DOMable { 
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
    
    public void appendString(final String name, final String content) {
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        elem.appendChild(getDocument().createTextNode(Objects.requireNonNull(content)));
        append(elem);
    }
    
    public void appendInt(final String name, final int content) {
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        elem.appendChild(getDocument().createTextNode("" + Objects.requireNonNull(content)));
        append(elem);
    }
    
    public void appendInts(final String listName, final String name, final int[] ids) {
        Objects.requireNonNull(ids);
        final var elem = getDocument().createElement(Objects.requireNonNull(listName));
        final List<Integer> idsList = new ArrayList<>();
        for (int id : ids) {
            idsList.add(id);
        }
        idsList.forEach(i -> elem.appendChild(getDocument().createElement(name)
                .appendChild(getDocument().createTextNode("" + i))));
        append(elem);
    }
    
    public <T extends DOMable> void appendList(final String name, final List<T> list) {
        Objects.requireNonNull(list);
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        list.forEach(p -> elem.appendChild(p.getDOM().getParentNode()));
        append(elem);
    }
    
    public void appendCustomDOM(final String name, final DOMable domable) {
        Objects.requireNonNull(domable);
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        elem.appendChild(domable.getDOM().getParentNode());
        append(elem);
    }
    
    public void appendCustomDOM(final DOMable domable) {
        Objects.requireNonNull(domable);
        append(domable.getDOM().getParentNode());
    }
    
    @Override
    public DOM getDOM() {
        return this;
    }
    
    @Override
    public void restoreFromDOM(final DOM dom) {
        // do nothing because this is already a DOM
    }
}
