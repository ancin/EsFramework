package com.sishuok.es.sys.user.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.es.framework.common.entity.BaseEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * <p>Date: 13-3-11 ����3:23
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_user_status_history")
public class UserStatusHistory extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * �������û�
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private User              user;

    /**
     * ��ע��Ϣ
     */
    private String            reason;

    /**
     * ������״̬
     */
    @Enumerated(EnumType.STRING)
    private UserStatus        status;

    /**
     * �����Ĺ���Ա
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "op_user_id")
    private User              opUser;

    /**
     * ����ʱ��
     */
    @Column(name = "op_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date              opDate;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getOpUser() {
        return opUser;
    }

    public void setOpUser(User opUser) {
        this.opUser = opUser;
    }

    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }
}
