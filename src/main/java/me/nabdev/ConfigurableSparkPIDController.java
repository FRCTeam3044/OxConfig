package me.nabdev;

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

    ArrayList<ConfigurableClassParam<?>> params = new ArrayList<ConfigurableClassParam<?>>();

    private SparkMaxPIDController myController;

    private String key;
    private String prettyName;

    /**
     * Sets up a spark max PID controller to be auto-configured.
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     * @param simRealSpecific whether or not the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key, boolean simRealSpecific) {
        String[] keys = key.split("/");
        initialize(controller, key, keys[keys.length - 1], simRealSpecific);
    }

    /**
     * Sets up a spark max PID controller to be auto-configured. Default to sim/real specific
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key) {
        String[] keys = key.split("/");
        initialize(controller, key, keys[keys.length - 1], true);
    }

    /**
     * Sets up a spark max PID controller to be auto-configured.
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     * @param prettyName The display name of this controller
     * @param simRealSpecific whether or not the value should be part of sim/real sub categories (true), or if it is universal (false)
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key, String prettyName, boolean simRealSpecific) {
        initialize(controller, key, prettyName, simRealSpecific);
    }

    /**
     * Sets up a spark max PID controller to be auto-configured. Defaults to sim/real specific
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
        myController = controller;
        kpParam = new ConfigurableClassParam<Double>(0.0, controller::setP, key + "/p", simReal, "P");
        kiParam = new ConfigurableClassParam<Double>(0.0, controller::setI, key + "/i", simReal, "I");
        kdParam = new ConfigurableClassParam<Double>(0.0, controller::setD, key + "/d", simReal, "D");
        iZoneParam = new ConfigurableClassParam<Double>(0.0, controller::setIZone, key + "/izone", simReal, "I-Zone");
        FFParam = new ConfigurableClassParam<Double>(0.0, controller::setFF, key + "/ff", simReal, "FF");
        minParam = new ConfigurableClassParam<Double>(0.0, this::setMin, key + "/min", simReal, "Min");
        maxParam = new ConfigurableClassParam<Double>(0.0, this::setMax, key + "/max", simReal, "Max");
        Collections.addAll(params, kpParam, kiParam, kdParam, iZoneParam, FFParam, minParam, maxParam);
        AutoConfig.registerConfigurableClass(this);
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

    public void setMin(double min){
        myController.setOutputRange(min, myController.getOutputMax());
    }

    public void setMax(double max){
        myController.setOutputRange(myController.getOutputMin(), max);
    }

}
