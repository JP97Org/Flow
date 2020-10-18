package org.jojo.flow.model.api;

import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IDefaultArrow extends IConnection {
    public static IDefaultArrow getDefaultImplementation(final IOutputPin from, final IInputPin to, final String name) {
        return (IDefaultArrow) DynamicObjectLoader.loadConnection(
                IModelFacade.getDefaultImplementation().nextFreeId(), from, to, name);
    }

    IData getData();

    boolean putData(IData data);

    IDataSignature getDataSignature();

    /**
     * Sets a data signature to the arrow which matches the data signature at the moment,
     * but must be completely checking.
     * @param iDataSignature - the given data signature which must be recursively checking
     * @return whether putting the new data signature was successful
     */
    boolean putDataSignature(IDataSignature iDataSignature);

    void forcePutDataSignature(IDataSignature checkingDataSignature);
}