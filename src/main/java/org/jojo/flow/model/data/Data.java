package org.jojo.flow.model.data;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IXMLSerialTransform;

public abstract class Data implements IData {
    /**
     * 
     */
    private static final long serialVersionUID = -480461809561335311L;

    protected abstract int getDataId();
    
    public abstract IDataSignature getDataSignature();
    
    public boolean hasSameType(final IDataSignature dataType) {
        return getDataSignature().matches(dataType);
    }
    
    @Override
    public int hashCode() {
        return getDataSignature().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other instanceof Data) {
            return hasSameType(((Data)other).getDataSignature());
        }
        return false;
    }
    
    @Override
    public abstract String toString();
    
    private static <T extends Data> String serialize(T o) throws IOException, ClassNotFoundException {
        return IXMLSerialTransform.serialize(o);
    }
    
    public String toSerializedString() throws ClassNotFoundException, IOException {
        final String base64 = serialize(this);
        IXMLSerialTransform transform = null;
        try {
            transform = IXMLSerialTransform.getDefaultImplementation();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            new Warning(null, e.toString(), true).reportWarning();
        }
        return transform == null ? base64 : transform.toXMLString(base64);
    }
    
    public static Data ofSerializedString(final String serializedString) throws ClassNotFoundException, IOException, IllegalArgumentException {
        IXMLSerialTransform transform = null;
        try {
            transform = IXMLSerialTransform.getDefaultImplementation();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            new Warning(null, e.toString(), true).reportWarning();
        }
        final String base64 = transform == null ? serializedString : transform.toSerialString(serializedString);
        return deserialize(base64);
    }
    
    private static Data deserialize(final String serializedString) throws IOException, ClassNotFoundException {
        Object o = IXMLSerialTransform.deserialize(serializedString);
        if (o instanceof Data) {
             return (Data)o;
        } else {
            throw new IllegalArgumentException("read object has not the correct type, it should be an instance of Data but is: " + o.getClass());
        }
    }
}
