package me.nabdev.oxconfig.sampleClasses;

import java.util.ArrayList;
import java.util.Collections;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

/**
 * A WPILib PIDController that can be configured by OxConfig.
 * This class is an example of implementing ConfigurableClass, but is safe for
 * competition use.
 */
public class ConfigurableProfiledPIDController extends ProfiledPIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam;
    private ConfigurableClassParam<Double> kiParam;
    private ConfigurableClassParam<Double> kdParam;
    private String key;
    private String prettyName;
    private final ArrayList<ConfigurableClassParam<?>> params = new ArrayList<>();

    /**
     * Allocates a ConfigurableProfiledPIDController and registers it to OxConfig
     * with the given constants for kp, ki, and kd and a default period of 0.02
     * seconds. Unfortunatley, OxConfig is unable to configure the constraints, as
     * they cannot be changed.
     *
     * @param Kp          The proportional coefficient.
     * @param Ki          The integral coefficient.
     * @param Kd          The derivative coefficient.
     * @param constraints Velocity and acceleration constraints for goal.
     * @param key         The json key for the controller to be stored in
     */
    public ConfigurableProfiledPIDController(double Kp, double Ki, double Kd, TrapezoidProfile.Constraints constraints,
            String key) {
        super(Kp, Ki, Kd, constraints);
        initialize(Kp, Ki, Kd, key, key);
    }

    /**
     * Allocates a ConfigurableProfiledPIDController and registers it to OxConfig
     * with the given constants for kp, ki, and kd and a default period of 0.02
     * seconds. Unfortunatley, OxConfig is unable to configure the constraints, as
     * they cannot be changed.
     *
     * @param Kp          The proportional coefficient.
     * @param Ki          The integral coefficient.
     * @param Kd          The derivative coefficient.
     * @param constraints Velocity and acceleration constraints for goal.
     * @param key         The json key for the controller to be stored in
     * @param prettyName  The name to be displayed in the OxConfig GUI
     */
    public ConfigurableProfiledPIDController(double Kp, double Ki, double Kd, TrapezoidProfile.Constraints constraints,
            String key, String prettyName) {
        super(Kp, Ki, Kd, constraints);
        initialize(Kp, Ki, Kd, key, prettyName);
    }

    private void initialize(double kp, double ki, double kd, String key, String prettyName) {
        this.key = key;
        this.prettyName = prettyName;
        kpParam = new ConfigurableClassParam<>(this, kp, this::setP, "P");
        kiParam = new ConfigurableClassParam<>(this, ki, this::setI, "I");
        kdParam = new ConfigurableClassParam<>(this, kd, this::setD, "D");
        Collections.addAll(params, kpParam, kiParam, kdParam);
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
