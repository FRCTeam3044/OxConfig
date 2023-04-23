package me.nabdev.oxconfig;

/**
 * A single parameter that can be configured by OxConfig.
 * If you need several parameters for one purpose, use the ConfigurableClass interface instead.
 * @param <T> The type of the parameter
 */
public class ConfigurableParameter<T> implements Configurable<T> {
    T value;

    /**
     * Sets the value of the parameter and calls the setter method
     * Note: This does not save the value to the config file and will be overwritten on reload
     * @param val The new value
     */
    public void set(T val) {
        value = val;
    }

    /**
     * Gets the current value of the parameter since the last reload
     * @return The current value of the parameter
     */
    public T get() {
        return value;
    }

    /**
     * Creates a new ConfigurableParameter with the given value and key, and registers it with the OxConfig
     * @param val Default value
     * @param key YAML key to register with (e.g. "driveTrain/maxSpeed")
     */
    public ConfigurableParameter(T val, String key) {
        value = val;
        OxConfig.registerParameter(key, this);
    }
}

