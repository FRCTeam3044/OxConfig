package me.nabdev.oxconfig.sampleClasses;

import java.util.ArrayList;
import java.util.Collections;

import edu.wpi.first.math.controller.PIDController;
import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

public class ConfigurablePIDController extends PIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private String key;
    private String prettyName;
    private ArrayList<ConfigurableClassParam<?>> params = new ArrayList<ConfigurableClassParam<?>>();

    /**
     * Allocates a ConfigurablePIDController and registers it to OxConfig with the given constants for kp, ki, and kd and a default period of
     * 0.02 seconds.
     *
     * @param kp The proportional coefficient.
     * @param ki The integral coefficient.
     * @param kd The derivative coefficient.
     * @param key The yaml key for the controller to be stored in
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key) {
        super(kp, ki, kd);
        String[] keys = key.split("/");
        initialize(kp, ki, kd, key, keys[keys.length - 1]);
    }


    /**
     * Allocates a ConfigurablePIDController and registers it to OxConfig with the given constants for kp, ki, and kd and a default period of
     * 0.02 seconds.
     *
     * @param kp The proportional coefficient.
     * @param ki The integral coefficient.
     * @param kd The derivative coefficient.
     * @param key The yaml key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key, String prettyName) {
        super(kp, ki, kd);
        initialize(kp, ki, kd, key, prettyName);
    }

    private void initialize(double kp, double ki, double kd, String key, String prettyName){
        this.key = key;
        this.prettyName = prettyName;
        kpParam = new ConfigurableClassParam<Double>(this, kp, this::setP, "P");
        kiParam = new ConfigurableClassParam<Double>(this, ki, this::setI, "I");
        kdParam = new ConfigurableClassParam<Double>(this, kd, this::setD, "D");
        Collections.addAll(params, kpParam, kiParam, kdParam);
        OxConfig.registerConfigurableClass(this);
    }

    @Override
    public ArrayList<ConfigurableClassParam<?>> getParameters() {
        return params;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getPrettyName(){
        return prettyName;
    }
}
