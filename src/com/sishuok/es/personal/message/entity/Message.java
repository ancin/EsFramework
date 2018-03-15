package com.sishuok.es.personal.message.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import org.es.framework.common.entity.BaseEntity;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * ϵͳ��Ϣ
 * 1���ռ���ͷ������ڵ���ϢĬ��365��󱻷���������
 * 2���������ڵ���Ϣ30����Զ�����ɾ��
 * 3���������ڵ���Ϣֻ��ֻ�е��ռ��˺ͷ����� ����Ϣ������������ɾ�����������ɾ��
 * 4���ղ���Ĳ���ɾ��
 * <p/>
 * ���type==system_message_all��ʾ�Ƿ��������˵���Ϣ �������£�
 * 1��������չʾʱ����һҳ��������Զ������е�system_message_all
 * 2������û��Ķ��ˣ�ֱ�Ӹ���һ�� ���������ռ��� ״̬��Ϊsystem_message
 * <p/>
 * �����Ϣ�ǲݸ� ��ô�ռ���״̬��null
 * <p/>
 * 
 */
@Entity
@Table(name = "personal_message")
@Proxy(lazy = true, proxyClass = Message.class)
public class Message extends BaseEntity<Long> {

    /**  */
    private static final long   serialVersionUID = 1L;

    /**
     * ��Ϣ������Id
     */
    @Column(name = "sender_id")
    private Long                senderId;

    /**
     * ��Ϣ������id
     */
    @Column(name = "receiver_id")
    private Long                receiverId;

    /**
     * ��Ϣ����ʱ��
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "send_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                sendDate;

    /**
     * ����
     */
    @Length(min = 5, max = 200, message = "{length.not.valid}")
    @Column(name = "title")
    private String              title;

    /**
     * OneToOne����������ֽ�������ܴ��� ���Ըĳ�OneToMany
     */
    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "message", orphanRemoval = true)
    private Set<MessageContent> contents;

    /**
     * ������״̬
     */
    @Column(name = "sender_state")
    @Enumerated(EnumType.STRING)
    private MessageState        senderState      = MessageState.out_box;
    /**
     * ������״̬�ı�ʱ�� Ĭ�Ϸ���ʱ��
     */
    @Column(name = "sender_state_change_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                senderStateChangeDate;

    //�ռ���״̬
    @Column(name = "receiver_state")
    @Enumerated(EnumType.STRING)
    private MessageState        receiverState    = MessageState.in_box;
    /**
     * �ռ���״̬�ı�ʱ�� Ĭ�Ϸ���ʱ��
     */
    @Column(name = "receiver_state_change_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                receiverStateChangeDate;

    /**
     * ��Ϣ����,Ĭ����ͨ��Ϣ
     */
    @Enumerated(EnumType.STRING)
    private MessageType         type             = MessageType.user_message;

    /**
     * �Ƿ��Ѷ�
     */
    @Column(name = "is_read")
    private Boolean             read             = Boolean.FALSE;
    /**
     * �Ƿ��ѻظ�
     */
    @Column(name = "is_replied")
    private Boolean             replied          = Boolean.FALSE;

    /**
     * �����
     */
    @Column(name = "parent_id")
    private Long                parentId;

    /**
     * ����Ϣ����б� ��1/2/3/4
     */
    @Column(name = "parent_ids")
    private String              parentIds;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MessageContent getContent() {
        if (contents != null && contents.size() > 0) {
            return contents.iterator().next();
        }
        return null;
    }

    public void setContent(MessageContent content) {
        if (contents == null) {
            contents = new HashSet<MessageContent>();
        }
        contents.add(content);
    }

    public MessageState getSenderState() {
        return senderState;
    }

    public void setSenderState(MessageState senderState) {
        this.senderState = senderState;
    }

    public Date getSenderStateChangeDate() {
        return senderStateChangeDate;
    }

    public void setSenderStateChangeDate(Date senderStateChangeDate) {
        this.senderStateChangeDate = senderStateChangeDate;
    }

    public MessageState getReceiverState() {
        return receiverState;
    }

    public void setReceiverState(MessageState receiverState) {
        this.receiverState = receiverState;
    }

    public Date getReceiverStateChangeDate() {
        return receiverStateChangeDate;
    }

    public void setReceiverStateChangeDate(Date receiverStateChangeDate) {
        this.receiverStateChangeDate = receiverStateChangeDate;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getReplied() {
        return replied;
    }

    public void setReplied(Boolean replied) {
        this.replied = replied;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public String makeSelfAsParentIds() {
        return (getParentIds() != null ? getParentIds() : "") + getId() + "/";
    }

}
