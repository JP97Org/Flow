package org.jojo.flow.model.api;

/**
 * The IDOMable interface represents a super-interface for all interfaces and classes 
 * which support that their instances can be transformed to and restored from a {@link IDOM}.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDOMable extends IAPI {
    
    /**
     * Gets the DOM of this instance. Note that it must be ensured that {@code a.isDOMValid(b.getDOM())}
     * always returns {@code true} for all instances a,b of the same class implementing {@link IDOMable}.
     * Moreover, it must be ensured that after {@code a.restoreFromDOM(b.getDOM())} 
     * (for all instances a,b of the same class implementing {@link IDOMable}) returns, a must be 
     * exactly equal to b, i.e. all relevant data of b must be copied to a, 
     * so that a and b are indistinguishable except maybe their object identity.
     * 
     * @return the DOM representing this instance
     * @see #restoreFromDOM(IDOM)
     * @see #isDOMValid(IDOM)
     */
    IDOM getDOM();
    
    /**
     * Restores this {@link IDOMable} from the given {@link IDOM} if it is valid.
     * This method does not change anything if the given {@link IDOM} is not valid. 
     * In this case it causes an error {@link org.jojo.flow.exc.Warning} to be reported.
     * 
     * @param dom - the given {@link IDOM}
     * @see #getDOM()
     * @see #isDOMValid(IDOM)
     */
    void restoreFromDOM(IDOM dom);
    
    /**
     * Determines whether the given {@link IDOM} is valid. It is strongly recommended to check a DOM's validity
     * before restoring it with {@link #restoreFromDOM(IDOM)}.
     * If the given DOM is invalid, an error {@link org.jojo.flow.exc.Warning} is reported.
     * 
     * @param dom - the given {@link IDOM}
     * @return whether the given {@link IDOM} is valid
     */
    boolean isDOMValid(IDOM dom);
}
