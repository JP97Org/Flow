package org.jojo.flow.model.api;

import java.io.File;

import org.jojo.flow.model.Settings;

/**
 * This interface represents program-wide settings.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface ISettings extends IAPI {
    
    /**
     * Gets the default implementation, which is the {@link org.jojo.flow.model.Settings} singleton.
     * 
     * @return
     */
    public static ISettings getDefaultImplementation() {
        return Settings.getInstance();
    }
    
    /**
     * Gets the location of the tmp directory which can be used for jar extraction 
     * or {@code null} if none is set.
     * 
     * @return the location of the tmp directory which can be used for jar extraction 
     * or {@code null} if none is set
     */
    File getLocationTmpDir();
    
    /**
     * Sets the location of the tmp directory which can be used for jar extraction.
     * 
     * @param location - the location of the tmp directory which can be used for jar extraction
     */
    void setLocationTmpDir(File location);
    
    /**
     * Gets the location of the XMLSerialTransformer jar or {@code null} if none is set.
     * 
     * @return the location of the XMLSerialTransformer jar or {@code null} if none is set
     */
    File getLocationXMLSerialTransformerJar();
    
    /**
     * Sets the location of the XMLSerialTransformer jar.
     * 
     * @param location - the location of the XMLSerialTransformer jar
     */
    void setLocationXMLSerialTransformerJar(File location);
    
    /**
     * Determines whether the location of the tmp dir has changed after the first setting.
     * 
     * @return whether the location of the tmp dir has changed after the first setting
     */
    boolean hasLocationTmpDirChanged();
}
