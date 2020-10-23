package org.jojo.flow.model.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * Represents a XML to Serial and vice versa transformator. Classes which implement this interface
 * must specify a parameter-less public constructor and be named XMLSerialTransformer in a package with
 * the name "org.xmlSerial".
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IXMLSerialTransform extends IAPI {
    
    /**
     * Gets the default IXMLSerialTransform, i.e. the first element of the classes loaded
     * by the default IDyanimClassLoader instance from the location specified by the
     * {@link ISettings#getLocationXMLSerialTransformerJar()} method of the default ISettings
     * implementation.
     * 
     * @return the default IXMLSerialTransform or {@code null} if non-existent or tmp dir is not specified
     * @throws IOException if an I/O failure occurs
     * @throws ClassNotFoundException if a class is not found
     * @throws InvocationTargetException if the invoked constructor throws an exception
     * @throws IllegalAccessException if the access of the constructor is not possible
     * @throws InstantiationException  if a class cannot be instatiated
     * @throws SecurityException if the security manager does not allow the access
     * @throws NoSuchMethodException if a method was not found
     */
     static IXMLSerialTransform getDefaultImplementation() 
             throws ClassNotFoundException, IOException, NoSuchMethodException, SecurityException, 
             InstantiationException, IllegalAccessException, IllegalArgumentException, 
             InvocationTargetException {
         final File locationTmp = ISettings.getDefaultImplementation().getLocationTmpDir();
         if (locationTmp == null) {
             return null;
         }
         
         final File locationTransformer = ISettings.getDefaultImplementation().getLocationXMLSerialTransformerJar();
         final IDynamicClassLoader loader = IDynamicClassLoader.getDefaultImplementation(locationTmp);
         final List<File> files = locationTransformer == null ? new ArrayList<>() : loader.unpack(locationTransformer);
         final List<Class<?>> loaded = new ArrayList<>();
         if (!files.isEmpty()) {
             final File mainClassFile = files.stream()
                     .filter(f -> f.getName().startsWith("XMLSerialTransformer"))
                     .findFirst().orElse(null);
             files.forEach(f -> {
                 final String binaryName = loader.binaryNameOf(f.getAbsolutePath());
                 loader.putExternalClass(binaryName, locationTransformer);
             });
             loaded.add(loader.loadClass(loader.binaryNameOf(mainClassFile.getAbsolutePath())));
         }
         @SuppressWarnings("unchecked")
         final Class<? extends IXMLSerialTransform> transformerClass = loaded
                 .stream()
                 .filter(c -> IXMLSerialTransform.class.isAssignableFrom(c))
                 .map(c -> (Class<? extends IXMLSerialTransform>)c)
                 .findFirst().orElse(null);
         if (transformerClass != null) {
             final Object o = DynamicObjectLoader.load(loader.getClassLoader(), transformerClass.getName(),
                     new Class<?>[] {});
             return (IXMLSerialTransform)o;
         }
         return null;
    }
    
    /**
     * Serializes the given object.
     * 
     * @param <T> - the generic type
     * @param o - the given object
     * @return the serialization string encoded in BASE64
     * @throws IOException if an I/O failure occurs
     * @throws ClassNotFoundException if a class is not found
     */
    static <T> String serialize(T o) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        try (ObjectOutputStream oos = new ObjectOutputStream (baos)) {
            oos.writeObject(o);
        }
        final int times = 10;
        return Base64.getEncoder().encodeToString(baos.toByteArray())
                .replaceAll(Pattern.quote("\n"), times("newline", times))
                .replaceAll(Pattern.quote("\""), times("quote", times))
                .replaceAll(Pattern.quote(";"), times("semicolon", times));
    }

    private static String times(final String in, int times) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(in);
        }
        return builder.toString();
    }
    
    /**
     * Deserializes the given serialization string.
     * 
     * @param serializedString - the given serialization string encoded in BASE64
     * @return the object represented by the serialization string
     * @throws IOException if an I/O failure occurs
     * @throws ClassNotFoundException if a class is not found
     */
    static Object deserialize(final String serializedString) throws IOException, ClassNotFoundException {
        final int times = 10;
        ByteArrayInputStream bais = new ByteArrayInputStream (
                Base64.getDecoder().decode(serializedString
                .replaceAll("(semicolon){"+times+"}", ";")
                .replaceAll("(quote){"+times+"}", "\"")
                .replaceAll("(newline){"+times+"}", "\n")));
        try (ObjectInputStream ois = new ObjectInputStream (bais)) {
            Object o = ois.readObject();
            return o;
        }
    }
    
    /**
     * Transforms a serial string to a xml string (quoted).
     * 
     * @param serialString - the serial string encoded in BASE64 encoding
     * @return a xml string (quoted)
     */
    String toXMLString(String serialString);
    
    /**
     * Transforms a xml string (quoted) to a serial string.
     * 
     * @param xmlString - the xml string (quoted)
     * @return a serial string encoded in BASE64 encoding
     */
    String toSerialString(String xmlString);
}
