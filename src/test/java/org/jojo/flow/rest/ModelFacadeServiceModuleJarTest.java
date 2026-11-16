package org.jojo.flow.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import jakarta.ws.rs.WebApplicationException;

import org.jojo.flow.model.api.IDynamicClassLoader;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.rest.dto.AddModuleRequest;
import org.jojo.flow.rest.dto.FlowChartDto;
import org.jojo.flow.rest.dto.ModuleJarUploadResultDto;
import org.jojo.flow.rest.dto.PointDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ModelFacadeServiceModuleJarTest {
    private static final String MODULE_CLASS_NAME = "generatedmodule.UploadedTestModule";

    @TempDir
    Path tempDir;

    @Test
    public void loadsModuleJarAtRuntimeAndRejectsDuplicateJar() throws Exception {
        final Path jar = buildModuleJar();

        final ModelFacadeService service = new ModelFacadeService();
        service.init();

        final ModuleJarUploadResultDto result = service.loadModuleJar("uploaded-test.jar", jar);

        assertEquals(1, result.moduleClasses().size());
        assertEquals(MODULE_CLASS_NAME, result.moduleClasses().get(0).className());
        assertFalse(result.moduleClasses().get(0).builtIn());
        assertTrue(service.getModuleClasses().stream()
                .anyMatch(moduleClass -> MODULE_CLASS_NAME.equals(moduleClass.className())));
        assertNotNull(DynamicObjectLoader.loadModule(
                IDynamicClassLoader.DEFAULT_DYNAMIC_LOADER.getClassLoader(), MODULE_CLASS_NAME, 42));
        assertNotNull(DynamicObjectLoader.loadModule(MODULE_CLASS_NAME, 43));

        service.createNewMainFlowChart();
        assertNotNull(service.addModule(
                new AddModuleRequest(MODULE_CLASS_NAME, new PointDto(1, 1))));
        final String xml = service.exportMainFlowChartXml();
        final FlowChartDto imported = service.importMainFlowChartXml(xml);
        assertEquals(1, imported.modules().size());
        assertEquals(MODULE_CLASS_NAME, imported.modules().get(0).className());

        final WebApplicationException duplicate = assertThrows(WebApplicationException.class,
                () -> service.loadModuleJar("uploaded-test.jar", jar));
        assertEquals(409, duplicate.getResponse().getStatus());
    }

    private Path buildModuleJar() throws IOException {
        final Path source = tempDir.resolve("generatedmodule/UploadedTestModule.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package generatedmodule;

                import java.awt.Point;
                import java.awt.Window;

                import org.jojo.flow.model.flowChart.GraphicalRepresentation;
                import org.jojo.flow.model.flowChart.modules.ExternalConfig;
                import org.jojo.flow.model.flowChart.modules.ModuleGR;
                import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;

                public class UploadedTestModule extends MockModule {
                    private final CustomGR gr;

                    public UploadedTestModule(int id, ExternalConfig externalConfig) {
                        super(id, externalConfig);
                        this.gr = new CustomGR(this, new Point(0, 0), 10, 10, "Uploaded");
                    }

                    @Override
                    public GraphicalRepresentation getGraphicalRepresentation() {
                        return gr;
                    }

                    public static class CustomGR extends ModuleGR {
                        public CustomGR(UploadedTestModule module, Point position, int height,
                                int width, String iconText) {
                            super(position, height, width, iconText);
                            setModule(module);
                        }

                        @Override
                        public String getInfoText() {
                            return "info: uploaded";
                        }

                        @Override
                        public Window getInternalConfigWindow() {
                            return null;
                        }
                    }
                }
                """);

        final Path classes = tempDir.resolve("classes");
        Files.createDirectories(classes);
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "A JDK is required to compile the test module");
        final int compilationResult = compiler.run(null, null, null,
                "-classpath", System.getProperty("java.class.path"),
                "-d", classes.toString(),
                source.toString());
        assertEquals(0, compilationResult, "The test module did not compile");

        final Path jar = tempDir.resolve("uploaded-test.jar");
        try (OutputStream output = Files.newOutputStream(jar);
                JarOutputStream jarOutput = new JarOutputStream(output);
                Stream<Path> paths = Files.walk(classes)) {
            for (final Path path : paths.filter(Files::isRegularFile).toList()) {
                final String entryName = classes.relativize(path).toString()
                        .replace(File.separatorChar, '/');
                jarOutput.putNextEntry(new JarEntry(entryName));
                Files.copy(path, jarOutput);
                jarOutput.closeEntry();
            }
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
        return jar;
    }
}
