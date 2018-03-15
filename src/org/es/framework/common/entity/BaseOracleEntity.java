
package org.es.framework.common.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/***
 * <p> 抽象实体基类，提供统一的ID，和相关的基本功能方法<p/>
 * <p> 如果是如mysql这种自动生成主键的，请参考{@link BaseEntity}
 * 子类只需要在类头上加 @SequenceGenerator(name="seq", sequenceName="你的sequence名字")
 * <p/>
 * 
 * @author kejun.song
 * @version $Id: BaseOracleEntity.java, v 0.1 2014年11月19日 上午10:15:08 kejun.song Exp $
 */
@MappedSuperclass
public abstract class BaseOracleEntity<PK extends Serializable> extends AbstractEntity<PK> {

    /**  */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private PK id;

    @Override
    public PK getId() {
        return id;
    }

    @Override
    public void setId(PK id) {
        this.id = id;
    }
}
