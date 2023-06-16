package me.nabdev.oxconfig;

public class TaskTimer {
    private long lastCallTime;

    public TaskTimer(){
        lastCallTime = System.currentTimeMillis();
    }


    public void logTime(String key) {
        long cur = System.currentTimeMillis();
        NT4Interface.setProfilingTime(key, (double)(cur - lastCallTime));
        lastCallTime = cur;
    }
}
