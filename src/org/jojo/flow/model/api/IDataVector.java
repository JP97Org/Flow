package org.jojo.flow.model.api;

import java.util.List;

public interface IDataVector extends IRecursiveCheckable {
    public static IDataVector getDefaultImplementation(final List<IData> data, final IDataSignature dataSignature) {
        return (IDataVector) IAPI.defaultImplementationOfThisApi(new Class<?>[] {List.class, IDataSignature.class}, data, dataSignature);
    }
}
