package com.sishuok.es.personal.calendar.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.time.DateUtils;
import org.es.framework.common.entity.BaseEntity;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "personal_calendar")
public class Calendar extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * ������
     */
    @Column(name = "user_id")
    private Long              userId;

    /**
     * ����
     */
    @Length(min = 1, max = 200, message = "{length.not.valid}")
    private String            title;

    /**
     * ��ϸ��Ϣ
     */
    @Length(min = 0, max = 500, message = "{length.not.valid}")
    private String            details;

    /**
     * ��ʼ����
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date              startDate;

    /**
     * ����ʱ��
     */
    private Integer           length;

    /**
     * ��ʼʱ��
     */
    @DateTimeFormat(pattern = "HH:mm:ss")
    @Temporal(TemporalType.TIME)
    @Column(name = "start_time")
    private Date              startTime;

    /**
     * ����ʱ��
     */
    @DateTimeFormat(pattern = "HH:mm:ss")
    @Temporal(TemporalType.TIME)
    @Column(name = "end_time")
    private Date              endTime;

    @Column(name = "background_color")
    private String            backgroundColor;

    @Column(name = "text_color")
    private String            textColor;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return DateUtils.addDays(startDate, length - 1);
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
