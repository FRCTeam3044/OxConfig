package me.nabdev.oxconfig;

public interface Configurable<T> {
    public T get();
    public void set(T val);
}
