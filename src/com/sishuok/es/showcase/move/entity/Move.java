package com.sishuok.es.showcase.move.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.entity.validate.group.Create;
import org.es.framework.common.plugin.entity.Movable;

@Entity
@Table(name = "showcase_moveable")
public class Move extends BaseEntity<Long> implements Movable {

    /**  */
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Create.class)
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
