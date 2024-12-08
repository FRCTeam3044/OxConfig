package me.nabdev.oxconfig.sampleClasses;

import java.util.ArrayList;
import java.util.Collections;

import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.ClosedLoopConfigAccessor;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfigAccessor;

import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

/**
 * A configurable spark max PID controller.
 * This class is an example of how to use the ConfigurableClass interface, but
 * is safe for competition use.
 */
public class ConfigureableSparkClosedLoop implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private ConfigurableClassParam<Double> iZoneParam;
    private ConfigurableClassParam<Double> FFParam;
    private ConfigurableClassParam<Double> minParam;
    private ConfigurableClassParam<Double> maxParam;

    private final ArrayList<ConfigurableClassParam<?>> params = new ArrayList<>();

    private SparkBaseConfig sparkConfig;
    private ClosedLoopConfig myConfig;

    private String key;
    private String prettyName;

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * If you would like to use default values, set them before creating this
     * 
     * @param config   The config object for the controller
     * @param accessor The accessor for the config object
     * @param key      The json key for the controller to be stored in
     */
    public ConfigureableSparkClosedLoop(SparkBaseConfig config, SparkBaseConfigAccessor accessor, String key) {
        initialize(config, accessor, key, key);
    }

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * If you would like to use default values, set them before creating this
     * 
     * @param config     The config object for the controller
     * @param accessor   The accessor for the config object
     * @param key        The json key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigureableSparkClosedLoop(SparkBaseConfig config, SparkBaseConfigAccessor accessor, String key,
            String prettyName) {
        initialize(config, accessor, key, prettyName);
    }

    /**
     * Initializes the controller with the given parameters.
     * 
     * @param config     The spark config object for the controller
     * @param accessor   The accessor for the spark config object
     * @param key        The json key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    private void initialize(SparkBaseConfig config, SparkBaseConfigAccessor accessor, String key,
            String prettyName) {
        this.key = key;
        this.prettyName = prettyName;
        this.sparkConfig = config;
        this.myConfig = sparkConfig.closedLoop;

        ClosedLoopConfigAccessor closedLoop = accessor.closedLoop;

        kpParam = new ConfigurableClassParam<>(this, closedLoop.getP(), myConfig::p, "P");
        kiParam = new ConfigurableClassParam<>(this, closedLoop.getI(), myConfig::i, "I");
        kdParam = new ConfigurableClassParam<>(this, closedLoop.getD(), myConfig::d, "D");
        iZoneParam = new ConfigurableClassParam<>(this, closedLoop.getIZone(), myConfig::iZone, "IZone");
        FFParam = new ConfigurableClassParam<>(this, closedLoop.getFF(), myConfig::velocityFF, "FF");
        minParam = new ConfigurableClassParam<>(this, closedLoop.getMinOutput(), myConfig::minOutput, "Min");
        maxParam = new ConfigurableClassParam<>(this, closedLoop.getMaxOutput(), myConfig::maxOutput, "Max");

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
}
