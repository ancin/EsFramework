package org.es.framework.common.repository.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/***
 * ��Listת��Ϊָ���ָ����ָ����ַ����洢 List��Ԫ������ֻ֧�ֳ������������� 
 * �ɲο�{@link org.apache.commons.beanutils.ConvertUtilsBean}
 * 
 * @author kejun.song
 * @version $Id: HashMapToStringUserType.java, v 0.1 2014��11��19�� ����11:01:40 kejun.song Exp $
 */
@SuppressWarnings("serial")
public class HashMapToStringUserType implements UserType, ParameterizedType, Serializable {

    /**
     * Ĭ�� java.lang.String
     */
    private Class<?> keyType;

    @Override
    public void setParameterValues(Properties parameters) {
        String keyType = (String) parameters.get("keyType");
        if (!StringUtils.isEmpty(keyType)) {
            try {
                this.keyType = Class.forName(keyType);
            } catch (ClassNotFoundException e) {
                throw new HibernateException(e);
            }
        } else {
            this.keyType = String.class;
        }

    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return HashMap.class;
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
                                                                                                     throws HibernateException,
                                                                                                     SQLException {
        String valueStr = rs.getString(names[0]);
        if (StringUtils.isEmpty(valueStr)) {
            return newMap();
        }

        Map map = JSONObject.parseObject(valueStr);
        Map result = newMap();
        try {
            for (Object key : map.keySet()) {
                Object value = map.get(key);
                result.put(keyType.getConstructor(String.class).newInstance(key), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HibernateException(e);
        }
        return result;
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

    @SuppressWarnings("rawtypes")
    private Map newMap() {
        try {
            return HashMap.class.newInstance();
        } catch (Exception e) {
            throw new HibernateException(e);
        }
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if (o == null)
            return null;
        Map copyMap = newMap();
        copyMap.putAll((Map) o);
        return copyMap;
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
