package org.jojo.flow.model.api;

import java.io.File;

import org.w3c.dom.Document;

public interface IDocumentString extends IAPI {
    public static IDocumentString getDefaultImplementation(final Document xml) {
        return (IDocumentString) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Document.class}, xml);
    }
    
    public static IDocumentString getDefaultImplementation(final File xmlFile) {
        return (IDocumentString) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {File.class}, xmlFile);
    }

    Document getDocument();

    boolean isTransformable();
}