package com.sishuok.es.showcase.status.audit.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.plugin.entity.Stateable;

@Entity
@Table(name = "showcase_status_audit")
public class Audit extends BaseEntity<Long> implements Stateable<Stateable.AuditStatus> {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String            name;

    @Enumerated(EnumType.STRING)
    private AuditStatus       status           = AuditStatus.waiting;

    private String            comment;

    @Override
    public AuditStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
