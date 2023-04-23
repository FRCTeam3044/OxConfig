package me.nabdev.oxconfig.sampleClasses;

import java.util.ArrayList;
import java.util.Collections;

import com.revrobotics.SparkMaxPIDController;

import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

/**
 * A configurable spark max PID controller.
 * This class is an example of how to use the ConfigurableClass interface, but is safe for competition use.
 */
public class ConfigurableSparkPIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private ConfigurableClassParam<Double> iZoneParam;
    private ConfigurableClassParam<Double> FFParam;
    private ConfigurableClassParam<Double> minParam;
    private ConfigurableClassParam<Double> maxParam;

    private ArrayList<ConfigurableClassParam<?>> params = new ArrayList<ConfigurableClassParam<?>>();

    private SparkMaxPIDController myController;

    private String key;
    private String prettyName;

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key) {
        String[] keys = key.split("/");
        initialize(controller, key, keys[keys.length - 1]);
    }

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * @param controller The spark max controller to be configured
     * @param key The yaml key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurableSparkPIDController(SparkMaxPIDController controller, String key, String prettyName) {
        initialize(controller, key, prettyName);
    }

    private void initialize(SparkMaxPIDController controller, String key, String prettyName){
        this.key = key;
        this.prettyName = prettyName;
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

    /**
     * Sets the minimum output of the controller
     * @param min The minimum output
     */
    public void setMin(double min){
        myController.setOutputRange(min, myController.getOutputMax());
    }

    /**
     * Sets the maximum output of the controller
     * @param max The minimum output
     */
    public void setMax(double max){
        myController.setOutputRange(myController.getOutputMin(), max);
    }

}
