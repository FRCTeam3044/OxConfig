package me.nabdev;

import java.util.ArrayList;

import edu.wpi.first.math.controller.PIDController;

public class ConfigurablePIDController extends PIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private String key;
    private String prettyName;

    /**
     * Allocates a PIDController with the given constants for kp, ki, and kd and a default period of
     * 0.02 seconds.
     *
     * @param kp The proportional coefficient.
     * @param ki The integral coefficient.
     * @param kd The derivative coefficient.
     * @param key The yaml key for the controller to be stored in
     * @param simRealSpecific whether or not the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key, boolean simRealSpecific) {
        super(kp, ki, kd);
        String[] keys = key.split("/");
        initialize(kp, ki, kd, key, keys[keys.length - 1], simRealSpecific);
    }

    /**
     * Allocates a PIDController with the given constants for kp, ki, and kd and a default period of
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
     * Allocates a PIDController with the given constants for kp, ki, and kd and a default period of
     * 0.02 seconds.
     *
     * @param kp The proportional coefficient.
     * @param ki The integral coefficient.
     * @param kd The derivative coefficient.
     * @param key The yaml key for the controller to be stored in
     * @param prettyName The display name of this controller
     * @param simRealSpecific whether or not the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurablePIDController(double kp, double ki, double kd, String key, String prettyName, boolean simRealSpecific) {
        super(kp, ki, kd);
        initialize(kp, ki, kd, key, prettyName, simRealSpecific);
    }

    /**
     * Allocates a PIDController with the given constants for kp, ki, and kd and a default period of
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
        kpParam = new ConfigurableClassParam<Double>(kp, this::setP, key + "/p", simReal, "P");
        kiParam = new ConfigurableClassParam<Double>(ki, this::setI, key + "/i", simReal, "I");
        kdParam = new ConfigurableClassParam<Double>(kd, this::setD, key + "/d", simReal, "D");
        AutoConfig.registerConfigurableClass(this);
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

}
