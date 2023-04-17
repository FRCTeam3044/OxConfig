package me.nabdev.oxconfig;

import java.util.ArrayList;

public interface ConfigurableClass {
    public ArrayList<ConfigurableClassParam<?>> getParameters();
    public String getKey();
    public String getPrettyName();
}
