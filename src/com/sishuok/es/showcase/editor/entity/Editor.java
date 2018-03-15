package com.sishuok.es.showcase.editor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;

@Entity
@Table(name = "showcase_editor")
public class Editor extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    @Column(name = "title")
    private String            title;

    @Column(name = "content")
    private String            content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
