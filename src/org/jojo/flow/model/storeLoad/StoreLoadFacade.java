package org.jojo.flow.model.storeLoad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.flowChart.FlowChart;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class StoreLoadFacade {
    public StoreLoadFacade() {
        
    }
    
    public FlowChart loadFlowChart(final File xmlFile) {
        Objects.requireNonNull(xmlFile);
        if (DocumentString.isParseable(xmlFile)) {
            final DocumentString docStr;
            try {
                docStr = new DocumentString(xmlFile);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                // should not happen
                e.printStackTrace();
                return null;
            }
            final Document document = docStr.getDocument();
            DOM.resetDocument(document);
            final FlowDOM flowDom = FlowDOM.of(document);
            final DOM fcDom = flowDom.getFlowChartDOM();
            return DynamicObjectLoader.loadFlowChartFromDOM(fcDom);
        }
        return null;
    }
    
    public boolean storeFlowChart(final File xmlFile, final DOM flowDom) {
        Objects.requireNonNull(xmlFile);
        final DocumentString docStr = new DocumentString(flowDom.getDocument());
        if (docStr.isTransformable()) {
            final String toWrite = docStr.toString();
            try {
                final FileWriter fw = new FileWriter(xmlFile);
                fw.write(toWrite);
                fw.close();
            } catch (IOException e) {
                new Warning(null, e.toString(), true).reportWarning();
                return false;
            }
            DOM.resetDocument();
            return true;
        }
        return false;
    }
}
