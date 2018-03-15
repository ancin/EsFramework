package org.es.framework.common.repository.hibernate.type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.codec.binary.Hex;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

/***
 * Object���л�/�����л�
 * ���ݿ�����hex�ַ����洢
 * 
 * @author kejun.song
 * @version $Id: ObjectSerializeUserType.java, v 0.1 2014��11��19�� ����11:04:21 kejun.song Exp $
 */
public class ObjectSerializeUserType implements UserType, Serializable {

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
     * @throws HibernateException
     * @throws SQLException
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
                                                                                                     throws HibernateException,
                                                                                                     SQLException {
        ObjectInputStream ois = null;
        try {
            String hexStr = rs.getString(names[0]);
            ois = new ObjectInputStream(new ByteArrayInputStream(
                Hex.decodeHex(hexStr.toCharArray())));
            return ois.readObject();
        } catch (Exception e) {
            throw new HibernateException(e);
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * ����������Hibernate�������ݱ���ʱ������
     * ���ǿ���ͨ��PreparedStateme���Զ�������д�뵽��Ӧ�����ݿ���ֶ�
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SessionImplementor session) throws HibernateException, SQLException {
        ObjectOutputStream oos = null;
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(value);
                oos.close();

                byte[] objectBytes = bos.toByteArray();
                String hexStr = Hex.encodeHexString(objectBytes);

                st.setString(index, hexStr);
            } catch (Exception e) {
                throw new HibernateException(e);
            } finally {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
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
     * @throws HibernateException
     */
    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if (o == null)
            return null;
        return o;
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
