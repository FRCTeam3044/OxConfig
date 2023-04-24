package me.nabdev.oxconfig;

import java.util.ArrayList;
import java.util.Arrays;


// Using a configurable class for now. May change later once individual parameter editing is implemented

/**
 * Class for selecting the current mode of the robot.
 */
public class ModeSelector implements ConfigurableClass {
    private static ModeSelector instance;
    private ConfigurableClassParam<String> modeParam = new ConfigurableClassParam<>(this, "testing", this::setMode, "mode");
    private String currentMode = "testing";

    ModeSelector(){
        OxConfig.registerConfigurableClass(this);
    }

    static ModeSelector getInstance() {
        if(instance == null) {
            instance = new ModeSelector();
        }
        return instance;
    }

    /**
     * Valid modes for the robot
     */
    public static String[] modes = {
        "presentation", "competition", "testing", "simulation"
    };

    // Return an arraylist with the mode parameter
    @Override
    public ArrayList<ConfigurableClassParam<?>> getParameters() {
        ArrayList<ConfigurableClassParam<?>> params = new ArrayList<>();
        params.add(modeParam);
        return params;
    }

    // Return the key of the mode in the config
    @Override
    public String getKey() {
        return "root/Mode";
    }

    // Returns the name of the Mode chooser for display in the Tuner
    @Override
    public String getPrettyName() {
        return "Mode";
    }

    // Should call reload in OxConfig
    void setMode(String mode) {
        if(Arrays.asList(modes).contains(mode)){
            if(!currentMode.equals(mode)) {
                currentMode = mode;
                if (OxConfig.hasInitialized) OxConfig.reload();
            }
        } else {
            System.out.println("Invalid mode: " + mode);
        }
    }

    /**
     * Gets the current mode of the robot
     * @return The current mode of the robot
     */
    public String getMode() {
        return currentMode;
    }
}
