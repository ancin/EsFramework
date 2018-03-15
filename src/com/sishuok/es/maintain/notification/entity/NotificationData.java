package com.sishuok.es.maintain.notification.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.es.framework.common.entity.BaseEntity;

@Entity
@Table(name = "maintain_notification_data")
public class NotificationData extends BaseEntity<Long> {

    /**  */
    private static final long  serialVersionUID = 1L;

    @NotNull(message = "{not.null}")
    @Column(name = "user_id")
    private Long               userId;

    @NotNull(message = "{not.null}")
    @Enumerated(EnumType.STRING)
    private NotificationSystem system;

    private String             title;

    private String             content;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date               date;

    @Column(name = "is_read")
    private Boolean            read             = Boolean.FALSE;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
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

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(final Boolean read) {
        this.read = read;
    }
}
