package com.sishuok.es.personal.message.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sishuok.es.personal.message.entity.Message;
import com.sishuok.es.personal.message.entity.MessageState;

public interface MessageApi {
    public static final String REPLY_PREFIX     = "�ظ���";
    public static final String FOWRARD_PREFIX   = "ת����";
    public static final String FOWRARD_TEMPLATE = "<br/><br/>-----------ת����Ϣ------------<br/>������:%s<br/>�ռ��ˣ�%s<br/>���⣺%s<br/><br/>%s";

    /**
     * �õ��û� ָ��״̬����Ϣ
     *
     * @param userId
     * @param state
     * @param pageable
     * @return
     */
    public Page<Message> findUserMessage(Long userId, MessageState state, Pageable pageable);

    /**
     * ��ѯ��Ϣ������ �� ���
     *
     * @param message
     * @return
     */
    List<Message> findAncestorsAndDescendants(Message message);

    /**
     * ����ݸ�
     *
     * @param message
     */
    void saveDraft(Message message);

    /**
     * ������Ϣ
     *
     * @param message
     */
    public void send(Message message);

    /**
     * ����ϵͳ��Ϣ�������
     *
     * @param receiverIds
     * @param message
     */
    public void sendSystemMessage(Long[] receiverIds, Message message);

    /**
     * ����ϵͳ��Ϣ��������
     *
     * @param message
     */
    public void sendSystemMessageToAllUser(Message message);

    /**
     * ����Ϣ�ƶ���������
     *
     * @param userId
     * @param messageId
     * @return
     */
    public void recycle(Long userId, Long messageId);

    /**
     * ��������Ϣ�ƶ���������
     *
     * @param userId
     * @param messageIds
     * @return
     */
    public void recycle(Long userId, Long[] messageIds);

    /**
     * ����Ϣ���浽�ղ���
     *
     * @param userId
     * @param messageId
     * @return
     */
    public void store(Long userId, Long messageId);

    /**
     * ��������Ϣ���浽�ղ���
     *
     * @param userId
     * @param messageIds
     * @return
     */
    public void store(Long userId, Long[] messageIds);

    /**
     * ��������ɾ����Ϣ
     *
     * @param userId
     * @param messageId
     */
    public void delete(Long userId, Long messageId);

    /**
     * ��������ɾ����Ϣ
     *
     * @param userId
     * @param messageIds
     */
    public void delete(Long userId, Long[] messageIds);

    /**
     * ���ָ��״̬����Ϣ
     *
     * @param userId
     * @param state
     */
    public void clearBox(Long userId, MessageState state);

    /**
     * ��ղݸ���
     *
     * @param userId
     */
    public void clearDraftBox(Long userId);

    /**
     * ����ռ���
     *
     * @param userId
     */
    public void clearInBox(Long userId);

    /**
     * ����ռ���
     *
     * @param userId
     */
    public void clearOutBox(Long userId);

    /**
     * ����ղ���
     *
     * @param userId
     */
    public void clearStoreBox(Long userId);

    /**
     * ���������
     *
     * @param userId
     */
    public void clearTrashBox(Long userId);

    /**
     * δ���ռ�����Ϣ����
     *
     * @param userId
     */
    public Long countUnread(Long userId);

    /**
     * ��ʶΪ�Ѷ�
     *
     * @param message
     */
    public void markRead(Message message);

    /**
     * ��ʶΪ�ѻظ�
     *
     * @param message
     */
    public void markReplied(Message message);

    void markRead(Long userId, Long[] ids);
}
