/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.icon.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;

/**
 * ͼ�����
 * class��ʽ ��
 */
@Entity
@Table(name = "maintain_icon")
public class Icon extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;
    /**
     * ��ʶ ǰ̨ʹ��ʱ������
     */
    private String            identity;
    /**
     * ������
     */
    @Column(name = "css_class")
    private String            cssClass;

    //////�� class��css sprite ����ѡһ
    /**
     * ͼƬ��ַ
     */
    @Column(name = "img_src")
    private String            imgSrc;

    //////�� class��css sprite ����ѡһ
    /**
     * css ����ͼ λ��
     * ���Ե�ַ  ��http://a.com/a.png
     * ����������ĵ�ַ ��/a/a.png ����������
     */
    @Column(name = "sprite_src")
    private String            spriteSrc;

    /**
     * ����spriteͼƬ��߶���
     */
    @Column(name = "left")
    private Integer           left;

    /**
     * ����spriteͼƬ�ϱ߶���
     */
    @Column(name = "top")
    private Integer           top;

    /**
     * ���
     */
    private Integer           width;
    /**
     * �߶�
     */
    private Integer           height;

    /**
     * ������ӵ�css��ʽ
     */
    private String            style            = "";

    @Enumerated(EnumType.STRING)
    private IconType          type;

    /**
     * ����
     */
    private String            description;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getSpriteSrc() {
        return spriteSrc;
    }

    public void setSpriteSrc(String spriteSrc) {
        this.spriteSrc = spriteSrc;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public IconType getType() {
        return type;
    }

    public void setType(IconType type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
