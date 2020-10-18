package org.jojo.flow.model.storeLoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IDOMable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public abstract class DOM implements IDOMable, IDOM { 
    private static Document documentStatic;
    
    private final Document document;
    private final Node parent;
    
    public DOM(final Document document, final Node parent) {
        this.document = Objects.requireNonNull(document);
        this.parent = Objects.requireNonNull(parent);
    }
    
    public static Document getDocumentForCreatingElements() {
        if (documentStatic == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            try {
                builder = dbf.newDocumentBuilder();
                Document doc = builder.newDocument();
                documentStatic = doc;
                return doc;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                new Warning(null, e.getMessage(), true).reportWarning();
                return null;
            }
        }
        return documentStatic;
    }
    
    protected static void resetDocument() {
        documentStatic = null;
    }
    
    protected static void resetDocument(final Document document) {
        documentStatic = document;
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
    
    @Override
    public void appendString(final String name, final String content) {
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        elem.appendChild(getDocument().createTextNode(Objects.requireNonNull(content)));
        append(elem);
    }
    
    @Override
    public void appendInt(final String name, final int content) {
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        elem.appendChild(getDocument().createTextNode("" + Objects.requireNonNull(content)));
        append(elem);
    }
    
    @Override
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
    
    @Override
    public <T extends IDOMable> void appendList(final String name, final List<T> list) {
        Objects.requireNonNull(list);
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        list.forEach(p -> elem.appendChild(p.getDOM().getParentNode()));
        append(elem);
    }
    
    @Override
    public void appendCustomDOM(final String name, final IDOMable domable) {
        Objects.requireNonNull(domable);
        final var elem = getDocument().createElement(Objects.requireNonNull(name));
        elem.appendChild(domable.getDOM().getParentNode());
        append(elem);
    }
    
    @Override
    public void appendCustomDOM(final IDOMable domable) {
        Objects.requireNonNull(domable);
        append(domable.getDOM().getParentNode());
    }
    
    @Override
    public Map<String, Object> getDOMMap() {
        return getDOMMap(getParentNode());
    }
    
    @Override
    public String elemGet() {
        if (this.parent.getChildNodes().getLength() == 0) {
            return null;
        }
        
        return this.parent.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE
                ? (((Text) this.parent.getChildNodes().item(0)).getNodeValue()) : null;
    }
    
    private static Map<String, Object> getDOMMap(final Node node) {
        final Map<String, Object> map = new HashMap<>();
        
        final Element elem = node.getNodeType() == Node.ELEMENT_NODE ? (Element)node : null;
        for (int i = 0; elem != null && i < node.getAttributes().getLength(); i++) {
            //TODO evtl. nochmal ueberarbeiten, hier sind die namen nicht eindeutig!
            final Node attr = node.getAttributes().item(i);
            final String name = attr.getNodeName();
            map.put(name, elem.getAttribute(name));
        }
        
        if (!elem.hasChildNodes()) {
            map.put(elem.getTagName(), "");
            return map;
        }
        
        for (int i = 0; elem != null && i < node.getChildNodes().getLength(); i++) {
            final Node childNode = node.getChildNodes().item(i);
            final Element childElem = childNode.getNodeType() == Node.ELEMENT_NODE ? (Element)childNode : null;
            final Text childText = childNode.getNodeType() == Node.TEXT_NODE ? (Text)childNode : null;
            String name = (childElem != null) ? (childElem.getTagName()) : (elem.getTagName() + i);
            while (map.containsKey(name)) {
                name += i + "_";
            }
            final Object value = (childText != null) 
                    ? childText.getNodeValue() 
                    : new DOM(getDocumentForCreatingElements(), childNode) {};
            map.put(name, value);
        }
        return map;
    }
    
    @Override
    public DOM getDOM() {
        return this;
    }
    
    @Override
    public void restoreFromDOM(final DOM dom) {
        // do nothing because this is already a DOM
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        return true;
    }
    
    @Override
    public String toString() {
        return getClass().getName() + " | parent= " + this.parent.toString();
    }
}
