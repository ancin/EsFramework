/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.apache.shiro.session.mgt;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.web.session.mgt.WebSessionContext;
import org.es.framework.common.utils.IpUtils;

import com.sishuok.es.sys.user.entity.UserOnline;

/**
 * �����Զ����session��
 * ���һЩ�Զ��������
 * �� �û���¼����ϵͳip
 * �û�״̬������ ���� ǿ���˳���
 * �� ���統ǰ����ϵͳ��
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-20 ����2:33
 * <p>Version: 1.0
 */
public class OnlineSessionFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext initData) {
        OnlineSession session = new OnlineSession();
        if (initData != null && initData instanceof WebSessionContext) {
            WebSessionContext sessionContext = (WebSessionContext) initData;
            HttpServletRequest request = (HttpServletRequest) sessionContext.getServletRequest();
            if (request != null) {
                session.setHost(IpUtils.getIpAddr(request));
                session.setUserAgent(request.getHeader("User-Agent"));
                session.setSystemHost(request.getLocalAddr() + ":" + request.getLocalPort());
            }
        }
        return session;
    }

    public Session createSession(UserOnline userOnline) {
        return userOnline.getSession();
    }
}
