package com.sishuok.es.personal.message.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.es.framework.common.service.BaseService;
import org.springframework.stereotype.Service;

import com.sishuok.es.personal.message.entity.Message;
import com.sishuok.es.personal.message.entity.MessageState;
import com.sishuok.es.personal.message.repository.MessageRepository;

@Service
public class MessageService extends BaseService<Message, Long> {

    private MessageRepository getMessageRepository() {
        return (MessageRepository) baseRepository;
    }

    /**
     * �ı䷢���� ��Ϣ��ԭ״̬ΪĿ��״̬
     *
     * @param senderId
     * @param oldState
     * @param newState
     */
    public Integer changeSenderState(Long senderId, MessageState oldState, MessageState newState) {
        Date changeDate = new Date();
        return getMessageRepository().changeSenderState(senderId, oldState, newState, changeDate);
    }

    /**
     * �ı��ռ����� ��Ϣ��ԭ״̬ΪĿ��״̬
     *
     * @param receiverId
     * @param oldState
     * @param newState
     */
    public Integer changeReceiverState(Long receiverId, MessageState oldState, MessageState newState) {
        Date changeDate = new Date();
        return getMessageRepository().changeReceiverState(receiverId, oldState, newState,
            changeDate);
    }

    /**
     * ����ɾ����Щ��ɾ���ģ����ռ��˺ͷ����� ͬʱ��ɾ���˵ģ�
     *
     * @param deletedState
     */
    public Integer clearDeletedMessage(MessageState deletedState) {
        return getMessageRepository().clearDeletedMessage(deletedState);
    }

    /**
     * ����״̬
     *
     * @param oldStates
     * @param newState
     * @param expireDays ��ǰʱ��-�������� ʱ��֮ǰ����Ϣ���ı�״̬
     */
    public Integer changeState(ArrayList<MessageState> oldStates, MessageState newState,
                               int expireDays) {
        Date changeDate = new Date();
        Integer count = getMessageRepository().changeSenderState(oldStates, newState, changeDate,
            DateUtils.addDays(changeDate, -expireDays));
        count += getMessageRepository().changeReceiverState(oldStates, newState, changeDate,
            DateUtils.addDays(changeDate, -expireDays));
        return count;
    }

    /**
     * ͳ���û��ռ���δ����Ϣ
     *
     * @param userId
     * @return
     */
    public Long countUnread(Long userId) {
        return getMessageRepository().countUnread(userId, MessageState.in_box);
    }

    public void markRead(final Long userId, final Long[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return;
        }
        getMessageRepository().markRead(userId, Arrays.asList(ids));
    }
}
