package org.apache.shiro;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: ShiroConstants.java, v 0.1 2014年11月19日 下午2:41:36 kejun.song Exp $
 */
public interface ShiroConstants {
    /**
     * 当前在线会话
     */
    String ONLINE_SESSION   = "online_session";

    /**
     * 仅清空本地缓存 不情况数据库的
     */
    String ONLY_CLEAR_CACHE = "online_session_only_clear_cache";
}
