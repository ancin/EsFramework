package com.sishuok.es.showcase.status.show.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.plugin.entity.Stateable;

@Entity
@Table(name = "showcase_status_show")
public class Show extends BaseEntity<Long> implements Stateable<Stateable.ShowStatus> {

    /**  */
    private static final long serialVersionUID = 1L;

    private String            name;

    @Enumerated(EnumType.STRING)
    private ShowStatus        status;

    @Override
    public ShowStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ShowStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
