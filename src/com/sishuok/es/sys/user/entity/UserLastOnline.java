/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.sys.user.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.es.framework.common.entity.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * �����û����һ��������Ϣ()
 * �˱���ڷ�����Ծ�û�����
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-11 ����3:25
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_user_last_online")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserLastOnline extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * ���ߵ��û�
     */
    @Column(name = "user_id")
    private Long              userId;

    @Column(name = "username")
    private String            username;

    /**
     * ����˳�ʱ��uid
     */
    private String            uid;

    /**
     * �û�������ַ
     */
    @Column(name = "host")
    private String            host;

    /**
     * �û����������
     */
    @Column(name = "user_agent")
    private String            userAgent;

    /**
     * �û���¼ʱϵͳIP
     */
    @Column(name = "system_host")
    private String            systemHost;

    /**
     * ����¼ʱ��
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_login_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date              lastLoginTimestamp;

    /**
     * ����˳�ʱ��
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_stop_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date              lastStopTimestamp;

    /**
     * ��¼����
     */
    @Column(name = "login_count")
    private Integer           loginCount       = 0;

    /**
     * �ܵ�����ʱ������Ϊ��λ��
     */
    @Column(name = "total_online_time")
    private Long              totalOnlineTime  = 0L;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Long getTotalOnlineTime() {
        return totalOnlineTime;
    }

    public void setTotalOnlineTime(Long totalOnlineTime) {
        this.totalOnlineTime = totalOnlineTime;
    }

    public String getSystemHost() {
        return systemHost;
    }

    public void setSystemHost(String systemHost) {
        this.systemHost = systemHost;
    }

    public Date getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }

    public void setLastLoginTimestamp(Date lastLoginTimestamp) {
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public Date getLastStopTimestamp() {
        return lastStopTimestamp;
    }

    public void setLastStopTimestamp(Date lastStopTimestamp) {
        this.lastStopTimestamp = lastStopTimestamp;
    }

    public void incLoginCount() {
        setLoginCount(getLoginCount() + 1);
    }

    public void incTotalOnlineTime() {
        long onlineTime = getLastStopTimestamp().getTime() - getLastLoginTimestamp().getTime();
        setTotalOnlineTime(getTotalOnlineTime() + onlineTime / 1000);
    }

    public static final UserLastOnline fromUserOnline(UserOnline online) {
        UserLastOnline lastOnline = new UserLastOnline();
        lastOnline.setHost(online.getHost());
        lastOnline.setUserId(online.getUserId());
        lastOnline.setUsername(online.getUsername());
        lastOnline.setUserAgent(online.getUserAgent());
        lastOnline.setSystemHost(online.getSystemHost());
        lastOnline.setUid(String.valueOf(online.getId()));
        lastOnline.setLastLoginTimestamp(online.getStartTimestamp());
        lastOnline.setLastStopTimestamp(online.getLastAccessTime());
        return lastOnline;
    }

    public static final void merge(UserLastOnline from, UserLastOnline to) {
        to.setHost(from.getHost());
        to.setUserId(from.getUserId());
        to.setUsername(from.getUsername());
        to.setUserAgent(from.getUserAgent());
        to.setSystemHost(from.getSystemHost());
        to.setUid(String.valueOf(from.getUid()));
        to.setLastLoginTimestamp(from.getLastLoginTimestamp());
        to.setLastStopTimestamp(from.getLastStopTimestamp());
    }

}
