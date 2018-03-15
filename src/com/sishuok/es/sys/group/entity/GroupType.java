package com.sishuok.es.sys.group.entity;

public enum GroupType {

    user("�û���"), organization("��֯������");

    private final String info;

    private GroupType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
