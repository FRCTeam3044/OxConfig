package me.nabdev.oxconfig.sampleClasses;

import java.util.List;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import me.nabdev.oxconfig.ConfigurableClass;
import me.nabdev.oxconfig.ConfigurableClassParam;
import me.nabdev.oxconfig.OxConfig;

/**
 * A WPILib ProfiledPIDController that can be configured by OxConfig.
 * This class is an example of implementing ConfigurableClass, but is safe for
 * competition use.
 */
public class ConfigurableProfiledPIDController extends ProfiledPIDController implements ConfigurableClass {
    private ConfigurableClassParam<Double> kpParam = new ConfigurableClassParam<>(this, 0.0, this::setP, "P");
    private ConfigurableClassParam<Double> kiParam = new ConfigurableClassParam<>(this, 0.0, this::setI, "I");
    private ConfigurableClassParam<Double> kdParam = new ConfigurableClassParam<>(this, 0.0, this::setD, "D");
    private String key;
    private String prettyName;
    private final List<ConfigurableClassParam<?>> params = List.of(kpParam, kiParam, kdParam);

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
        this.key = key;
        this.prettyName = key;
        OxConfig.registerConfigurableClass(this);
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
        String[] keys = key.split("/");
        this.key = key;
        this.prettyName = keys[keys.length - 1];
        OxConfig.registerConfigurableClass(this);
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
