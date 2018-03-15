package org.es.framework.common.repository.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import com.alibaba.fastjson.JSON;

/***
 * Json�ַ���---->Map
 * Map----->Json�ַ���
 * 
 * @author kejun.song
 * @version $Id: JsonMapUserType.java, v 0.1 2014��11��19�� ����11:03:00 kejun.song Exp $
 */
public class JsonMapUserType implements UserType, Serializable {

    //    private static ObjectMapper objectMapper = new ObjectMapper();

    /**  */
    private static final long serialVersionUID = 1L;

    static {
        //        objectMapper.enableDefaultTyping();
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return JsonMap.class;
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
    @SuppressWarnings("unchecked")
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
                                                                                                     throws HibernateException,
                                                                                                     SQLException {
        String json = rs.getString(names[0]);
        //        try {
        //            Map<Object, Object> map = objectMapper.readValue(json, HashMap.class);
        //            return new JsonMap(map);
        //        } catch (IOException e) {
        //            throw new HibernateException(e);
        //        }
        Map<Object, Object> map = JSON.parseObject(json, HashMap.class);
        return new JsonMap(map);
    }

    /**
     * ����������Hibernate�������ݱ���ʱ������
     * ���ǿ���ͨ��PreparedStateme���Զ�������д�뵽��Ӧ�����ݿ���ֶ�
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            //            try {
            //                st.setString(index, objectMapper.writeValueAsString((((JsonMap) value).getMap())));
            //            } catch (JsonProcessingException e) {
            //                throw new HibernateException(e);
            //            }
            st.setString(index, JSON.toJSONString((((JsonMap) value).getMap())));
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
        JsonMap map = new JsonMap();
        map.setMap(((JsonMap) o).getMap());
        return map;
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
