package com.sishuok.es.showcase.parentchild.entity;

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
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "showcase_parent")
public class Parent extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    @NotNull(message = "not.null")
    @Column(name = "name")
    private String            name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ParentChildType   type;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "beginDate")
    @Temporal(TemporalType.DATE)
    private Date              beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "endDate")
    @Temporal(TemporalType.DATE)
    private Date              endDate;

    @Column(name = "is_show")
    private Boolean           show;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParentChildType getType() {
        return type;
    }

    public void setType(ParentChildType type) {
        this.type = type;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
