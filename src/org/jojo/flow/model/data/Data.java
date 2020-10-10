package org.jojo.flow.model.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.regex.Pattern;

public abstract class Data implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -480461809561335311L;

    protected abstract int getDataId();
    
    public abstract DataSignature getDataSignature();
    
    public boolean hasSameType(final DataSignature other) {
        return getDataSignature().equals(other);
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
    
    private static final int TIMES = 10;
    private static <T extends Data> String serialize(T o) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        try (ObjectOutputStream oos = new ObjectOutputStream (baos)) {
            oos.writeObject(o);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray())
                .replaceAll(Pattern.quote("\n"), times("newline", TIMES))
                .replaceAll(Pattern.quote("\""), times("quote", TIMES))
                .replaceAll(Pattern.quote(";"), times("semicolon", TIMES));
    }

    private static String times(final String in, int times) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(in);
        }
        return builder.toString();
    }
    
    public String toSerializedString() throws ClassNotFoundException, IOException {
        return serialize(this);
    }
    
    public static Data ofSerializedString(final String serializedString) throws ClassNotFoundException, IOException {
        return deserialize(serializedString);
    }
    
    private static Data deserialize(final String serializedString) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream (
                Base64.getDecoder().decode(serializedString
                .replaceAll("(semicolon){"+TIMES+"}", ";")
                .replaceAll("(quote){"+TIMES+"}", "\"")
                .replaceAll("(newline){"+TIMES+"}", "\n")));
        try (ObjectInputStream ois = new ObjectInputStream (bais)) {
            Object o = ois.readObject();
            if (o instanceof Data) {
                return (Data)o;
            } else {
                throw new IllegalArgumentException("read object has not the correct type, it should be an instance of Data but is: " + o.getClass());
            }
        }
    }
}
