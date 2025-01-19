package me.nabdev.oxconfig.sampleClasses;

import java.util.ArrayList;
import java.util.Collections;

import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
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
public class ConfigurableSparkClosedLoop implements ConfigurableClass {
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
     * @param config     The config object for the controller
     * @param accessor   The accessor for the config object
     * @param controller The controller to be configured
     * @param key        The json key for the controller to be stored in
     */
    public ConfigurableSparkClosedLoop(SparkBaseConfig config, SparkBaseConfigAccessor accessor, SparkBase controller,
            String key) {
        initialize(config, accessor, controller, key, key);
    }

    /**
     * Sets up a spark max PID controller to be autoconfigured.
     * If you would like to use default values, set them before creating this
     * 
     * @param config     The config object for the controller
     * @param accessor   The accessor for the config object
     * @param controller The controller to be configured
     * @param key        The json key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurableSparkClosedLoop(SparkBaseConfig config, SparkBaseConfigAccessor accessor, SparkBase controller,
            String key, String prettyName) {
        initialize(config, accessor, controller, key, prettyName);
    }

    /**
     * Initializes the controller with the given parameters.
     * 
     * @param config     The spark config object for the controller
     * @param accessor   The accessor for the spark config object
     * @param controller The controller to be configured
     * @param key        The json key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    private void initialize(SparkBaseConfig config, SparkBaseConfigAccessor accessor, SparkBase controller, String key,
            String prettyName) {
        this.key = key;
        this.prettyName = prettyName;
        this.sparkConfig = config;
        this.myConfig = sparkConfig.closedLoop;

        ClosedLoopConfigAccessor closedLoop = accessor.closedLoop;

        kpParam = new ConfigurableClassParam<>(this, closedLoop.getP(), (Double val) -> {
            myConfig.p(val);
            controller.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        }, "P");
        kiParam = new ConfigurableClassParam<>(this, closedLoop.getI(), (Double val) -> {
            myConfig.i(val);
            controller.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        }, "I");
        kdParam = new ConfigurableClassParam<>(this, closedLoop.getD(), (Double val) -> {
            myConfig.d(val);
            controller.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        }, "D");
        iZoneParam = new ConfigurableClassParam<>(this, closedLoop.getIZone(), (Double val) -> {
            myConfig.iZone(val);
            controller.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        }, "IZone");
        FFParam = new ConfigurableClassParam<>(this, closedLoop.getFF(), (Double val) -> {
            myConfig.velocityFF(val);
            controller.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        }, "FF");
        minParam = new ConfigurableClassParam<>(this, closedLoop.getMinOutput(), (Double val) -> {
            myConfig.minOutput(val);
            controller.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        }, "Min");
        maxParam = new ConfigurableClassParam<>(this, closedLoop.getMaxOutput(), (Double val) -> {
            myConfig.maxOutput(val);
            controller.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        }, "Max");

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
