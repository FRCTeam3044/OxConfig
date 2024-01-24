package me.nabdev.oxconfig;

import java.util.function.Consumer;

/**
 * Parameters for use with ConfigurableClass
 * @param <T> The type of the parameter
 */
public class ConfigurableClassParam<T> implements Configurable<T> {
    private T value;
    private final Consumer<T> setter;
    private final String key;
    private final String prettyName;

    /**
     * Sets the value of the parameter and calls the setter method
     * Note: This does not save the value to the config file and will be overwritten on reload
     * @param val The new value
     */
    @Override
    public void set(T val) {
        value = val;
        setter.accept(val);
    }

    /**
     * Gets the current value of the parameter since the last reload
     * @return The current value of the parameter
     */
    @Override
    public T get() {
        return value;
    }

    String getKey() {
        return key;
    }

    String getPrettyName(){
        return prettyName;
    }

    /**
     * Creates a new ConfigurableClassParam with the given value and key
     * @param myClass The ConfigurableClass that this parameter belongs to
     * @param val Default value
     * @param setter Setter method for the value
     * @param key The json key for the value to be stored in (e.g. "kP"), must not include commas
     */
    public ConfigurableClassParam(ConfigurableClass myClass, T val, Consumer<T> setter, String key) {
        if(key.contains(",")){
            throw new IllegalArgumentException("Key must not contain commas: " + key);
        }
        if(val.toString().contains(",")){
            throw new IllegalArgumentException("Value must not contain commas: " + val);
        }
        if(myClass.getKey().contains(",")){
            throw new IllegalArgumentException("Key must not contain commas: " + myClass.getKey());
        }
        value = val;
        this.setter = setter;
        this.key = myClass.getKey() + "/" + key;
        String[] split = key.split("/");
        this.prettyName = split[split.length - 1];
    }

    /**
     * Creates a new ConfigurableClassParam with the given value and key
     * @param myClass The ConfigurableClass that this parameter belongs to
     * @param val Default value
     * @param key The json key for the value to be stored in (e.g. "kP"), must not include commas
     */
    public ConfigurableClassParam(ConfigurableClass myClass, T val, String key) {
        if(key.contains(",")){
            throw new IllegalArgumentException("Key must not contain commas: " + key);
        }
        if(val.toString().contains(",")){
            throw new IllegalArgumentException("Value must not contain commas: " + val);
        }
        if(myClass.getKey().contains(",")){
            throw new IllegalArgumentException("Key must not contain commas: " + myClass.getKey());
        }
        value = val;
        this.setter = (T t) -> {};
        this.key = myClass.getKey() + "/" + key;
        String[] split = key.split("/");
        this.prettyName = split[split.length - 1];
    }
}

