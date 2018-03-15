package org.es.framework.common.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>�ȴӲ����ң������Ҳ����ӷ������ң�����ʹ��Ĭ�ϵĲ�ѯ����</p>
 * <pre>
 *     ��ʽ���£�
 *     value = {"baseInfo.age_lt=123", "name_like=abc", "id_in=1,2,3,4"}
 * </pre>
 *
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SearchableDefaults {

    /**
     * Ĭ�ϲ�ѯ�����ַ���
     *
     * @return
     */
    String[] value() default {};

    /**
     * �Ƿ�ϲ�Ĭ�ϵ����Զ����
     *
     * @return
     */
    boolean merge() default false;

    /**
     * �Ƿ���Ҫ��ҳ
     *
     * @return
     */
    boolean needPage() default true;

    /**
     * �Ƿ���Ҫ����
     *
     * @return
     */
    boolean needSort() default true;
}
