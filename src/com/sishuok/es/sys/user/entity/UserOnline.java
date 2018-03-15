package com.sishuok.es.sys.user.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.shiro.session.mgt.OnlineSession;
import org.es.framework.common.entity.AbstractEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * ��ǰ���߻Ự
 * 
 */
@Entity
@Table(name = "sys_user_online")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserOnline extends AbstractEntity<String> {

    /**  */
    private static final long          serialVersionUID = 1L;

    /**
     * �û��Ựid ===> uid
     */
    @Id
    @GeneratedValue(generator = "assigned")
    @GenericGenerator(name = "assigned", strategy = "assigned")
    private String                     id;

    //��ǰ��¼���û�Id
    @Column(name = "user_id")
    private Long                       userId           = 0L;

    @Column(name = "username")
    private String                     username;

    /**
     * �û�������ַ
     */
    @Column(name = "host")
    private String                     host;

    /**
     * �û���¼ʱϵͳIP
     */
    @Column(name = "system_host")
    private String                     systemHost;

    /**
     * �û����������
     */
    @Column(name = "user_agent")
    private String                     userAgent;

    /**
     * ����״̬
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OnlineSession.OnlineStatus status           = OnlineSession.OnlineStatus.on_line;

    /**
     * session����ʱ��
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_timestsamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       startTimestamp;
    /**
     * session������ʱ��
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_access_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       lastAccessTime;

    /**
     * ��ʱʱ��
     */
    @Column(name = "timeout")
    private Long                       timeout;

    /**
     * ���ݵĵ�ǰ�û��Ự
     */
    @Column(name = "session")
	@Type(type = "org.es.framework.common.repository.hibernate.type.ObjectSerializeUserType")
    private OnlineSession              session;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public OnlineSession.OnlineStatus getStatus() {
        return status;
    }

    public void setStatus(OnlineSession.OnlineStatus status) {
        this.status = status;
    }

    public OnlineSession getSession() {
        return session;
    }

    public void setSession(OnlineSession session) {
        this.session = session;
    }

    public String getSystemHost() {
        return systemHost;
    }

    public void setSystemHost(String systemHost) {
        this.systemHost = systemHost;
    }

    public static final UserOnline fromOnlineSession(OnlineSession session) {
        UserOnline online = new UserOnline();
        online.setId(String.valueOf(session.getId()));
        online.setUserId(session.getUserId());
        online.setUsername(session.getUsername());
        online.setStartTimestamp(session.getStartTimestamp());
        online.setLastAccessTime(session.getLastAccessTime());
        online.setTimeout(session.getTimeout());
        online.setHost(session.getHost());
        online.setUserAgent(session.getUserAgent());
        online.setSystemHost(session.getSystemHost());
        online.setSession(session);

        return online;
    }

}
