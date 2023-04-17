package me.nabdev.oxconfig;

import java.util.ArrayList;
import java.util.Collections;

import com.revrobotics.SparkMaxPIDController;

public class ConfigurableSparkPIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private ConfigurableClassParam<Double> iZoneParam;
    private ConfigurableClassParam<Double> FFParam;
    private ConfigurableClassParam<Double> minParam;
    private ConfigurableClassParam<Double> maxParam;

    private ArrayList<ConfigurableClassParam<?>> params;

    private SparkMaxPIDController myController;

    private String key;
    private String prettyName;
    private boolean isSimRealSpecific;

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     * @param simRealSpecific whether the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key, boolean simRealSpecific) {
        String[] keys = key.split("/");
        initialize(controller, key, keys[keys.length - 1], simRealSpecific);
    }

    /**
     * Sets up a spark max PID controller to be autoconfigured. Default to sim/real specific
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key) {
        String[] keys = key.split("/");
        initialize(controller, key, keys[keys.length - 1], true);
    }

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     * @param prettyName The display name of this controller
     * @param simRealSpecific whether the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key, String prettyName, boolean simRealSpecific) {
        initialize(controller, key, prettyName, simRealSpecific);
    }

    /**
     * Sets up a spark max PID controller to be autoconfigured. Defaults to sim/real specific
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key, String prettyName) {
        initialize(controller, key, prettyName, true);
    }

    private void initialize(SparkMaxPIDController controller, String key, String prettyName, boolean simReal){
        this.key = key;
        this.prettyName = prettyName;
        this.isSimRealSpecific = simReal;
        myController = controller;
        kpParam = new ConfigurableClassParam<Double>(this, 0.0, controller::setP, "P");
        kiParam = new ConfigurableClassParam<Double>(this, 0.0, controller::setI, "I");
        kdParam = new ConfigurableClassParam<Double>(this, 0.0, controller::setD, "D");
        iZoneParam = new ConfigurableClassParam<Double>(this, 0.0, controller::setIZone, "IZone");
        FFParam = new ConfigurableClassParam<Double>(this, 0.0, controller::setFF, "FF");
        minParam = new ConfigurableClassParam<Double>(this, 0.0, this::setMin, "Min");
        maxParam = new ConfigurableClassParam<Double>(this, 0.0, this::setMax, "Max");
        Collections.addAll(params, kpParam, kiParam, kdParam, iZoneParam, FFParam, minParam, maxParam);
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

    @Override
    public boolean isSimRealSpecific() {
        return isSimRealSpecific;
    }

    public void setMin(double min){
        myController.setOutputRange(min, myController.getOutputMax());
    }

    public void setMax(double max){
        myController.setOutputRange(myController.getOutputMin(), max);
    }

}
