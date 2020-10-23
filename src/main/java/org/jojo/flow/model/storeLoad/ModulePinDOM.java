package org.jojo.flow.model.storeLoad;

import org.jojo.flow.model.flowChart.GraphicalRepresentation;

public class ModulePinDOM extends DOM {
    public static final String NAME = "Pin";
    public static final String NAME_CLASSNAME = "ClassName";
    public static final String NAME_CLASSNAME_IMP = "ClassNameImp";
    public static final String NAME_MODULE_ID = "ModuleID";
    public static final String NAME_CONNECTION_IDS = "ConnectionIDs";
    public static final String NAME_CONNECTION_ID = "ConnectionID";
    
    public ModulePinDOM() {
        super(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements().createElement(NAME));
    }
    
    public void setClassName(final String className) {
        appendString(NAME_CLASSNAME, className);
    }
    
    public void setClassNameImp(final String classNameImp) {
        appendString(NAME_CLASSNAME_IMP, classNameImp);
    }
    
    public void setModuleID(final int id) {
        appendInt(NAME_MODULE_ID, id);
    }
    
    public void setConnectionIDs(final int[] ids) {
        appendInts(NAME_CONNECTION_IDS, NAME_CONNECTION_ID, ids);
    }
    
    public void setGraphicalRepresentation(final GraphicalRepresentation gr) {
        appendCustomDOM(gr);
    }
}
