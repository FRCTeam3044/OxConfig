package me.nabdev.oxconfig;

interface Configurable<T> {
    public T get();
    public void set(T val);
}
