package com.sishuok.es.sys.user.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.mgt.OnlineSession;
import org.apache.shiro.session.mgt.eis.OnlineSessionDAO;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.utils.MessageUtils;
import org.es.framework.common.web.controller.BaseCRUDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sishuok.es.sys.user.entity.UserOnline;

@Controller
@RequestMapping(value = "/admin/sys/user/online")
public class UserOnlineController extends BaseCRUDController<UserOnline, String> {

    @Autowired
    private OnlineSessionDAO onlineSessionDAO;

    public UserOnlineController() {
    }

    @Override
    public String list(Searchable searchable, Model model) {
        if (!SecurityUtils.getSubject().isPermitted(
            "sys:userOnline:view or monitor:userOnline:view")) {
            throw new UnauthorizedException(MessageUtils.message("no.view.permission",
                "sys:userOnline:view»òmonitor:userOnline:view"));
        }
        return super.list(searchable, model);
    }

    @RequestMapping("/forceLogout")
    public String forceLogout(@RequestParam(value = "ids") String[] ids) {

        if (!SecurityUtils.getSubject().isPermitted("sys:userOnline or monitor:userOnline")) {
            throw new UnauthorizedException(MessageUtils.message("no.view.permission",
                "sys:userOnline»òmonitor:userOnline"));
        }

        for (String id : ids) {
            UserOnline online = baseService.findOne(id);
            if (online == null) {
                continue;
            }

            OnlineSession onlineSession = (OnlineSession) onlineSessionDAO.readSession(online
                .getId());

            if (onlineSession == null) {
                continue;
            }
            onlineSession.setStatus(OnlineSession.OnlineStatus.force_logout);
            online.setStatus(OnlineSession.OnlineStatus.force_logout);
            baseService.update(online);
        }
        return redirectToUrl(null);
    }

}
