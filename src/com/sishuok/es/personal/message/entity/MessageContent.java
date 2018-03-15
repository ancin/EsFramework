package com.sishuok.es.personal.message.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "personal_message_content")
@Proxy(lazy = true, proxyClass = MessageContent.class)
public class MessageContent extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
    private Message           message;

    @Length(min = 5, max = 50000, message = "{length.not.valid}")
    private String            content;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
