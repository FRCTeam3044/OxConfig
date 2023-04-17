package me.nabdev.oxconfig;

public class ConfigurableParameter<T> implements Configurable<T> {
    T value;
    public void set(T val) {
        value = val;
    }
    
    public T get() {
        return value;
    }

    /**
     * Creates a new ConfigurableParameter with the given value and key, and registers it with the OxConfig
     * @param val Default value
     * @param key YAML key to register with (e.g. "driveTrain/maxSpeed")
     * @param simRealSpecific whether the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurableParameter(T val, String key, boolean simRealSpecific) {
        value = val;
        OxConfig.registerParameter(key, this, simRealSpecific);
    }

    /**
     * Creates a new ConfigurableParameter with the given value and key, and registers it with the OxConfig
     * @param val Default value
     * @param key YAML key to register with (e.g. "driveTrain/maxSpeed")
     */
    public ConfigurableParameter(T val, String key) {
        value = val;
        OxConfig.registerParameter(key, this, false);
    }
}

