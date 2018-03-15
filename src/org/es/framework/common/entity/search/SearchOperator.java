package org.es.framework.common.entity.search;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.common.entity.search.exception.SearchException;

/**
 * <p>��ѯ������</p>
 */
public enum SearchOperator {
    eq("����", "="), ne("������", "!="), gt("����", ">"), gte("���ڵ���", ">="), lt("С��", "<"), lte("С�ڵ���",
                                                                                         "<="), prefixLike(
                                                                                                           "ǰ׺ģ��ƥ��",
                                                                                                           "like"), prefixNotLike(
                                                                                                                                  "ǰ׺ģ����ƥ��",
                                                                                                                                  "not like"), suffixLike(
                                                                                                                                                          "��׺ģ��ƥ��",
                                                                                                                                                          "like"), suffixNotLike(
                                                                                                                                                                                 "��׺ģ����ƥ��",
                                                                                                                                                                                 "not like"), like(
                                                                                                                                                                                                   "ģ��ƥ��",
                                                                                                                                                                                                   "like"), notLike(
                                                                                                                                                                                                                    "��ƥ��",
                                                                                                                                                                                                                    "not like"), isNull(
                                                                                                                                                                                                                                        "��",
                                                                                                                                                                                                                                        "is null"), isNotNull(
                                                                                                                                                                                                                                                              "�ǿ�",
                                                                                                                                                                                                                                                              "is not null"), in(
                                                                                                                                                                                                                                                                                 "����",
                                                                                                                                                                                                                                                                                 "in"), notIn(
                                                                                                                                                                                                                                                                                              "������",
                                                                                                                                                                                                                                                                                              "not in"), custom(
                                                                                                                                                                                                                                                                                                                "�Զ���Ĭ�ϵ�",
                                                                                                                                                                                                                                                                                                                null);

    private final String info;
    private final String symbol;

    SearchOperator(final String info, String symbol) {
        this.info = info;
        this.symbol = symbol;
    }

    public String getInfo() {
        return info;
    }

    public String getSymbol() {
        return symbol;
    }

    public static String toStringAllOperator() {
        return Arrays.toString(SearchOperator.values());
    }

    /**
     * �������Ƿ�����Ϊ��
     *
     * @param operator
     * @return
     */
    public static boolean isAllowBlankValue(final SearchOperator operator) {
        return operator == SearchOperator.isNotNull || operator == SearchOperator.isNull;
    }

    public static SearchOperator valueBySymbol(String symbol) throws SearchException {
        symbol = formatSymbol(symbol);
        for (SearchOperator operator : values()) {
            if (operator.getSymbol().equals(symbol)) {
                return operator;
            }
        }

        throw new SearchException("SearchOperator not method search operator symbol : " + symbol);
    }

    private static String formatSymbol(String symbol) {
        if (StringUtils.isBlank(symbol)) {
            return symbol;
        }
        return symbol.trim().toLowerCase().replace("  ", " ");
    }
}
