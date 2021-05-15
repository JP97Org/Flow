package org.jojo.flow.model.api;

import java.io.File;

import org.w3c.dom.Document;

/**
 * This interface represents a document string which contains a xml document.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDocumentString extends IAPI {
    
    /**
     * Gets the default implementation.
     * 
     * @param xml - the xml document which should be contained (it must not be {@code null})
     * @return the default implementation containing the given document
     */
    public static IDocumentString getDefaultImplementation(final Document xml) {
        return (IDocumentString) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Document.class}, xml);
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param xmlFile - the xml file to be parsed (it must not be {@code null}) and should be parseable
     * @return the default implementation 
     * @see org.jojo.flow.model.storeLoad.DocumentString#isParseable(File)
     */
    public static IDocumentString getDefaultImplementation(final File xmlFile) {
        return (IDocumentString) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {File.class}, xmlFile);
    }

    /**
     * Gets the document.
     * 
     * @return the document
     */
    Document getDocument();

    /**
     * Determines whether the document of this instance is transformable to a string.
     * If this method returns {@code false} a Warning is reported.
     * 
     * @return whether the document of this instance is transformable to a string
     * @see #toString()
     */
    boolean isTransformable();
    
    /**
     * Transforms this instance to a string which can be written to a xml file. <br/>
     * Before calling this method in order to write the contents of this document to a xml file,
     * please make sure that this instance is transformable. If this instance is not transformable
     * an error Warning is reported.
     * 
     * @return if this instance is transformable, the xml document as a string which can be written to a xml file <br/>
     * otherwise, the exception's string representation is returned
     * @see #isTransformable()
     */
    String toString();
}