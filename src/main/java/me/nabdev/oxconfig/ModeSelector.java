package me.nabdev.oxconfig;

import java.util.Arrays;

import edu.wpi.first.wpilibj.RobotBase;

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
            "competition", "presentation", "simulation"
    };

    /**
     * Set the current OxConfig mode. Is not saved to the config file, and will be
     * overwritten on code restart, file reload, or mode changed over nt.
     * 
     * @param mode The mode to switch to
     */
    public void setMode(String mode) {
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
                    synchronized (OxConfig.class) {
                        OxConfig.pendingNTUpdate = true;
                    }
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
