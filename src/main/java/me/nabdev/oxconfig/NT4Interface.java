package me.nabdev.oxconfig;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NT4Interface {
    private static NetworkTable table;

    static {
        table = NetworkTableInstance.getDefault().getTable("OxConfig");
        table.getEntry("KeySetter").setString("");
    }

    public static void updateConfig(String newConfig){
        table.getEntry("Raw").setString(newConfig);
    }

    public static void updateClasses(HashMap<String, ConfigurableClass> configurableClasses){
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

    public static String getSetKeys(){
        NetworkTableEntry keyEntry = table.getEntry("KeySetter");
        String key = keyEntry.getString("");
        if(!key.isEmpty()){
            keyEntry.setString("");
        }
        return key;
    }
}
