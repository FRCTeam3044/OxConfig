package me.nabdev.oxconfig;

interface Configurable<T> {
    T get();
    void set(T val);
}
