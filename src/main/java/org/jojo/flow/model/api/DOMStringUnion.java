package org.jojo.flow.model.api;

import java.util.Objects;

/**
 * This class represents a union of IDOM and String.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class DOMStringUnion { 
    private final IDOM dom;
    private final String str;
    
    /**
     * Creates a new union for a IDOM.
     * 
     * @param dom - the IDOM
     */
    public DOMStringUnion(final IDOM dom) {
        this(dom, null);
    }
    
    /**
     * Creates a new union for a string.
     * 
     * @param str - the string
     */
    public DOMStringUnion(final String str) {
        this(null, str);
    }
    
    /**
     * Creates a new union.
     * 
     * @param value - the value of the union (if it is not String nor IDOM this union will contain {@code null})
     */
    public DOMStringUnion(final Object value) {
        this(value instanceof IDOM ? (IDOM)value : null, value instanceof String ? (String)value : null);
    }
    
    private DOMStringUnion(final IDOM dom, final String str) {
        this.dom = dom;
        this.str = str;
    }
    
    /**
     * Gets the IDOM if existent.
     * 
     * @return the IDOM if existent
     */
    public IDOM getDOM() {
        return this.dom;
    }
    
    /**
     * Gets the string if existent.
     * 
     * @return the string if existent.
     */
    public String getStr() {
        return this.str;
    }
    
    /**
     * Determines whether this union contains an IDOM.
     * 
     * @return whether this union contains an IDOM
     */
    public boolean isDOM() {
        return this.dom != null;
    }
    
    /**
     * Determines whether this union contains a string.
     * 
     * @return whether this union contains a string
     */
    public boolean isString() {
        return this.str != null;
    }
    
    /**
     * Determines whether this union contains neither a string nor an IDOM.
     * 
     * @return whether this union contains neither a string nor an IDOM
     */
    public boolean isNull() {
        return !isDOM() && !isString();
    }
    
    /**
     * Gets the value of this union.
     * 
     * @return the value of this union
     */
    public Object getValue() {
        return isString() ? this.str : this.dom;
    }
    
    @Override
    public int hashCode() {
        return getValue() == null ? 0 : getValue().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof DOMStringUnion) {
            return Objects.equals(getValue(), ((DOMStringUnion)o).getValue());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return getValue() == null ? "null" : getValue().toString();
    }
}
