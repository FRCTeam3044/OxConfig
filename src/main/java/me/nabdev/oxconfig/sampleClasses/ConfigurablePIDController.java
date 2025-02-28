package me.nabdev.oxconfig.sampleClasses;

import java.util.List;

import edu.wpi.first.math.controller.PIDController;
import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

/**
 * A WPILib PIDController that can be configured by OxConfig.
 * This class is an example of implementing ConfigurableClass, but is safe for
 * competition use.
 */
public class ConfigurablePIDController extends PIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam = new ConfigurableClassParam<>(this, 0.0, this::setP, "P");
    private ConfigurableClassParam<Double> kiParam = new ConfigurableClassParam<>(this, 0.0, this::setI, "I");
    private ConfigurableClassParam<Double> kdParam = new ConfigurableClassParam<>(this, 0.0, this::setD, "D");
    private String key;
    private String prettyName;
    private final List<ConfigurableClassParam<?>> params = List.of(kpParam, kiParam, kdParam);

    /**
     * Allocates a ConfigurablePIDController and registers it to OxConfig with the
     * given constants for kp, ki, and kd and a default period of
     * 0.02 seconds.
     *
     * @param kp  The proportional coefficient.
     * @param ki  The integral coefficient.
     * @param kd  The derivative coefficient.
     * @param key The json key for the controller to be stored in
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key) {
        super(kp, ki, kd);
        String[] keys = key.split("/");
        this.key = key;
        this.prettyName = keys[keys.length - 1];
        OxConfig.registerConfigurableClass(this);
    }

    /**
     * Allocates a ConfigurablePIDController and registers it to OxConfig with the
     * given constants for kp, ki, and kd and a default period of
     * 0.02 seconds.
     *
     * @param kp         The proportional coefficient.
     * @param ki         The integral coefficient.
     * @param kd         The derivative coefficient.
     * @param key        The json key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key, String prettyName) {
        super(kp, ki, kd);
        this.key = key;
        this.prettyName = prettyName;
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
}
