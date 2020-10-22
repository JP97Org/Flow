package org.jojo.flow.model.api;

import java.util.Objects;

//TODO doc for this class and implement as better datatype instead of Object for value type of getDOMMap()
public class DOMStringUnion { 
    private final IDOM dom;
    private final String str;
    
    public DOMStringUnion(final IDOM dom) {
        this.dom = dom;
        this.str = null;
    }
    
    public DOMStringUnion(final String str) {
        this.dom = null;
        this.str = str;
    }
    
    public IDOM getDOM() {
        return this.dom;
    }
    
    public String getStr() {
        return this.str;
    }
    
    public boolean isDOM() {
        return this.dom != null;
    }
    
    public boolean isString() {
        return this.str != null;
    }
    
    public boolean isNull() {
        return !isDOM() && !isString();
    }
    
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
