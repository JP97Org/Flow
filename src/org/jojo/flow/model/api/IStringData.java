package org.jojo.flow.model.api;

public interface IStringData extends IBasicCheckable {
    public static IStringData getDefaultImplementation(final String string) {
        final var ret = (IStringData) IAPI.defaultImplementationOfThisApi(new Class<?>[] {String.class}, string);
        return ret;
    }
    
    @Override
    String toString();
}
