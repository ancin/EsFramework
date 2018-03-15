package com.sishuok.es.sys.user.entity;

public enum UserStatus {

    normal("Õý³£×´Ì¬"), blocked("·â½û×´Ì¬");

    private final String info;

    private UserStatus(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
