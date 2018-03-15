package org.es.framework.common.plugin.entity;

/***
 * <p>实体实现该接口表示想要调整数据的顺序<p/>
 * <p>优先级值越大则展示时顺序越靠前 比如 2 排在 1 前边<p/>
 * 
 * 
 * @author kejun.song
 * @version $Id: Movable.java, v 0.1 2014年11月19日 上午10:49:29 kejun.song Exp $
 */
public interface Movable {

    public Integer getWeight();

    public void setWeight(Integer weight);

}
