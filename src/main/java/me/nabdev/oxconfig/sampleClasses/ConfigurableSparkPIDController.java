package me.nabdev.oxconfig.sampleClasses;

import java.util.ArrayList;
import java.util.Collections;

import com.revrobotics.SparkPIDController;

import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

/**
 * A configurable spark max PID controller.
 * This class is an example of how to use the ConfigurableClass interface, but
 * is safe for competition use.
 */
public class ConfigurableSparkPIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private ConfigurableClassParam<Double> iZoneParam;
    private ConfigurableClassParam<Double> FFParam;
    private ConfigurableClassParam<Double> minParam;
    private ConfigurableClassParam<Double> maxParam;

    private final ArrayList<ConfigurableClassParam<?>> params = new ArrayList<>();

    private SparkPIDController myController;

    private String key;
    private String prettyName;

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * If you would like to use default values, set them before creating this
     * 
     * @param controller The spark max controller to be configured
     * @param key        The json key for the controller to be stored in
     */
    public ConfigurableSparkPIDController(SparkPIDController controller, String key) {
        initialize(controller, key, key);
    }

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * If you would like to use default values, set them before creating this
     * 
     * @param controller The spark max controller to be configured
     * @param key        The json key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurableSparkPIDController(SparkPIDController controller, String key, String prettyName) {
        initialize(controller, key, prettyName);
    }

    /**
     * Initializes the controller with the given parameters.
     * 
     * @param controller The spark max controller to be configured
     * @param key        The json key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    private void initialize(SparkPIDController myController, String key, String prettyName) {
        this.key = key;
        this.prettyName = prettyName;
        this.myController = myController;
        kpParam = new ConfigurableClassParam<>(this, myController.getP(), myController::setP, "P");
        kiParam = new ConfigurableClassParam<>(this, myController.getI(), myController::setI, "I");
        kdParam = new ConfigurableClassParam<>(this, myController.getD(), myController::setD, "D");
        iZoneParam = new ConfigurableClassParam<>(this, myController.getIZone(), myController::setIZone, "IZone");
        FFParam = new ConfigurableClassParam<>(this, myController.getFF(), myController::setFF, "FF");
        minParam = new ConfigurableClassParam<>(this, myController.getOutputMin(), this::setMin, "Min");
        maxParam = new ConfigurableClassParam<>(this, myController.getOutputMax(), this::setMax, "Max");
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
    public String getPrettyName() {
        return prettyName;
    }

    /**
     * Sets the minimum output of the controller
     * 
     * @param min The minimum output
     */
    void setMin(double min) {
        myController.setOutputRange(min, myController.getOutputMax());
    }

    /**
     * Sets the maximum output of the controller
     * 
     * @param max The minimum output
     */
    void setMax(double max) {
        myController.setOutputRange(myController.getOutputMin(), max);
    }
}
