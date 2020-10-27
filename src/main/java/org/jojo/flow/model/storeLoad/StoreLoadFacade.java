package org.jojo.flow.model.storeLoad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IDynamicClassLoader;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IModuleClassesList;
import org.jojo.flow.model.api.IStoreLoadFacade;
import org.jojo.flow.model.api.Pair;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class StoreLoadFacade implements IStoreLoadFacade {
    private final IModuleClassesList list;
    private final IDynamicClassLoader loader;
    
    public StoreLoadFacade() {
        this.list = null;
        this.loader = null;
    }
    
    public StoreLoadFacade(final IModuleClassesList list) {
        this.list = Objects.requireNonNull(list);
        this.loader = list.getClassLoader();
    }
    
    public StoreLoadFacade(final IModuleClassesList list, IDynamicClassLoader loader) {
        this.list = Objects.requireNonNull(list);
        this.loader = Objects.requireNonNull(loader);
    }
    
    @Override
    public IFlowChart loadFlowChart(final File xmlFile) {
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
            final IDOM fcDom = flowDom.getFlowChartDOM();
            return DynamicObjectLoader.loadFlowChartFromDOM(fcDom);
        }
        new Warning(null, "could not load flowchart because the document is not parseable", true).reportWarning();
        return null;
    }
    
    @Override
    public boolean storeFlowChart(final File xmlFile, final IDOM flowDom) {
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
    
    @Override
    public Pair<IModuleClassesList, IDynamicClassLoader> getListLoaderPair() {
        if (this.list == null) {
            return null;
        }
        return new Pair<>(this.list, this.loader);
    }
    
    @Override
    public ModuleClassesList getNewModuleClassesList(final File tmpDirectory, final File... jars) {
        final var classLoader = new DynamicClassLoader(tmpDirectory);
        return new ModuleClassesList(classLoader, jars);
    }
    
    @Override
    public DynamicClassLoader getNewDynamicClassLoader(final File tmpDirectory) {
        return new DynamicClassLoader(tmpDirectory);
    }
}
