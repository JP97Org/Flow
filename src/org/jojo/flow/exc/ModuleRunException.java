package org.jojo.flow.exc;

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
