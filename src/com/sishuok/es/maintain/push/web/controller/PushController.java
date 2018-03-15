package com.sishuok.es.maintain.push.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.sishuok.es.maintain.notification.service.NotificationApi;
import com.sishuok.es.maintain.push.service.PushService;
import com.sishuok.es.personal.message.service.MessageApi;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.web.bind.annotation.CurrentUser;

/**
 * 1��ʵʱ�����û�����Ϣ��֪ͨ
 */
@Controller
public class PushController {

    @Autowired
    private MessageApi      messageApi;

    @Autowired
    private NotificationApi notificationApi;

    @Autowired
    private PushService     pushService;

    /**
     * ��ȡҳ�����ʾ��Ϣ
     * @return
     */
    @RequestMapping(value = "/admin/polling")
    @ResponseBody
    public Object polling(HttpServletResponse resp, @CurrentUser User user) {
        resp.setHeader("Connection", "Keep-Alive");
        resp.addHeader("Cache-Control", "private");
        resp.addHeader("Pragma", "no-cache");

        Long userId = user.getId();
        if (userId == null) {
            return null;
        }
        //����û���һ���� ��������
        if (!pushService.isOnline(userId)) {
            Long unreadMessageCount = messageApi.countUnread(userId);
            List<Map<String, Object>> notifications = notificationApi.topFiveNotification(user
                .getId());

            Map<String, Object> data = Maps.newHashMap();
            data.put("unreadMessageCount", unreadMessageCount);
            data.put("notifications", notifications);
            pushService.online(userId);
            return data;
        } else {
            //����ѯ
            return pushService.newDeferredResult(userId);
        }
    }

}
