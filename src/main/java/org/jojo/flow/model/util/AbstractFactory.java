package org.jojo.flow.model.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IAPI;
import org.jojo.flow.model.api.IFactory;

public abstract class AbstractFactory implements IFactory {
    private final Map<Class<?>, Class<?>> apiToImplementationMap;
    
    protected AbstractFactory() {
        this.apiToImplementationMap = new HashMap<>();
    }
    
    @Override
    public abstract void initialize();
    
    protected void clear() {
        this.apiToImplementationMap.clear();
    }
    
    @Override
    public Map<Class<?>, Class<?>> getApiToImplementationMap() {
        return new HashMap<>(this.apiToImplementationMap);
    }
    
    @Override
    public boolean putImplementationMapping(final Class<?> key, final Class<?> value) {
        if (key != null && value != null && key.isAssignableFrom(value)) {
            this.apiToImplementationMap.put(key, value);
            return true;
        }
        new Warning(null, "implementation cannot be set: key is not assignable from value. key= " 
                            + key + " | value= " + value, true).reportWarning();
        return false;
    }

    @Override
    public IAPI getImplementationOfApi(String iClassName, final Class<?>[] parameterTypes, final Object... initArgs) throws IllegalArgumentException {
        try {
            final Class<?> iClass = Class.forName(iClassName);
            final Class<?> implClass = this.apiToImplementationMap.get(iClass);
            if (implClass != null) {
                final Object o = DynamicObjectLoader.load(IAPI.class.getClassLoader(), implClass.getName(), 
                            parameterTypes, initArgs);
                if (IAPI.class.isInstance(o) && implClass.isInstance(o)) {
                    return (IAPI) o;
                } else {
                    final String warning = "wrong API to default impl. mapping: " + iClass + " -> " + implClass;
                    throw new IllegalArgumentException(warning);
                }
            }
        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException e) {
            throw new IllegalArgumentException(e.toString());
        }
        return null;
    }
}
