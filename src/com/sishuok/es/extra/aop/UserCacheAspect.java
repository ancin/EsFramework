/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.extra.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.es.framework.common.cache.BaseCacheAspect;
import org.springframework.stereotype.Component;

import com.sishuok.es.sys.user.entity.User;

/**
 * �û���������
 * ����ʵ��
 * 1��username/email/mobilePhoneNumber------>id
 * 2��id------->Model
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-22 ����9:00
 * <p>Version: 1.0
 */
@Component
@Aspect
public class UserCacheAspect extends BaseCacheAspect {

    public UserCacheAspect() {
        setCacheName("sys-userCache");
    }

    private final String idKeyPrefix                = "id-";
    private final String usernameKeyPrefix          = "username-";
    private final String emailKeyPrefix             = "email-";
    private final String mobilePhoneNumberKeyPrefix = "mobilePhoneNumber-";

    ////////////////////////////////////////////////////////////////////////////////
    ////�����
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * ƥ���û�Service
     */
    @Pointcut(value = "target(com.sishuok.es.sys.user.service.UserService)")
    private void userServicePointcut() {
    }

    /**
     * only put
     * �� ���� �޸� ��¼ ���� ��״̬  ֻ���漰�޸ļ���
     */
    @Pointcut(value = "execution(* save(..)) " + "|| execution(* saveAndFlush(..)) "
                      + "|| execution(* update(..)) " + "|| execution(* login(..)) "
                      + "|| execution(* changePassword(..)) " + "|| execution(* changeStatus(..))")
    private void cachePutPointcut() {
    }

    /**
     * evict ����ɾ��
     */
    @Pointcut(value = "(execution(* delete(*))) && args(arg)", argNames = "arg")
    private void cacheEvictPointcut(Object arg) {
    }

    /**
     * put �� get
     * �����ѯ
     */
    @Pointcut(value = "(execution(* findByUsername(*)) " + "|| execution(* findByEmail(*)) "
                      + "|| execution(* findByMobilePhoneNumber(*)) "
                      + "|| execution(* findOne(*)))")
    private void cacheablePointcut() {
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////��ǿʵ��
    ////////////////////////////////////////////////////////////////////////////////
    @AfterReturning(value = "userServicePointcut() && cachePutPointcut()", returning = "user")
    public void cachePutAdvice(Object user) {
        //put cache
        put((User) user);
    }

    @After(value = "userServicePointcut() && cacheEvictPointcut(arg)", argNames = "arg")
    public void cacheEvictAdvice(Object arg) {
        if (arg == null) {
            return;
        }
        if (arg instanceof Long) {
            //only evict id
            evictId(String.valueOf(arg));
        }
        if (arg instanceof Long[]) {
            for (Long id : (Long[]) arg) {
                //only evict id
                evictId(String.valueOf(id));
            }
        }

        if (arg instanceof String) {
            //only evict id
            evictId((String) arg);
        }
        if (arg instanceof String[]) {
            for (String id : (String[]) arg) {
                //only evict id
                evictId(String.valueOf(id));
            }
        }
        if (arg instanceof User) {
            //evict user
            evict((User) arg);
        }
    }

    @Around(value = "userServicePointcut() && cacheablePointcut()")
    public Object cacheableAdvice(ProceedingJoinPoint pjp) throws Throwable {

        String methodName = pjp.getSignature().getName();
        Object arg = pjp.getArgs().length >= 1 ? pjp.getArgs()[0] : null;

        String key = "";
        boolean isIdKey = false;
        if ("findOne".equals(methodName)) {
            key = idKey(String.valueOf(arg));
            isIdKey = true;
        } else if ("findByUsername".equals(methodName)) {
            key = usernameKey((String) arg);
        } else if ("findByEmail".equals(methodName)) {
            key = emailKey((String) arg);
        } else if ("findByMobilePhoneNumber".equals(methodName)) {
            key = mobilePhoneNumberKey((String) arg);
        }

        User user = null;
        if (isIdKey == true) {
            user = get(key);
        } else {
            Long id = get(key);
            if (id != null) {
                key = idKey(String.valueOf(id));
                user = get(key);
            }
        }
        //cache hit
        if (user != null) {
            log.debug("cacheName:{}, hit key:{}", cacheName, key);
            return user;
        }
        log.debug("cacheName:{}, miss key:{}", cacheName, key);

        //cache miss
        user = (User) pjp.proceed();

        //put cache
        put(user);
        return user;

    }

    private String idKey(String id) {
        return idKeyPrefix + id;
    }

    private String usernameKey(String username) {
        return usernameKeyPrefix + username;
    }

    private String emailKey(String email) {
        return emailKeyPrefix + email;
    }

    private String mobilePhoneNumberKey(String mobilePhoneNumber) {
        return mobilePhoneNumberKeyPrefix + mobilePhoneNumber;
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////cache ����ʵ��
    ////////////////////////////////////////////////////////////////////////////////
    public void put(User user) {
        if (user == null) {
            return;
        }
        Long id = user.getId();
        //username email mobilePhoneNumber ---> id
        put(usernameKey(user.getUsername()), id);
        put(emailKey(user.getEmail()), id);
        put(mobilePhoneNumberKey(user.getMobilePhoneNumber()), id);
        // id ---> user
        put(idKey(String.valueOf(id)), user);
    }

    public void evictId(String id) {
        evict(idKey(id));
    }

    public void evict(User user) {
        if (user == null) {
            return;
        }
        Long id = user.getId();
        evict(idKey(String.valueOf(id)));
        evict(usernameKey(user.getUsername()));
        evict(emailKey(user.getEmail()));
        evict(mobilePhoneNumberKey(user.getMobilePhoneNumber()));
    }

}
