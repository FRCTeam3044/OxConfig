package me.nabdev.oxconfig.sampleClasses;

import java.util.List;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

/**
 * A configurable Talon SRX or Victor SPX PID controller.
 * This class is an example of how to use the ConfigurableClass interface, but
 * is safe for competition use.
 */
public class ConfigurablePhoenixMotorPIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam = new ConfigurableClassParam<>(this, 0.0, this::setP, "P");
    private ConfigurableClassParam<Double> kiParam = new ConfigurableClassParam<>(this, 0.0, this::setI, "I");
    private ConfigurableClassParam<Double> kdParam = new ConfigurableClassParam<>(this, 0.0, this::setD, "D");
    private ConfigurableClassParam<Double> iZoneParam = new ConfigurableClassParam<>(this, 0.0, this::setIZone,
            "IZone");
    private ConfigurableClassParam<Double> FFParam = new ConfigurableClassParam<>(this, 0.0, this::setFF, "FF");

    private final List<ConfigurableClassParam<?>> params = List.of(kpParam, kiParam, kdParam, iZoneParam, FFParam);

    private BaseMotorController motorController;

    private String key;
    private String prettyName;

    /**
     * Sets up a Talon SRX PID controller to be autoconfigured.
     *
     * @param controller The Talon SRX motor controller to be configured
     * @param key        The json key for the controller to be stored in
     */
    public ConfigurablePhoenixMotorPIDController(BaseMotorController controller, String key) {
        String[] keys = key.split("/");
        this.key = key;
        this.prettyName = keys[keys.length - 1];
        motorController = controller;
        OxConfig.registerConfigurableClass(this);
    }

    /**
     * Sets up a Talon SRX PID controller to be autoconfigured.
     *
     * @param controller The Talon SRX motor controller to be configured
     * @param key        The json key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurablePhoenixMotorPIDController(BaseMotorController controller, String key, String prettyName) {
        this.key = key;
        this.prettyName = prettyName;
        motorController = controller;
        OxConfig.registerConfigurableClass(this);
    }

    private void setP(double p) {
        motorController.config_kP(0, p);
    }

    private void setI(double i) {
        motorController.config_kI(0, i);
    }

    private void setD(double d) {
        motorController.config_kD(0, d);
    }

    private void setIZone(double iZone) {
        motorController.config_IntegralZone(0, iZone);
    }

    private void setFF(double FF) {
        motorController.config_kF(0, FF);
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
