package org.jojo.flow.model.storeLoad;

public class ConfigDOM extends DOM {
    public static final String NAME_EXT_CONFIG = "ExternalConfig";
    public static final String NAME_INT_CONFIG = "InternalConfig";
    public static final String NAME_NAME = "Name";
    public static final String NAME_PRIORITY = "Priority";
    
    private ConfigDOM(final boolean isExternal) {
        super(DOM.getDocumentForCreatingElements(), 
                DOM.getDocumentForCreatingElements().createElement(isExternal ? NAME_EXT_CONFIG : NAME_INT_CONFIG));
    }
    
    public static ConfigDOM getExternal() {
        return new ConfigDOM(true);
    }
    
    public static ConfigDOM getInternal() {
        return new ConfigDOM(false);
    }

    public void setName(final String name) {
        appendString(NAME_NAME, name);
    }
    
    public void setPriority(final int priority) {
        appendInt(NAME_PRIORITY, priority);
    }
}
