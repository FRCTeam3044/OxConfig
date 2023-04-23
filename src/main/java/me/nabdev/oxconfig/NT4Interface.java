package me.nabdev.oxconfig;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * A helper class for interfacing with NetworkTables
 */
public class NT4Interface {
    private static NetworkTable table;

    static {
        table = NetworkTableInstance.getDefault().getTable("OxConfig");
        table.getEntry("KeySetter").setString("");
    }

    static String getSetKeys(){
        NetworkTableEntry keyEntry = table.getEntry("KeySetter");
        String key = keyEntry.getString("");
        if(!key.isEmpty()){
            keyEntry.setString("");
        }
        return key;
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
                paramArr.put(param.getKey());
                paramArr.put(param.get().toString());
                classArr.put(paramArr);
            }
            classes.put(classArr);
        }
        table.getEntry("Classes").setString(classes.toString());
    }

    static void updateParameters(HashMap<String, ConfigurableParameter<?>> parameters){
        JSONArray params = new JSONArray();
        for(String paramKey : parameters.keySet()){
            JSONArray paramArr = new JSONArray();
            ConfigurableParameter<?> param = parameters.get(paramKey);
            paramArr.put(paramKey);
            paramArr.put(param.get());
            params.put(paramArr);
        }
        table.getEntry("Params").setString(params.toString());
    }
}
