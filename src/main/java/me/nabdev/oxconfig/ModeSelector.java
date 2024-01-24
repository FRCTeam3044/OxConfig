package me.nabdev.oxconfig;

import java.util.Arrays;

import edu.wpi.first.wpilibj.RobotBase;

// Using a configurable class for now. May change later once individual parameter editing is implemented

/**
 * Class for selecting the current mode of the robot.
 */
class ModeSelector {
    private String currentMode = "competition";
    private boolean hasInitialized = false;
    final ConfigurableParameter<String> modeParam = new ConfigurableParameter<>(modes[0], "root/mode", this::setMode);

    /**
     * Valid modes for the robot
     */
    public static String[] modes = {
            "competition", "testing", "presentation", "simulation"
    };

    // Should call reload in OxConfig
    void setMode(String mode) {
        if (!hasInitialized) {
            hasInitialized = true;
            if (RobotBase.isSimulation()) {
                modeParam.set("simulation");
                return;
            }
        }
        if (Arrays.asList(modes).contains(mode)) {
            if (!currentMode.equals(mode)) {
                currentMode = mode;
                if (OxConfig.hasInitialized) {
                    OxConfig.pendingNTUpdate = true;
                    OxConfig.reload();
                }
            }
        } else {
            Logger.logWarning("Invalid mode: " + mode + ". Valid modes are: " + String.join(", ", modes));
        }
    }

    /**
     * Gets the current mode of the robot
     * 
     * @return The current mode of the robot
     */
    public String getMode() {
        return currentMode;
    }
}
