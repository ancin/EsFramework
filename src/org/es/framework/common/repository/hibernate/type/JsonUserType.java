package org.es.framework.common.repository.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/***
 * ������ ת��ΪJson�ַ���
 * 
 * @author kejun.song
 * @version $Id: JsonUserType.java, v 0.1 2014��11��19�� ����11:03:50 kejun.song Exp $
 */
public class JsonUserType implements UserType, Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return Object.class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        if (o == o1) {
            return true;
        }
        if (o == null || o == null) {
            return false;
        }

        return o.equals(o1);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    /**
     * ��JDBC ResultSet��ȡ����,����ת��Ϊ�Զ������ͺ󷵻�
     * (�˷���Ҫ��Կ��ܳ���nullֵ���д���)
     * names�а����˵�ǰ�Զ������͵�ӳ���ֶ�����
     *
     * @param names
     * @param owner
     * @return
     * @throws org.hibernate.HibernateException
     * @throws java.sql.SQLException
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
                                                                                                     throws HibernateException,
                                                                                                     SQLException {
        String valueStr = rs.getString(names[0]);
        if (StringUtils.isEmpty(valueStr)) {
            return null;
        }

        return JSONObject.parse(valueStr);
    }

    /**
     * ����������Hibernate�������ݱ���ʱ������
     * ���ǿ���ͨ��PreparedStateme���Զ�������д�뵽��Ӧ�����ݿ���ֶ�
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SessionImplementor session) throws HibernateException, SQLException {
        String valueStr;
        if (value == null) {
            valueStr = "";
        } else {
            valueStr = JSONObject.toJSONString(value, SerializerFeature.WriteClassName);
        }
        st.setString(index, valueStr);
    }

    /**
     * �ṩ�Զ������͵���ȫ���Ʒ���
     * ���������ù��췵�ض���
     * ��nullSafeGet��������֮�����ǻ�����Զ������ݶ��������û������Զ�������֮ǰ��
     * deepCopy�����������ã����������Զ������ݶ�����һ����ȫ�����������˿������ظ��û�
     * ��ʱ���Ǿ͵õ����Զ������ݶ���������汾����һ���Ǵ����ݿ������ԭʼ�汾�����������ͨ��
     * deepCopy��������ĸ��ư汾��ԭʼ�İ汾����Hibernateά�������ư����û�ʹ�á�ԭʼ�汾����
     * �Ժ�������ݼ�����ݣ�Hibernate���������ݼ������н������汾�����ݽ��жԱȣ�ͨ������
     * equals��������������ݷ����˱仯��equals��������false������ִ�ж�Ӧ�ĳ־û�����
     *
     * @param o
     * @return
     * @throws org.hibernate.HibernateException
     */
    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if (o == null)
            return null;
        String jsonStr = JSONObject.toJSONString(o, SerializerFeature.WriteClassName);
        return JSONObject.parse(jsonStr);
    }

    /**
     * ������ʵ���Ƿ�ɱ�
     *
     * @return
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /* ���л� */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return ((Serializable) value);
    }

    /* �����л� */
    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
