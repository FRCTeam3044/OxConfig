package me.nabdev.oxconfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import org.json.JSONArray;

import com.amihaiemil.eoyaml.YamlMapping;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * A helper class for interfacing with NetworkTables
 */
public class NT4Interface {
    private static final NetworkTable table;
    static boolean hasInitialized = false;

    static {
        table = NetworkTableInstance.getDefault().getTable("OxConfig");
    }

    static void initalize() {
        hasInitialized = true;
        table.getEntry("KeySetter").setString("");
        table.getEntry("ClassSetter").setString("");
        table.getEntry("ModeSetter").setString("");
        table.getEntry("Modes").setString(String.join(",", ModeSelector.modes));
    }

    static void setProfilingTime(String key, double time){
        table.getSubTable("Profiling").getEntry(key).setDouble(time);
    }

    static String getSetKeys(){
        NetworkTableEntry keyEntry = table.getEntry("KeySetter");
        String key = keyEntry.getString("");
        if(!key.isEmpty()){
            keyEntry.setString("");
        }
        return key;
    }

    static String getSetClasses(){
        NetworkTableEntry keyEntry = table.getEntry("ClassSetter");
        String key = keyEntry.getString("");
        if(!key.isEmpty()){
            keyEntry.setString("");
        }
        return key;
    }


    static String getSetModes(){
        NetworkTableEntry modeEntry = table.getEntry("ModeSetter");
        String mode = modeEntry.getString("");
        if(!mode.isEmpty()){
            modeEntry.setString("");
        }
        return mode;
    }

    static void updateClasses(HashMap<String, ConfigurableClass> configurableClasses){
        JSONArray classes = new JSONArray();
        for(String configClassKey : configurableClasses.keySet()){
            JSONArray classArr = new JSONArray();
            ConfigurableClass configClass = configurableClasses.get(configClassKey);
            classArr.put(configClass.getPrettyName());
            classArr.put(configClassKey);

            ArrayList<ConfigurableClassParam<?>> parameters = configClass.getParameters();
            for(ConfigurableClassParam<?> param : parameters){
                JSONArray paramArr = new JSONArray();
                paramArr.put(param.getPrettyName());
                String key = param.getKey();
                paramArr.put(key);
                paramArr.put(param.get().getClass().getSimpleName());
                String[] keys = key.split("/");
                for(String mode : ModeSelector.modes){
                    YamlMapping curModeNested = OxConfig.getNestedValue(mode + "/" + key, OxConfig.config);
                    paramArr.put(curModeNested.string(keys[keys.length - 1]));
                }
                classArr.put(paramArr);
            }
            classes.put(classArr);
        }
        table.getEntry("Classes").setString(classes.toString());
    }

    static void updateParameters(HashMap<String, ConfigurableParameter<?>> parameters){
        JSONArray params = new JSONArray();
        for(String paramKey : parameters.keySet()){
            String[] keys = paramKey.split("/");
            JSONArray paramArr = new JSONArray();
            ConfigurableParameter<?> param = parameters.get(paramKey);
            paramArr.put(paramKey);
            YamlMapping nested = OxConfig.getNestedValue(OxConfig.appendModeIfNotRoot(paramKey), OxConfig.config);
            String finalKey = keys[keys.length - 1];
            paramArr.put(nested.value(finalKey).comment().value());
            // Put the type of the parameter
            paramArr.put(param.get().getClass().getSimpleName());
            if(keys[0].equals("root")){
                paramArr.put(param.get());
            } else {
                for(String mode : ModeSelector.modes){
                    YamlMapping curModeNested = OxConfig.getNestedValue(mode + "/" + paramKey, OxConfig.config);
                    paramArr.put(curModeNested.string(keys[keys.length - 1]));
                }
            }
            params.put(paramArr);
        }
        table.getEntry("Params").setString(params.toString());
    }

    static void updateRaw(YamlMapping raw){
        String string = raw.toString();
        if(!Objects.equals(string, table.getEntry("Raw").getString(""))) {
            String timestamp = String.valueOf(new Date().getTime());
            table.getEntry("Raw").setString(timestamp + "," + raw);
        }
    }

    static void updateMode(){
        table.getEntry("CurrentMode").setString(OxConfig.modeSelector.getMode());
    }
}
