package org.jojo.flow.model.api;

/**
 * The IDOMable interface represents a super-interface for all interfaces and classes 
 * which support that their instances can be transformed to and restored from a IDOM.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDOMable extends IAPI {
    
    /**
     * Gets the DOM of this instance. Note that it must be ensured that {@code a.isDOMValid(b.getDOM())}
     * always returns {@code true} for all instances a,b of the same class implementing IDOMable.
     * Moreover, it must be ensured that after {@code a.restoreFromDOM(b.getDOM())} 
     * (for all instances a,b of the same class implementing IDOMable) returns, a must be 
     * exactly equal to b, i.e. all relevant data of b must be copied to a, 
     * so that a and b are indistinguishable except maybe their object identity.
     * 
     * @return the DOM representing this instance
     */
    IDOM getDOM();
    
    /**
     * Restores this IDOMable from the given IDOM if it is valid.
     * This method does not change anything if the given IDOM is not valid. 
     * In this case it causes an error Warning to be reported.
     * 
     * @param dom - the given IDOM
     */
    void restoreFromDOM(IDOM dom);
    
    /**
     * Determines whether the given IDOM is valid. It is strongly recommended to check a DOM's validity
     * before restoring it with {@link #restoreFromDOM(IDOM)}.
     * If the given DOM is invalid, an error Warning is reported.
     * 
     * @param dom - the given IDOM
     * @return whether the given IDOM is valid
     */
    boolean isDOMValid(IDOM dom);
}
