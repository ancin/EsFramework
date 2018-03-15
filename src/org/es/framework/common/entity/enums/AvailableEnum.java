package org.es.framework.common.entity.enums;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: AvailableEnum.java, v 0.1 2014年11月19日 上午10:15:54 kejun.song Exp $
 */
public enum AvailableEnum {
    TRUE(Boolean.TRUE, "可用"), FALSE(Boolean.FALSE, "不可用");

    private final Boolean value;
    private final String info;

    private AvailableEnum(Boolean value, String info) {
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
