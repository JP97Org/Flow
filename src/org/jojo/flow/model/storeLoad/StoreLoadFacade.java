package org.jojo.flow.model.storeLoad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.Pair;
import org.jojo.flow.model.flowChart.FlowChart;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class StoreLoadFacade {
    private final ModuleClassesList list;
    private final DynamicClassLoader loader;
    
    public StoreLoadFacade() {
        this.list = null;
        this.loader = null;
    }
    
    public StoreLoadFacade(final ModuleClassesList list) {
        this.list = Objects.requireNonNull(list);
        this.loader = list.getClassLoader();
    }
    
    public StoreLoadFacade(final ModuleClassesList list, DynamicClassLoader loader) {
        this.list = Objects.requireNonNull(list);
        this.loader = Objects.requireNonNull(loader);
    }
    
    public FlowChart loadFlowChart(final File xmlFile) {
        Objects.requireNonNull(xmlFile);
        if (DocumentString.isParseable(xmlFile)) {
            final DocumentString docStr;
            try {
                docStr = new DocumentString(xmlFile);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                // should not happen
                new Warning(null, e.toString(), true).reportWarning();
                return null;
            }
            final Document document = docStr.getDocument();
            DOM.resetDocument(document);
            final FlowDOM flowDom = FlowDOM.of(document);
            final DOM fcDom = flowDom.getFlowChartDOM();
            return DynamicObjectLoader.loadFlowChartFromDOM(fcDom);
        }
        new Warning(null, "could not load flowchart because the document is not parseable", true).reportWarning();
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
        new Warning(null, "could not store flowchart because the document is not transformable", true).reportWarning();
        return false;
    }
    
    public Pair<ModuleClassesList, DynamicClassLoader> getListLoaderPair() {
        if (this.list == null) {
            return null;
        }
        return new Pair<>(this.list, this.loader);
    }
    
    public ModuleClassesList getNewModuleClassesList(final File tmpDirectory, final File... jars) {
        final var classLoader = new DynamicClassLoader(tmpDirectory);
        return new ModuleClassesList(classLoader, jars);
    }
    
    public DynamicClassLoader getNewDynamicClassLoader(final File tmpDirectory) {
        return new DynamicClassLoader(tmpDirectory);
    }
}
