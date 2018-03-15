package com.sishuok.es.showcase.parentchild.entity;

public enum ParentChildType {
    type1("��������1"), type2("��������2");

    private final String info;

    private ParentChildType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
