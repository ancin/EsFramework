package com.sishuok.es.sys.organization.entity;

/**
 * ��֯��������
 * <p>Version: 1.0
 */
public enum OrganizationType {
    bloc("����"), branch_office("�ֹ�˾"), department("����"), group("����С��");

    private final String info;

    private OrganizationType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
