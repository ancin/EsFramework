/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.apache.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.es.framework.common.repository.support.SimpleBaseRepositoryFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sishuok.es.sys.auth.service.UserAuthService;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.exception.UserBlockedException;
import com.sishuok.es.sys.user.exception.UserException;
import com.sishuok.es.sys.user.exception.UserNotExistsException;
import com.sishuok.es.sys.user.exception.UserPasswordNotMatchException;
import com.sishuok.es.sys.user.exception.UserPasswordRetryLimitExceedException;
import com.sishuok.es.sys.user.service.UserService;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService         userService;
    @Autowired
    private UserAuthService     userAuthService;

    private static final Logger log          = LoggerFactory.getLogger("es-error");

    private static final String OR_OPERATOR  = " or ";
    private static final String AND_OPERATOR = " and ";
    private static final String NOT_OPERATOR = "not ";

    public UserRealm() {
    }

    @Autowired
    public UserRealm(ApplicationContext ctx) {
        super();
        //����ע�� ��Ϊ��ȡbean����˳��������ɿ����ò���ĳЩbean����
        //why��
        //��Ϊspring�ڲ���findAutowireCandidatesʱ��FactoryBean�����Ż�����ֻ��ȡBean��������autowire���ԣ�
        //����������ǵ�bean����������bean֮ǰ��ʼ������ô�͵ò���ObjectType����Զ��Repository��
        //���Դ˴�������getBeanһ�� ��û��������
        ctx.getBeansOfType(SimpleBaseRepositoryFactoryBean.class);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        User user = userService.findByUsername(username);

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(userAuthService.findStringRoles(user));
        authorizationInfo.setStringPermissions(userAuthService.findStringPermissions(user));

        return authorizationInfo;
    }

    /**
     * ֧��or and not �ؼ���  ��֧��and or����
     *
     * @param principals
     * @param permission
     * @return
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        if (permission.contains(OR_OPERATOR)) {
            String[] permissions = permission.split(OR_OPERATOR);
            for (String orPermission : permissions) {
                if (isPermittedWithNotOperator(principals, orPermission)) {
                    return true;
                }
            }
            return false;
        } else if (permission.contains(AND_OPERATOR)) {
            String[] permissions = permission.split(AND_OPERATOR);
            for (String orPermission : permissions) {
                if (!isPermittedWithNotOperator(principals, orPermission)) {
                    return false;
                }
            }
            return true;
        } else {
            return isPermittedWithNotOperator(principals, permission);
        }
    }

    private boolean isPermittedWithNotOperator(PrincipalCollection principals, String permission) {
        if (permission.startsWith(NOT_OPERATOR)) {
            return !super.isPermitted(principals, permission.substring(NOT_OPERATOR.length()));
        } else {
            return super.isPermitted(principals, permission);
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
                                                                                   throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername().trim();
        String password = "";
        if (upToken.getPassword() != null) {
            password = new String(upToken.getPassword());
        }

        User user = null;
        try {
            user = userService.login(username, password);
        } catch (UserNotExistsException e) {
            throw new UnknownAccountException(e.getMessage(), e);
        } catch (UserPasswordNotMatchException e) {
            throw new AuthenticationException(e.getMessage(), e);
        } catch (UserPasswordRetryLimitExceedException e) {
            throw new ExcessiveAttemptsException(e.getMessage(), e);
        } catch (UserBlockedException e) {
            throw new LockedAccountException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("login error", e);
            throw new AuthenticationException(new UserException("user.unknown.error", null));
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUsername(),
            password.toCharArray(), getName());
        return info;
    }

}
