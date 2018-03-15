/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.es.framework.common.repository.support.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.es.framework.common.repository.callback.SearchCallback;

/**
 * ����Ĭ�ϵĸ���������ѯ����
 *
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SearchableQuery {

    /**
     * ����Ĭ�ϵĲ�ѯ����ql
     *
     * @return
     */
    String findAllQuery() default "";

    /**
     * ����Ĭ�ϵ�ͳ������ql
     *
     * @return
     */
    String countAllQuery() default "";

    /**
     * ��qlƴ��������ֵ�Ļص�����
     *
     * @return com.sishuok.es.common.repository.callback.SearchCallback����
     */
    Class<? extends SearchCallback> callbackClass() default SearchCallback.class;

    QueryJoin[] joins() default {};

}
