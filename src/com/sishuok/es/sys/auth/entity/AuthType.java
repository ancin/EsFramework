package com.sishuok.es.sys.auth.entity;

/**
 * ��Ȩ����
 * 
 */
public enum AuthType {

    user("�û�"), user_group("�û���"), organization_job("��֯�����͹���ְ��"), organization_group("��֯������");

    private final String info;

    private AuthType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
