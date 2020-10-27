package org.jojo.flow.exc;

/**
 * This exception represents an exception which should be used for wrapping exceptions occurring
 * during running of flow module code.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ModuleRunException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 865109349521154420L;
    public static final String MOD_RUN_EXC_STR = "Module run exception with this exception message: ";

    public ModuleRunException(final Warning errorWarning) {
        super(errorWarning);
    }
}
