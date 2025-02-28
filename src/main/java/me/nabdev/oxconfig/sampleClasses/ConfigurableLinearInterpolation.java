package me.nabdev.oxconfig.sampleClasses;

import java.util.List;

import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

/**
 * A configurable linear interpolation function.
 * This class is an example of implementing ConfigurableClass, but is safe for
 * competition use.
 */
public class ConfigurableLinearInterpolation implements ConfigurableClass {
    private ConfigurableClassParam<Double> x1 = new ConfigurableClassParam<Double>(this, 0.0, (x) -> {
    }, "x1");
    private ConfigurableClassParam<Double> y1 = new ConfigurableClassParam<Double>(this, 0.0, (x) -> {
    }, "y1");
    private ConfigurableClassParam<Double> x2 = new ConfigurableClassParam<Double>(this, 0.0, (x) -> {
    }, "x2");
    private ConfigurableClassParam<Double> y2 = new ConfigurableClassParam<Double>(this, 0.0, (x) -> {
    }, "y2");
    private String key;
    private String prettyName;
    private final List<ConfigurableClassParam<?>> params = List.of(x1, y1, x2, y2);

    /**
     * Allocates a ConfigurableLinearInterpolation and registers it to OxConfig
     * 
     * @param key The json key for the controller to be stored in
     */
    public ConfigurableLinearInterpolation(String key) {
        this.key = key;
        this.prettyName = key;
        OxConfig.registerConfigurableClass(this);

    }

    @Override
    public List<ConfigurableClassParam<?>> getParameters() {
        return params;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getPrettyName() {
        return prettyName;
    }

    /**
     * Calculates the y value of the linear interpolation function at the given x
     * 
     * @param x The x value to calculate
     * @return The y value of the linear interpolation function at the given x
     */
    public double calculate(double x) {
        return y1.get() + (y2.get() - y1.get()) * (x - x1.get()) / (x2.get() - x1.get());
    }

    /**
     * Gets the y1 value
     * 
     * @return The y1 value
     */
    public double getY1() {
        return y1.get();
    }

    /**
     * Gets the y2 value
     * 
     * @return The y2 value
     */
    public double getY2() {
        return y2.get();
    }

    /**
     * Gets the x1 value
     * 
     * @return The x1 value
     */
    public double getX1() {
        return x1.get();
    }

    /**
     * Gets the x2 value
     * 
     * @return The x2 value
     */
    public double getX2() {
        return x2.get();
    }
}
