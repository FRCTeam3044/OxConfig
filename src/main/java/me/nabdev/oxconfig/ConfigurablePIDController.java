package me.nabdev.oxconfig;

import java.util.ArrayList;

import edu.wpi.first.math.controller.PIDController;

public class ConfigurablePIDController extends PIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private String key;
    private String prettyName;
    private boolean isSimRealSpecific;

    /**
     * Allocates a ConfigurablePIDController and registers it to OxConfig with the given constants for kp, ki, and kd and a default period of
     * 0.02 seconds.
     *
     * @param kp The proportional coefficient.
     * @param ki The integral coefficient.
     * @param kd The derivative coefficient.
     * @param key The yaml key for the controller to be stored in
     * @param simRealSpecific whether the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key, boolean simRealSpecific) {
        super(kp, ki, kd);
        String[] keys = key.split("/");
        initialize(kp, ki, kd, key, keys[keys.length - 1], simRealSpecific);
    }

    /**
     * Allocates a ConfigurablePIDController and registers it to OxConfig with the given constants for kp, ki, and kd and a default period of
     * 0.02 seconds. DEFAULTS TO SIM/REAL SPECIFIC
     *
     * @param kp The proportional coefficient.
     * @param ki The integral coefficient.
     * @param kd The derivative coefficient.
     * @param key The yaml key for the controller to be stored in
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key) {
        super(kp, ki, kd);
        String[] keys = key.split("/");
        initialize(kp, ki, kd, key, keys[keys.length - 1], true);
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
     * @param simRealSpecific whether the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key, String prettyName, boolean simRealSpecific) {
        super(kp, ki, kd);
        initialize(kp, ki, kd, key, prettyName, simRealSpecific);
    }

    /**
     * Allocates a ConfigurablePIDController and registers it to OxConfig with the given constants for kp, ki, and kd and a default period of
     * 0.02 seconds. DEFAULTS TO SIM/REAL SPECIFIC
     *
     * @param kp The proportional coefficient.
     * @param ki The integral coefficient.
     * @param kd The derivative coefficient.
     * @param key The yaml key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key, String prettyName) {
        super(kp, ki, kd);
        initialize(kp, ki, kd, key, prettyName, true);
    }

    private void initialize(double kp, double ki, double kd, String key, String prettyName, boolean simReal){
        this.key = key;
        this.prettyName = prettyName;
        isSimRealSpecific = simReal;
        kpParam = new ConfigurableClassParam<Double>(this, kp, this::setP, "P");
        kiParam = new ConfigurableClassParam<Double>(this, ki, this::setI, "I");
        kdParam = new ConfigurableClassParam<Double>(this, kd, this::setD, "D");
        OxConfig.registerConfigurableClass(this);
    }

    @Override
    public ArrayList<ConfigurableClassParam<?>> getParameters() {
        ArrayList<ConfigurableClassParam<?>> list = new ArrayList<ConfigurableClassParam<?>>();
        list.add(kpParam);
        list.add(kiParam);
        list.add(kdParam);
        return list;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getPrettyName(){
        return prettyName;
    }

    @Override
    public boolean isSimRealSpecific() {
        return isSimRealSpecific;
    }

}
