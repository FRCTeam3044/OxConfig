package me.nabdev.oxconfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;

import com.amihaiemil.eoyaml.YamlMapping;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * A helper class for interfacing with NetworkTables
 */
class NT4Interface {
    private static final NetworkTable table;
    private static final NetworkTableEntry paramsEntry;
    private static final NetworkTableEntry modesEntry;
    private static final NetworkTableEntry keySetterEntry;
    private static final NetworkTableEntry classSetterEntry;
    private static final NetworkTableEntry modeSetterEntry;
    private static final NetworkTableEntry rawEntry;
    private static final NetworkTableEntry isInitializedEntry;

    private static final NetworkTableEntry currentModeEntry;
    private static final NetworkTable profiling;
    static boolean hasInitialized = false;

    static {
        table = NetworkTableInstance.getDefault().getTable("OxConfig");
        paramsEntry = table.getEntry("Params");
        modesEntry = table.getEntry("Modes");
        keySetterEntry = table.getEntry("KeySetter");
        classSetterEntry = table.getEntry("ClassSetter");
        modeSetterEntry = table.getEntry("ModeSetter");
        rawEntry = table.getEntry("Raw");
        currentModeEntry = table.getEntry("CurrentMode");
        profiling = table.getSubTable("Profiling");
        isInitializedEntry = table.getEntry("IsInitialized");
    }

    static void initialize() {
        hasInitialized = true;
        keySetterEntry.setString("");
        classSetterEntry.setString("");
        modeSetterEntry.setString("");
        modesEntry.setString(String.join(",", ModeSelector.modes));
    }

    static void setProfilingTime(String key, double time){
        profiling.getEntry(key).setDouble(time);
    }

    static void setInitialized(boolean initialized){
        isInitializedEntry.setBoolean(initialized);
    }

    static String getSetKeys(){
        String key = keySetterEntry.getString("");
        if(!key.isEmpty()){
            keySetterEntry.setString("");
        }
        return key;
    }

    static String getSetClasses(){
        String key = classSetterEntry.getString("");
        if(!key.isEmpty()){
            classSetterEntry.setString("");
        }
        return key;
    }


    static String getSetModes(){
        String mode = modeSetterEntry.getString("");
        if(!mode.isEmpty()){
            modeSetterEntry.setString("");
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
                for(String mode : ModeSelector.modes){
                    YamlMapping curModeMap = YamlUtils.getModeMap(mode);
                    paramArr.put(curModeMap.string(key));
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
            if(paramKey.equalsIgnoreCase("root/mode")){
                continue;
            }
            JSONArray paramArr = new JSONArray();
            ConfigurableParameter<?> param = parameters.get(paramKey);
            paramArr.put(paramKey);

            YamlMapping nested = YamlUtils.getModeMap(OxConfig.modeSelector.getMode());
            paramArr.put(nested.value(paramKey).comment().value());
            // Put the type of the parameter
            paramArr.put(param.get().getClass().getSimpleName());
            for(String mode : ModeSelector.modes){
                YamlMapping curModeNested = YamlUtils.getModeMap(mode);
                paramArr.put(curModeNested.string(paramKey));
            }
            params.put(paramArr);
        }
        paramsEntry.setString(params.toString());
    }

    static void updateRaw(String raw){
        String timestamp = String.valueOf(new Date().getTime());
        rawEntry.setString(timestamp + "," + raw);
    }

    static void updateMode(){
        currentModeEntry.setString(OxConfig.modeSelector.getMode());
    }
}
