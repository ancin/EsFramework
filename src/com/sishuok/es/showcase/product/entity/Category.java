package com.sishuok.es.showcase.product.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.plugin.entity.Movable;

@Entity
@Table(name = "showcase_category")
public class Category extends BaseEntity<Long> implements Movable {

    /**  */
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String            name;

    @Column(name = "weight")
    private Integer           weight;

    @Column(name = "is_show")
    private Boolean           show;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
