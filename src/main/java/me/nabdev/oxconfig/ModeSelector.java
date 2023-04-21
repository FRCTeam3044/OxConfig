package me.nabdev.oxconfig;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Using a configurable class for now. May change later once individual parameter editing is implemented
 */
public class ModeSelector implements ConfigurableClass {
    private static ModeSelector instance;
    private ConfigurableClassParam<String> modeParam = new ConfigurableClassParam<String>(this, "testing", this::setMode, "mode");
    private String currentMode = "testing";

    // Register configurable class
    public ModeSelector(){
        OxConfig.registerConfigurableClass(this);
    }

    public static ModeSelector getInstance() {
        if(instance == null) {
            instance = new ModeSelector();
        }
        return instance;
    }

    // Represent current mode (presentation, competition, testing, etc.)
    public static String[] modes = {
        "presentation", "competition", "testing"
    };

    // Return an arraylist with the mode parameter
    @Override
    public ArrayList<ConfigurableClassParam<?>> getParameters() {
        ArrayList<ConfigurableClassParam<?>> params = new ArrayList<ConfigurableClassParam<?>>();
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
    public void setMode(String mode) {
        if(Arrays.asList(modes).contains(mode)){
            if(!currentMode.equals(mode)) {
                currentMode = mode;
                if (OxConfig.hasInitialized) OxConfig.reload();
            }
        } else {
            System.out.println("Invalid mode: " + mode);
        }
    }

    // Should return a string representing the current mode
    public String getMode() {
        return currentMode;
    }
}
