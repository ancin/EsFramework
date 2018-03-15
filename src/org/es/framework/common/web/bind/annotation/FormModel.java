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

package org.es.framework.common.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * <p>�����������ģ�ͣ����ұ�¶��ģ���й�ҳ��ʹ��</p>
 * 
 * @author kejun.song
 * @version $Id: FormModel.java, v 0.1 2014��11��19�� ����11:18:17 kejun.song Exp $
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormModel {

    /**
     * ָ�����������ǰ׺�ͱ�¶��ģ�Ͷ�������ֹ���ͼʹ��
     * <p/>
     * <p>1�������������ģ�ͣ��󶨹���<br/>
     * ���������<br>
     * <pre class="code">
     * <input name="student.name" value="Kate" /><br>
     * <input name="student.type" value="�Է�" /><br>
     * </pre>
     * ������������<br>
     * <pre class="code">
     *
     * @RequestMapping(value = "/test")
     * public String test(@FormModel("student") Student student) //��������  student.name student.type��������
     * </pre>
     * <p/>
     * ��springmvcĬ��<br>
     * ���������<br>
     * <pre class="code">
     * <input name="name" value="Kate" /><br>
     * <input name="type" value="�Է�" /><br>
     * </pre>
     * ������������<br>
     * <pre class="code">
     * public String test(@ModelAttribute("student") Student student) //��������name type��������
     * </pre>
     * <p/>
     * <p>������Բο�iteye�����⣺<a href="http://www.iteye.com/problems/89942">springMVC ���ݰ� ������� ���׼ȷ��</a>
     * <p/>
     * <p>2������value�е����ֱ�¶��ģ�Ͷ����й���ͼʹ��
     */
    String value();

}
