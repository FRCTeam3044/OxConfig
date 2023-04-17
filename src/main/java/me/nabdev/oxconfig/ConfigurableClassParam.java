package me.nabdev.oxconfig;

import java.util.function.Consumer;

public class ConfigurableClassParam<T> implements Configurable<T> {
    private T value;
    private Consumer<T> setter;
    private String key;
    private String prettyName;
    private ConfigurableClass myClass;

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
     * Creates a new ConfigurableClassParam with the given value and key
     * @param myClass The ConfigurableClass that this parameter belongs to
     * @param val Default value
     * @param setter Setter method for the value
     * @param key The yaml key for the value to be stored in (e.g. "kP")
     * @param prettyName The formatted name to display in the Tuner
     */
    public ConfigurableClassParam(ConfigurableClass myClass, T val, Consumer<T> setter, String key, String prettyName) {
        value = val;
        this.setter = setter;
        this.key = myClass.getKey() + "/" + key;
        this.simRealSpecific = myClass.isSimRealSpecific();
        this.prettyName = prettyName;
    }

    /**
     * Creates a new ConfigurableClassParam with the given value and key
     * @param myClass The ConfigurableClass that this parameter belongs to
     * @param val Default value
     * @param setter Setter method for the value
     * @param key The yaml key for the value to be stored in (e.g. "kP")
     */
    public ConfigurableClassParam(ConfigurableClass myClass, T val, Consumer<T> setter, String key) {
        value = val;
        this.setter = setter;
        this.key = myClass.getKey() + "/" + key;
        this.simRealSpecific = myClass.isSimRealSpecific();
        String[] split = key.split("/");
        this.prettyName = split[split.length - 1];
    }
}

