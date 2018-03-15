package com.sishuok.es.personal.calendar.service;

import java.sql.Time;
import java.util.Date;

import org.es.framework.common.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.sishuok.es.personal.calendar.entity.Calendar;
import com.sishuok.es.personal.calendar.repository.CalendarRepository;

@Service
public class CalendarService extends BaseService<Calendar, Long> {

    private CalendarRepository getCalendarRepository() {
        return (CalendarRepository) baseRepository;
    }

    public void copyAndRemove(Calendar calendar) {
        delete(calendar);

        Calendar copyCalendar = new Calendar();
        BeanUtils.copyProperties(calendar, copyCalendar);
        copyCalendar.setId(null);
        save(copyCalendar);
    }

    //2013 10 11   10-20   -3 > now
    //     10 11  10-19
    @SuppressWarnings("deprecation")
    public Long countRecentlyCalendar(Long userId, Integer interval) {
        Date nowDate = new Date();
        Date nowTime = new Time(nowDate.getHours(), nowDate.getMinutes(), nowDate.getSeconds());
        nowDate.setHours(0);
        nowDate.setMinutes(0);
        nowDate.setSeconds(0);

        return getCalendarRepository().countRecentlyCalendar(userId, nowDate, nowTime, interval);
    }
}
