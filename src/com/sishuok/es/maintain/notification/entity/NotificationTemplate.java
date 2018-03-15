package com.sishuok.es.maintain.notification.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.plugin.entity.LogicDeleteable;
import org.hibernate.validator.constraints.Length;

/**
 * ��Ϣ֪ͨģ��
 */
@Entity
@Table(name = "maintain_notification_template")
public class NotificationTemplate extends BaseEntity<Long> implements LogicDeleteable {

    /**  */
    private static final long  serialVersionUID = 1L;

    /**
     * ģ������ ����Ψһ ����ʱʹ��
     */
    @NotNull(message = "{not.null}")
    @Length(min = 1, max = 100, message = "{length.not.valid}")
    private String             name;

    /**
     * ����ϵͳ
     */
    @NotNull(message = "{not.null}")
    @Enumerated(EnumType.STRING)
    private NotificationSystem system;

    /**
     * ģ�����
     */
    @Length(min = 1, max = 200, message = "{length.not.valid}")
    private String             title;

    /**
     * ģ������
     */
    private String             template;

    /**
     * �Ƿ����߼�ɾ��
     */
    private Boolean            deleted          = Boolean.FALSE;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public NotificationSystem getSystem() {
        return system;
    }

    public void setSystem(final NotificationSystem system) {
        this.system = system;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(final String template) {
        this.template = template;
    }

    @Override
    public Boolean getDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void markDeleted() {
        setDeleted(Boolean.TRUE);
    }

}
