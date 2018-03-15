package com.sishuok.es.showcase.excel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "showcase_excel_data")
public class ExcelData extends BaseEntity<Long> {

    @Column(name = "content")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }
}
