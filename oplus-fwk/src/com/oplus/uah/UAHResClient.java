package com.oplus.uah;

public class UAHResClient {

    private UAHResClient(Class cls) {}

    public static UAHResClient get(Class cls) {
        return new UAHResClient(cls);
    }

    public void release(int handle) {}
}
