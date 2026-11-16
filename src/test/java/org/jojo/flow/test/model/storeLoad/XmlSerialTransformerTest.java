package org.jojo.flow.test.model.storeLoad;

import java.io.File;
import java.nio.file.Files;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IRigidPin;
import org.jojo.flow.model.api.ISettings;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.storeLoad.DocumentString;
import org.jojo.flow.model.storeLoad.FlowDOM;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class XmlSerialTransformerTest {
    private static final File TRANSFORMER_JAR = new File("lib/xmlSerial.jar");

    @Test
    public void loadsXmlWithXmlSerializedData() throws Exception {
        Assumptions.assumeTrue(TRANSFORMER_JAR.isFile(),
                "lib/xmlSerial.jar is required for this test");
        final ISettings settings = ISettings.getDefaultImplementation();
        final File previousTransformer = settings.getLocationXMLSerialTransformerJar();
        final File file = Files.createTempFile("flow-xmlserial-", ".xml").toFile();
        try {
            settings.setLocationXMLSerialTransformerJar(TRANSFORMER_JAR);
            final String xml = export(createSourceChart());
            Assertions.assertTrue(xml.contains("&amp;lt;"),
                    "expected XML-serialized default data");

            final IFlowChart loaded = importXml(file, xml);
            assertChart(loaded);
        } finally {
            settings.setLocationXMLSerialTransformerJar(previousTransformer);
            file.delete();
        }
    }

    @Test
    public void loadsBase64DataWhileTransformerConfigured() throws Exception {
        Assumptions.assumeTrue(TRANSFORMER_JAR.isFile(),
                "lib/xmlSerial.jar is required for this test");
        final ISettings settings = ISettings.getDefaultImplementation();
        final File previousTransformer = settings.getLocationXMLSerialTransformerJar();
        final File file = Files.createTempFile("flow-base64-", ".xml").toFile();
        try {
            settings.setLocationXMLSerialTransformerJar(null);
            final String xml = export(createSourceChart());
            Assertions.assertTrue(xml.contains("rO0AB"),
                    "expected BASE64-serialized default data");

            settings.setLocationXMLSerialTransformerJar(TRANSFORMER_JAR);
            final IFlowChart loaded = importXml(file, xml);
            assertChart(loaded);
        } finally {
            settings.setLocationXMLSerialTransformerJar(previousTransformer);
            file.delete();
        }
    }

    private IFlowChart createSourceChart() throws Exception {
        final MockModule module = (MockModule) DynamicObjectLoader.loadModule(
                MockModule.class.getName(), 100);
        final IFlowChart source = new FlowChart(0, new FlowChartGR());
        source.addModule(module);
        source.addConnection(new DefaultArrow(1000, module.getDefaultOutputs().get(0),
                module.getDefaultInputs().get(0), "DefaultArrow"));
        final IRigidPin rigidFrom = (IRigidPin) module.getAllOutputs().get(1).getModulePinImp();
        final IRigidPin rigidTo = (IRigidPin) module.getAllInputs().get(2).getModulePinImp();
        source.addConnection(DynamicObjectLoader.loadConnection(2000, rigidFrom, rigidTo,
                "RigidConnection"));
        return source;
    }

    private String export(final IFlowChart source) {
        return new DocumentString(new FlowDOM(source.getDOM()).getDocument()).toString();
    }

    private IFlowChart importXml(final File file, final String xml) throws Exception {
        Files.writeString(file.toPath(), xml);
        new ModelFacade().setMainFlowChart(new FlowChart(0, new FlowChartGR()));
        return new StoreLoadFacade().loadFlowChart(file);
    }

    private void assertChart(final IFlowChart loaded) {
        Assertions.assertNotNull(loaded);
        Assertions.assertEquals(2, loaded.getConnections().size());
        for (final IConnection connection : loaded.getConnections()) {
            Assertions.assertEquals(100, connection.getFromPin().getModule().getId());
            Assertions.assertEquals(100, connection.getToPins().get(0).getModule().getId());
        }
    }
}
