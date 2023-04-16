package me.nabdev;

public interface Configurable<T> {
    public T get();
    public void set(T val);
}
