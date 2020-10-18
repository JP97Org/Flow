package org.jojo.flow.model.api;

import org.w3c.dom.Document;

public interface IDocumentString extends IAPI {

    Document getDocument();

    boolean isTransformable();
}