package com.sishuok.es.showcase.deleted.entity;

public enum Sex {
    male("��"), female("Ů");
    private final String info;

    private Sex(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
