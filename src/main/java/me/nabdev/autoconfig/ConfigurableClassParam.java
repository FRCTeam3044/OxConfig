package me.nabdev.autoconfig;

import java.util.function.Consumer;

public class ConfigurableClassParam<T> implements Configurable<T> {
    private T value;
    private Consumer<T> setter;
    private String key;
    private String prettyName;
    private boolean simRealSpecific;

    @Override
    public void set(T val) {
        value = val;
        setter.accept(val);
    }
    
    @Override
    public T get() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public boolean isSimRealSpecific() {
        return simRealSpecific;
    }

    public String getPrettyName(){
        return prettyName;
    }

    /**
     * Creates a new ConfigurableParameter with the given value and key, and registers it with the AutoConfig
     * @param val Default value
     * @param setter Setter method for the value
     * @param key YAML key to register with (e.g. "driveTrain/maxSpeed")
     * @param simRealSpecific whether or not the value should be part of sim/real sub categories (true), or if it is universal (false)
     * @param prettyName the name to display in the In Control
     */
    public ConfigurableClassParam(T val, Consumer<T> setter, String key, boolean simRealSpecific, String prettyName) {
        value = val;
        this.setter = setter;
        this.key = key;
        this.simRealSpecific = simRealSpecific;
        this.prettyName = prettyName;
    }
}

