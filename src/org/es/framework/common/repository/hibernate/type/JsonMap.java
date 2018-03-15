package org.es.framework.common.repository.hibernate.type;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: JsonMap.java, v 0.1 2014年11月19日 上午11:01:59 kejun.song Exp $
 */
public class JsonMap implements Serializable {

    /**  */
    private static final long   serialVersionUID = 1L;
    private Map<Object, Object> map;

    public JsonMap() {
    }

    public JsonMap(Map<Object, Object> map) {
        this.map = map;
    }

    public Map<Object, Object> getMap() {
        if (map == null) {
            map = Maps.newHashMap();
        }
        return map;
    }

    public void setMap(Map<Object, Object> map) {
        this.map = map;
    }
}
