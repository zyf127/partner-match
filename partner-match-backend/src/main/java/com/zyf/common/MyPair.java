package com.zyf.common;

public class MyPair<F, S> {
    private final F key;
    private final S value;

    public MyPair(F key, S value) {
        this.key = key;
        this.value = value;
    }

    public F getKey() {
        return key;
    }

    public S getValue() {
        return value;
    }
}
