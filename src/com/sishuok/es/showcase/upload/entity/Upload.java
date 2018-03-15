/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.showcase.upload.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;

@Entity
@Table(name = "showcase_upload")
public class Upload extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String            name;

    @Column(name = "src")
    private String            src;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
