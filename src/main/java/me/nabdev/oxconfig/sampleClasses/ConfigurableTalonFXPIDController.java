package me.nabdev.oxconfig.sampleClasses;

import java.util.ArrayList;
import java.util.Collections;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

public class ConfigurableTalonFXPIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private ConfigurableClassParam<Double> iZoneParam;
    private ConfigurableClassParam<Double> FFParam;

    private ArrayList<ConfigurableClassParam<?>> params = new ArrayList<ConfigurableClassParam<?>>();

    private TalonFX motorController;

    private String key;
    private String prettyName;

    /**
     * Sets up a Talon FX PID controller to be autoconfigured.
     *
     * @param controller The Talon FX motor controller to be configured
     * @param key        The yaml key for the controller to be stored in
     */
    public ConfigurableTalonFXPIDController(TalonFX controller, String key) {
        String[] keys = key.split("/");
        initialize(controller, key, keys[keys.length - 1]);
    }

    /**
     * Sets up a Talon FX PID controller to be autoconfigured.
     *
     * @param controller The Talon FX motor controller to be configured
     * @param key        The yaml key for the controller to be stored in
     * @param prettyName The display name of this controller
     */
    public ConfigurableTalonFXPIDController(TalonFX controller, String key, String prettyName) {
        initialize(controller, key, prettyName);
    }

    private void initialize(TalonFX controller, String key, String prettyName) {
        this.key = key;
        this.prettyName = prettyName;
        motorController = controller;
        kpParam = new ConfigurableClassParam<>(this, 0.0, this::setP, "P");
        kiParam = new ConfigurableClassParam<>(this, 0.0, this::setI, "I");
        kdParam = new ConfigurableClassParam<>(this, 0.0, this::setD, "D");
        iZoneParam = new ConfigurableClassParam<>(this, 0.0, this::setIZone, "IZone");
        FFParam = new ConfigurableClassParam<>(this, 0.0, this::setFF, "FF");
        Collections.addAll(params, kpParam, kiParam, kdParam, iZoneParam, FFParam);
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
