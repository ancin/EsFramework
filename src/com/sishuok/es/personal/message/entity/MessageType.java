package com.sishuok.es.personal.message.entity;

public enum MessageType {
    user_message("��ͨ��Ϣ"), system_message("ϵͳ��Ϣ");

    private final String info;

    private MessageType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

}
