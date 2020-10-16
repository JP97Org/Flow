package org.jojo.flow.model.api;

public interface IDataArray extends IRecursiveCheckable {
    public static IDataArray getDefaultImplementation(final IData[] data, final IDataSignature dataSignature) {
        return (IDataArray) IAPI.defaultImplementationOfThisApi(new Class<?>[] {IData[].class, IDataSignature.class}, data, dataSignature);
    }
}
