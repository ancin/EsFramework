
package org.es.framework.common.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/***
 * <p> ����ʵ����࣬�ṩͳһ��ID������صĻ������ܷ���<p/>
 * <p> �������mysql�����Զ����������ģ���ο�{@link BaseEntity}
 * ����ֻ��Ҫ����ͷ�ϼ� @SequenceGenerator(name="seq", sequenceName="���sequence����")
 * <p/>
 * 
 * @author kejun.song
 * @version $Id: BaseOracleEntity.java, v 0.1 2014��11��19�� ����10:15:08 kejun.song Exp $
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
