package com.sishuok.es.maintain.notification.entity;

/**
 * ��������ϵͳ
 */
public enum NotificationSystem {

    system("ϵͳ"), excel("excel");

    private final String info;

    private NotificationSystem(final String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

}
