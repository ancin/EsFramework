/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.extra.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sishuok.es.sys.auth.task.AuthRelationClearTask;
import com.sishuok.es.sys.group.task.GroupClearRelationTask;
import com.sishuok.es.sys.permission.task.RoleClearRelationTask;
import com.sishuok.es.sys.user.task.UserClearRelationTask;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: RelationClearTask.java, v 0.1 2014年11月19日 下午2:59:46 kejun.song Exp $
 */
@Service("relationClearTask")
public class RelationClearTask {

    @Autowired
    private UserClearRelationTask  userClearRelationTask;

    @Autowired
    private GroupClearRelationTask groupClearRelationTask;

    @Autowired
    private RoleClearRelationTask  roleClearRelationTask;

    @Autowired
    private AuthRelationClearTask  authRelationClearTask;

    public void autoClearRelation() {

    }

}
