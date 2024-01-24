package me.nabdev.oxconfig;

import java.util.function.Consumer;

/**
 * A single parameter that can be configured by OxConfig.
 * If you need several instances of a class with several parameters, use
 * ConfigurableClass instead.
 * 
 * @param <T> The type of the parameter
 */
public class ConfigurableParameter<T> implements Configurable<T> {
    T value;
    Consumer<T> setter;

    /**
     * Sets the value of the parameter and calls the setter method
     * Note: This does not save the value to the config file and will be overwritten
     * on reload
     * 
     * @param val The new value
     */
    public void set(T val) {
        value = val;
        setter.accept(val);
    }

    /**
     * Gets the current value of the parameter since the last reload
     * 
     * @return The current value of the parameter
     */
    public T get() {
        return value;
    }

    /**
     * Whether or not a comment should be stored.
     * 
     * @return Whether or not a commentshould be stored.
     */
    @Override
    public boolean shouldStoreComment() {
        return true;
    }

    /**
     * Creates a new ConfigurableParameter with the given value and key, and
     * registers it with the OxConfig
     * 
     * @param val Default value, must not include commas
     * @param key json key to register with (e.g. "driveTrainMaxSpeed"), must not
     *            include commas
     */
    public ConfigurableParameter(T val, String key) {
        if (key.contains(",")) {
            throw new IllegalArgumentException("Key must not contain commas: " + key);
        }
        if (val.toString().contains(",")) {
            throw new IllegalArgumentException("Value must not contain commas: " + val);
        }
        value = val;
        setter = (T t) -> {
        };
        OxConfig.registerParameter(key, this);
    }

    /**
     * Creates a new ConfigurableParameter with the given value and key, and
     * registers it with the OxConfig
     * 
     * @param val      Default value, must not include commas
     * @param key      json key to register with (e.g. "driveTrain/maxSpeed"), must
     *                 not include commas
     * @param callback Callback function on value change
     */
    public ConfigurableParameter(T val, String key, Consumer<T> callback) {
        if (key.contains(",")) {
            throw new IllegalArgumentException("Key must not contain commas: " + key);
        }
        if (val.toString().contains(",")) {
            throw new IllegalArgumentException("Value must not contain commas: " + val);
        }
        value = val;
        setter = callback;
        OxConfig.registerParameter(key, this);
    }
}
