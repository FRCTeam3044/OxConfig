package me.nabdev.oxconfig;

class TaskTimer {
    public static boolean nt;

    private long lastCallTime;

    public TaskTimer(){
        if(!OxConfig.isProfiling) return;
        lastCallTime = System.currentTimeMillis();
    }

    public void logTime(String key) {
        if(!OxConfig.isProfiling) return;
        long cur = System.currentTimeMillis();
        if(nt) NT4Interface.setProfilingTime(key, (double)(cur - lastCallTime));
        else System.out.println(key + ": " + (cur - lastCallTime));
        lastCallTime = cur;
    }

    public void reset(){
        if(!OxConfig.isProfiling) return;
        lastCallTime = System.currentTimeMillis();
    }
}
