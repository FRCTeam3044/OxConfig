package me.nabdev.oxconfig;

import java.util.ArrayList;

/**
 * Interface for classes that can be automatically configured by OxConfig
 * Classes that implement this should have multiple configurable parameters
 * and should use the ConfigurableClassParam class to store them. If you only
 * need one configurable parameter, use the ConfigurableParameter class instead.
 */
public interface ConfigurableClass {
    /**
     * Gets the parameters of the configurable class
     * @return The parameters of the configurable class
     */
    public ArrayList<ConfigurableClassParam<?>> getParameters();
    /**
     * Gets the key of the configurable class (e.g. "subsystems/arm")
     * @return The key of the configurable class
     */
    public String getKey();
    /**
     * Gets the pretty name of the configurable class (e.g. "Arm")
     * @return The pretty name of the configurable class
     */
    public String getPrettyName();
}
