package org.es.framework.common.repository.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/***
 * ��Listת��Ϊָ���ָ����ָ����ַ����洢 List��Ԫ������ֻ֧�ֳ������������� 
 * �ɲο�{@link org.apache.commons.beanutils.ConvertUtilsBean}
 * 
 * @author kejun.song
 * @version $Id: CollectionToStringUserType.java, v 0.1 2014��11��19�� ����10:59:58 kejun.song Exp $
 */
public class CollectionToStringUserType implements UserType, ParameterizedType, Serializable {

    /**  */
    private static final long serialVersionUID = 1L;
    /**
     * Ĭ��,
     */
    private String            separator;
    /**
     * Ĭ�� java.lang.Long
     */
    private Class<?>          elementType;
    /**
     * Ĭ�� ArrayList
     */
    private Class<?>          collectionType;

    @Override
    public void setParameterValues(Properties parameters) {
        String separator = (String) parameters.get("separator");
        if (!StringUtils.isEmpty(separator)) {
            this.separator = separator;
        } else {
            this.separator = ",";
        }

        String collectionType = (String) parameters.get("collectionType");
        if (!StringUtils.isEmpty(collectionType)) {
            try {
                this.collectionType = Class.forName(collectionType);
            } catch (ClassNotFoundException e) {
                throw new HibernateException(e);
            }
        } else {
            this.collectionType = java.util.ArrayList.class;
        }

        String elementType = (String) parameters.get("elementType");
        if (!StringUtils.isEmpty(elementType)) {
            try {
                this.elementType = Class.forName(elementType);
            } catch (ClassNotFoundException e) {
                throw new HibernateException(e);
            }
        } else {
            this.elementType = Long.TYPE;
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return collectionType;
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
            return newCollection();
        }

        String[] values = StringUtils.split(valueStr, separator);

        Collection result = newCollection();

        for (String value : values) {
            if (StringUtils.isNotEmpty(value)) {
                result.add(ConvertUtils.convert(value, elementType));
            }
        }
        return result;

    }

    @SuppressWarnings("rawtypes")
    private Collection newCollection() {
        try {
            return (Collection) collectionType.newInstance();
        } catch (Exception e) {
            throw new HibernateException(e);
        }
    }

    /**
     * ����������Hibernate�������ݱ���ʱ������
     * ���ǿ���ͨ��PreparedStateme���Զ�������д�뵽��Ӧ�����ݿ���ֶ�
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SessionImplementor session) throws HibernateException, SQLException {
        String valueStr;
        if (value == null) {
            valueStr = "";
        } else {
            valueStr = StringUtils.join((Collection) value, separator);
        }
        if (StringUtils.isNotEmpty(valueStr)) {
            valueStr = valueStr + ",";
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if (o == null)
            return null;
        Collection copyCollection = newCollection();
        copyCollection.addAll((Collection) o);
        return copyCollection;
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
