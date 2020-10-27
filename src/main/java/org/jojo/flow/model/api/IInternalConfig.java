package org.jojo.flow.model.api;

import java.io.Serializable;

/**
 * This interface represents an internal config for a flow module. The internal config may contain
 * any amount and types of serializable data. All implementing non-abstract classes must also provide 
 * the methods for DOM creation defined in {@link IDOMable}.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @see Serializable
 * @see IDOMable
 */
public interface IInternalConfig extends Serializable, IDOMable {
    
}
