package com.sishuok.es.maintain.icon.entity;

/**
 * ͼ������
 */
public enum IconType {
    css_class("css��ͼ��"), upload_file("�ļ�ͼ��"), css_sprite("css����ͼ��");

    private final String info;

    private IconType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
