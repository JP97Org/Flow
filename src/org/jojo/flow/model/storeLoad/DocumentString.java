package org.jojo.flow.model.storeLoad;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jojo.flow.model.Warning;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DocumentString {
    private final Document xml;

    public DocumentString(final Document xml) {
        this.xml = Objects.requireNonNull(xml);
    }
    
    public DocumentString(final File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        this.xml = dBuilder.parse(Objects.requireNonNull(xmlFile));
    }
    
    public static boolean isParseable(final File xmlFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = factory.newDocumentBuilder();
            dBuilder.parse(Objects.requireNonNull(xmlFile));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            new Warning(null, e.getMessage(), false).reportWarning();
            return false;
        }
    }
    
    public Document getDocument() {
        return this.xml;
    }
    
    public boolean isTransformable() {
        try {
            transform();
            return true;
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            new Warning(null, e.getMessage(), false).reportWarning();
            return false;
        }
    }
    
    private String transform() throws TransformerFactoryConfigurationError, TransformerException {
        Transformer tf = TransformerFactory.newInstance().newTransformer();

        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(this.xml), new StreamResult(out));

        return out.toString();
    }

    @Override
    public String toString() {
        try {
            return transform();
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            e.printStackTrace();
            new Warning(null, e.getMessage(), true).reportWarning();
            return e.getMessage();
        }
    }
}
