package org.es.framework.common.plugin.entity;

/***
 *  <p>实体实现该接口表示想要逻辑删除
 * 为了简化开发 约定删除标识列名为deleted，如果想自定义删除的标识列名：
 * 1、使用注解元数据
 * 2、写一个 getColumn() 方法 返回列名
 * @author kejun.song
 * @version $Id: LogicDeleteable.java, v 0.1 2014年11月19日 上午10:48:53 kejun.song Exp $
 */
public interface LogicDeleteable {

    public Boolean getDeleted();

    public void setDeleted(Boolean deleted);

    public void markDeleted();

}
