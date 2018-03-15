package org.es.framework.common.entity.enums;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: BooleanEnum.java, v 0.1 2014��11��19�� ����10:16:35 kejun.song Exp $
 */
public enum BooleanEnum {
    TRUE(Boolean.TRUE, "��"), FALSE(Boolean.FALSE, "��");

    private final Boolean value;
    private final String  info;

    private BooleanEnum(Boolean value, String info) {
        this.value = value;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public Boolean getValue() {
        return value;
    }
}
