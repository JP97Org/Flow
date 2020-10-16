package org.jojo.flow.model.api;

import java.util.List;

public interface IDataBundle extends IRecursiveCheckable {
    public static IDataBundle getDefaultImplementation(final List<IData> data) {
        return (IDataBundle) IAPI.defaultImplementationOfThisApi(new Class<?>[] {List.class}, data);
    }
}
