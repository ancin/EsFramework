/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.personal.message.task;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sishuok.es.personal.message.entity.MessageState;
import com.sishuok.es.personal.message.service.MessageService;

/**
 * ���� ���ڵ�/ɾ������Ϣ
 * 
 */
@Service
public class MessageClearTask {

    public static final int EXPIRE_DAYS_OF_ONE_YEAR  = 366;
    public static final int EXPIRE_DAYS_OF_ONE_MONTH = 31;

    @Autowired
    private MessageService  messageService;

    public void autoClearExpiredOrDeletedmMessage() {
        MessageClearTask messageClearTask = (MessageClearTask) AopContext.currentProxy();
        //1���ռ��䡢������״̬�޸�Ϊ������״̬
        messageClearTask.doClearInOrOutBox();
        //2��������״̬��Ϊ��ɾ��״̬
        messageClearTask.doClearTrashBox();
        //3������ɾ����Щ��ɾ���ģ����ռ��˺ͷ����� ͬʱ��ɾ���˵ģ�
        messageClearTask.doClearDeletedMessage();
    }

    public void doClearDeletedMessage() {
        messageService.clearDeletedMessage(MessageState.delete_box);
    }

    public void doClearInOrOutBox() {
        messageService.changeState(Lists.newArrayList(MessageState.in_box, MessageState.out_box),
            MessageState.trash_box, EXPIRE_DAYS_OF_ONE_YEAR);

    }

    public void doClearTrashBox() {
        messageService.changeState(Lists.newArrayList(MessageState.trash_box),
            MessageState.delete_box, EXPIRE_DAYS_OF_ONE_MONTH);
    }

}
