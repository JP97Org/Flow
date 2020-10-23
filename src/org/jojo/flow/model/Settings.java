package org.jojo.flow.model;

import java.io.File;

import org.jojo.flow.model.api.ISettings;

/**
 * This class represents the settings of the model.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class Settings implements ISettings {
    private static Settings settings;
    
    private File locationTmpDir;
    private File locationXMLSerialTransformerJar;
    
    private Settings() {
        
    }
    
    public static synchronized Settings getInstance() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    @Override
    public File getLocationTmpDir() {
        return this.locationTmpDir;
    }

    @Override
    public void setLocationTmpDir(File location) {
        this.locationTmpDir = location;
    }

    @Override
    public File getLocationXMLSerialTransformerJar() {
        return this.locationXMLSerialTransformerJar;
    }

    @Override
    public void setLocationXMLSerialTransformerJar(File location) {
        this.locationXMLSerialTransformerJar = location;
    }
}
