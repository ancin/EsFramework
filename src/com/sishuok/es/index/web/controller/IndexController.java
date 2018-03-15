package com.sishuok.es.index.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sishuok.es.maintain.push.service.PushApi;
import com.sishuok.es.personal.calendar.service.CalendarService;
import com.sishuok.es.personal.message.service.MessageService;
import com.sishuok.es.sys.resource.entity.tmp.Menu;
import com.sishuok.es.sys.resource.service.ResourceService;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.web.bind.annotation.CurrentUser;

@Controller("adminIndexController")
@RequestMapping("/admin")
public class IndexController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PushApi         pushApi;

    @Autowired
    private MessageService  messageService;

    @Autowired
    private CalendarService calendarService;

    @RequestMapping(value = { "/{index:index;?.*}" })
    //spring3.2.2 bug see  http://jinnianshilongnian.iteye.com/blog/1831408
    public String index(@CurrentUser User user, Model model) {

        List<Menu> menus = resourceService.findMenus(user);
        model.addAttribute("menus", menus);

        pushApi.offline(user.getId());

        return "admin/index/index";
    }

    @RequestMapping(value = "/welcome")
    public String welcome(@CurrentUser User loginUser, Model model) {

        //δ����Ϣ
        Long messageUnreadCount = messageService.countUnread(loginUser.getId());
        model.addAttribute("messageUnreadCount", messageUnreadCount);

        //���3�������
        model.addAttribute("calendarCount",
            calendarService.countRecentlyCalendar(loginUser.getId(), 2));

        return "admin/index/welcome";
    }

}
