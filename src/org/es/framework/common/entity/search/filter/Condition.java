package org.es.framework.common.entity.search.filter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.common.entity.search.SearchOperator;
import org.es.framework.common.entity.search.exception.InvlidSearchOperatorException;
import org.es.framework.common.entity.search.exception.SearchException;
import org.springframework.util.Assert;

/***
 * <p>��ѯ��������</p>
 * 
 * @author kejun.song
 * @version $Id: Condition.java, v 0.1 2014��11��19�� ����10:27:45 kejun.song Exp $
 */
public final class Condition implements SearchFilter {

    //��ѯ�����ָ���
    public static final String separator = "_";

    private final String       key;
    private String             searchProperty;
    private SearchOperator     operator;
    private Object             value;

    /**
     * ���ݲ�ѯkey��ֵ����Condition
     *
     * @param key   �� name_like
     * @param value
     * @return
     */
    @SuppressWarnings("rawtypes")
    static Condition newCondition(final String key, final Object value) throws SearchException {

        Assert.notNull(key, "Condition key must not null");

        String[] searchs = StringUtils.split(key, separator);

        if (searchs.length == 0) {
            throw new SearchException("Condition key format must be : property or property_op");
        }

        String searchProperty = searchs[0];

        SearchOperator operator = null;
        if (searchs.length == 1) {
            operator = SearchOperator.custom;
        } else {
            try {
                operator = SearchOperator.valueOf(searchs[1]);
            } catch (IllegalArgumentException e) {
                throw new InvlidSearchOperatorException(searchProperty, searchs[1]);
            }
        }

        boolean allowBlankValue = SearchOperator.isAllowBlankValue(operator);
        boolean isValueBlank = (value == null);
        isValueBlank = isValueBlank
                       || (value instanceof String && StringUtils.isBlank((String) value));
        isValueBlank = isValueBlank || (value instanceof List && ((List) value).size() == 0);
        //���˵���ֵ�����������ѯ
        if (!allowBlankValue && isValueBlank) {
            return null;
        }

        Condition searchFilter = newCondition(searchProperty, operator, value);

        return searchFilter;
    }

    /**
     * ���ݲ�ѯ���ԡ���������ֵ����Condition
     *
     * @param searchProperty
     * @param operator
     * @param value
     * @return
     */
    static Condition newCondition(final String searchProperty, final SearchOperator operator,
                                  final Object value) {
        return new Condition(searchProperty, operator, value);
    }

    /**
     * @param searchProperty ������
     * @param operator       ����
     * @param value          ֵ
     */
    private Condition(final String searchProperty, final SearchOperator operator, final Object value) {
        this.searchProperty = searchProperty;
        this.operator = operator;
        this.value = value;
        this.key = this.searchProperty + separator + this.operator;
    }

    public String getKey() {
        return key;

    }

    public String getSearchProperty() {
        return searchProperty;
    }

    /**
     * ��ȡ ������
     *
     * @return
     */
    public SearchOperator getOperator() throws InvlidSearchOperatorException {
        return operator;
    }

    /**
     * ��ȡ�Զ����ѯʹ�õĲ�����
     * 1�����Ȼ�ȡǰ̨����
     * 2�����ؿ�
     *
     * @return
     */
    public String getOperatorStr() {
        if (operator != null) {
            return operator.getSymbol();
        }
        return "";
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public void setOperator(final SearchOperator operator) {
        this.operator = operator;
    }

    public void setSearchProperty(final String searchProperty) {
        this.searchProperty = searchProperty;
    }

    /**
     * �õ�ʵ��������
     *
     * @return
     */
    public String getEntityProperty() {
        return searchProperty;
    }

    /**
     * �Ƿ���һԪ���� ��is null is not null
     *
     * @return
     */
    public boolean isUnaryFilter() {
        String operatorStr = getOperator().getSymbol();
        return StringUtils.isNotEmpty(operatorStr) && operatorStr.startsWith("is");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Condition that = (Condition) o;

        if (key != null ? !key.equals(that.key) : that.key != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Condition{" + "searchProperty='" + searchProperty + '\'' + ", operator=" + operator
               + ", value=" + value + '}';
    }
}
