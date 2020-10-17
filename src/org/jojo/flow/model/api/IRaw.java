package org.jojo.flow.model.api;

import java.util.List;

public interface IRaw extends IBasicCheckable {
    public static IRaw getDefaultImplementation(final List<Byte> bytes) {
        final var ret = (IRaw) IAPI.defaultImplementationOfThisApi(new Class<?>[] {List.class}, bytes);
        return ret;
    }
    
    public byte[] getData();
}
