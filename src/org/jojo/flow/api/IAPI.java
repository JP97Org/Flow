package org.jojo.flow.api;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;

public interface IAPI {
    static final Map<Class<?>, Class<?>> apiToDefaultImplementationMap = new HashMap<>();
    
    static void initialize() {
        apiToDefaultImplementationMap.put(IFraction.class, Fraction.class);
        apiToDefaultImplementationMap.put(IDataSignature.class, DataSignature.DontCareDataSignature.class);
    }
    
    static IAPI defaultImplementationOfThisApi(final Class<?>[] parameterTypes, final Object... initArgs) {
        final String iClassName;
        try { 
            throw new RuntimeException();
        } catch (RuntimeException e) {
            iClassName = e.getStackTrace()[1].getClassName();
        }
        try {
            final Class<?> iClass = Class.forName(iClassName);
            final Class<?> implClass = apiToDefaultImplementationMap.get(iClass);
            if (implClass != null) {
                final Object o = DynamicObjectLoader.load(IAPI.class.getClassLoader(), implClass.getName(), 
                            parameterTypes, initArgs);
                if (IAPI.class.isInstance(o) && implClass.isInstance(o)) {
                    return (IAPI) o;
                } else {
                    System.err.println("wrong API to default impl. mapping: " + iClass + " -> " + implClass);
                }
            }
        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException e) {
            // should not happen
            e.printStackTrace();
        }
        return null;
    }
}
