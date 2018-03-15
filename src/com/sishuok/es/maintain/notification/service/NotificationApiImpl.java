/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.notification.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.es.framework.common.entity.search.SearchOperator;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.utils.PrettyTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sishuok.es.maintain.notification.entity.NotificationData;
import com.sishuok.es.maintain.notification.entity.NotificationTemplate;
import com.sishuok.es.maintain.notification.exception.TemplateNotFoundException;
import com.sishuok.es.maintain.push.service.PushApi;

@Service
public class NotificationApiImpl implements NotificationApi {

    @Autowired
    private NotificationTemplateService notificationTemplateService;

    @Autowired
    private NotificationDataService     notificationDataService;

    @Autowired
    private PushApi                     pushApi;

    /**
     * �첽����
     * @param userId �������û����
     * @param templateName ģ������
     * @param context ģ����Ҫ������
     */
    @Async
    @Override
    public void notify(final Long userId, final String templateName,
                       final Map<String, Object> context) {
        NotificationTemplate template = notificationTemplateService.findByName(templateName);

        if (template == null) {
            throw new TemplateNotFoundException(templateName);
        }

        NotificationData data = new NotificationData();

        data.setUserId(userId);
        data.setSystem(template.getSystem());
        data.setDate(new Date());

        String content = template.getTemplate();
        String title = template.getTitle();
        if (context != null) {
            for (String key : context.keySet()) {
                //TODO �������������������� ��Ҫ����
                title = title.replace("{" + key + "}", String.valueOf(context.get(key)));
                content = content.replace("{" + key + "}", String.valueOf(context.get(key)));
            }
        }

        data.setTitle(title);
        data.setContent(content);

        notificationDataService.save(data);

        pushApi.pushNewNotification(userId, topFiveNotification(userId));

    }

    @Override
    public List<Map<String, Object>> topFiveNotification(final Long userId) {

        List<Map<String, Object>> dataList = Lists.newArrayList();

        Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("userId", SearchOperator.eq, userId);
        //        searchable.addSearchFilter("read", SearchOperator.eq, Boolean.FALSE);
        searchable.addSort(Sort.Direction.DESC, "id");
        searchable.setPage(0, 5);

        Page<NotificationData> page = notificationDataService.findAll(searchable);

        for (NotificationData data : page.getContent()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", data.getId());
            map.put("title", data.getTitle());
            map.put("content", data.getContent());
            map.put("read", data.getRead());
            map.put("date", PrettyTimeUtils.prettyTime(data.getDate()));
            dataList.add(map);
        }

        return dataList;
    }
}
