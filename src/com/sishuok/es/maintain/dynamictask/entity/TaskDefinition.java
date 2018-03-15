package com.sishuok.es.maintain.dynamictask.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;

/**
 * ��ʱ���� beanName.beanMethod �� beanClass.beanMethod ��ѡһ
 * 
 */
@Entity
@Table(name = "maintain_task_definition")
public class TaskDefinition extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String            name;

    /**
     * cron���ʽ
     */
    @Column(name = "cron")
    private String            cron;

    /**
     * Ҫִ�е�class����
     */
    @Column(name = "bean_class")
    private String            beanClass;

    /**
     * Ҫִ�е�bean����
     */
    @Column(name = "bean_name")
    private String            beanName;

    /**
     * Ҫִ�е�bean�ķ�����
     */
    @Column(name = "method_name")
    private String            methodName;

    /**
     * �Ƿ�������
     */
    @Column(name = "is_start")
    private Boolean           start            = Boolean.FALSE;

    /**
     * ����
     */
    @Column(name = "description")
    private String            description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Boolean getStart() {
        return start;
    }

    public void setStart(Boolean start) {
        this.start = start;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
