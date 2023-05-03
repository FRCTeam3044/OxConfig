package me.nabdev.oxconfig;

import java.util.function.Consumer;

/**
 * A single parameter that can be configured by OxConfig.
 * If you need several instances of a class with several parameters, use ConfigurableClass instead.
 * @param <T> The type of the parameter
 */
public class ConfigurableParameter<T> implements Configurable<T> {
    T value;
    Consumer<T> setter;

    /**
     * Sets the value of the parameter and calls the setter method
     * Note: This does not save the value to the config file and will be overwritten on reload
     * @param val The new value
     */
    public void set(T val) {
        value = val;
        setter.accept(val);
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
        setter = (T t) -> {};
        OxConfig.registerParameter(key, this);
    }

    /**
     * Creates a new ConfigurableParameter with the given value and key, and registers it with the OxConfig
     * @param val Default value
     * @param key YAML key to register with (e.g. "driveTrain/maxSpeed")
     * @param callback Callback function on value change
     */
    public ConfigurableParameter(T val, String key, Consumer<T> callback) {
        value = val;
        setter = callback;
        OxConfig.registerParameter(key, this);
    }
}

