package org.jojo.flow.model.api;

public interface IDefaultArrow extends IConnection {

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