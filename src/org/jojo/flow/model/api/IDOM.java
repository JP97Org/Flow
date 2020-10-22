package org.jojo.flow.model.api;

import java.util.List;
import java.util.Map;

import org.jojo.flow.model.storeLoad.DOM;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface represents a DOM, i.e. a document object model which can be used for building a xml file
 * from objects of types implementing IDOMable.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDOM extends IDOMable {
    
    /**
     * The name of the "Others" tag.
     */
    String NAME_OTHERS = "Others";
    
    /**
     * Resets the document of the default implementation of IDOM, i.e. it deletes the document so that
     * a new document is created the next time 
     * 
     * {@link org.jojo.flow.model.storeLoad.DOM#getDocumentForCreatingElements()} is called.
     * 
     * @see org.jojo.flow.model.storeLoad.DOM#resetDocument()
     */
    static void resetDocument() {
        DOM.resetDocument();
    }

    /**
     * Gets the document.
     * 
     * @return the document
     */
    Document getDocument();
    
    /**
     * Gets the parent node of this DOM, i.e. the root node of this DOM.
     * All appended elements are appended as children of this node.
     * 
     * @return the parent node of this DOM, i.e. the root node of this DOM
     */
    Node getParentNode();
    
    /**
     * Appends a string to this DOM.
     * 
     * @param name - the name of the tag
     * @param content - the content of the text element
     */
    void appendString(String name, String content);

    /**
     * Appends an int to this DOM.
     * 
     * @param name - the name of the tag
     * @param content - the content of the text element as an int
     */
    void appendInt(String name, int content);

    /**
     * Appends an array of ints to this DOM.
     * 
     * @param listName - the name of the tag for the array
     * @param name - the name of the tag for the array elements
     * @param ints - the array of ints to be appended
     */
    void appendInts(String listName, String name, int[] ints);

    /**
     * Appends a list of IDOMable to this DOM.
     * 
     * @param <T> - the generic type of the list elements
     * @param name - the name of the tag for the list
     * @param list - the list of IDOMable 
     */
    <T extends IDOMable> void appendList(String name, List<T> list);

    /**
     * Appends a custom IDOMable's IDOM to this DOM. 
     * 
     * @param name - the name of the tag
     * @param domable - the IDOMable to be appended
     */
    void appendCustomDOM(String name, IDOMable domable);

    /**
     * Appends a custom IDOMable's IDOM directly to this DOM, i.e. no tag is created under which the
     * given IDOMable is appended.
     * 
     * @param domable - the IDOMable to be appended
     */
    void appendCustomDOM(IDOMable domable);

    /**
     * Gets a map from tags and attributes to contained objects. 
     * The contained object is a DOMStringUnion which either contains another IDOM or 
     * a String describing basic data.
     * 
     * @return a map from tags and attributes to contained objects
     * 
     */
    Map<String, DOMStringUnion> getDOMMap();

    /**
     * Gets the content of the text element of this tag which only contains one text element. 
     * 
     * @return the content of the text element of this tag which only contains one text element 
     * or {@code null} if this IDOM's parent node contains child nodes.
     */
    String elemGet();
}