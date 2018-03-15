package org.es.framework.common.repository.support.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.criteria.JoinType;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: QueryJoin.java, v 0.1 2014年11月19日 上午11:07:39 kejun.song Exp $
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryJoin {

    String property();

    JoinType joinType();

}
