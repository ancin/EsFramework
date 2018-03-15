package com.sishuok.es.front.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.es.framework.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.service.UserStatusHistoryService;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: LoginFormController.java, v 0.1 2014��11��19�� ����3:01:57 kejun.song Exp $
 */
@Controller
public class LoginFormController {

    @Value(value = "${shiro.login.url}")
    private String                   loginUrl;

    @Autowired
    private MessageSource            messageSource;

    @Autowired
    private UserStatusHistoryService userStatusHistoryService;

    @RequestMapping(value = { "/{login:login;?.*}" })
    //spring3.2.2 bug see  http://jinnianshilongnian.iteye.com/blog/1831408
    public String loginForm(HttpServletRequest request, ModelMap model) {

        //��ʾ�˳�
        if (!StringUtils.isEmpty(request.getParameter("logout"))) {
            model.addAttribute(Constants.MESSAGE,
                messageSource.getMessage("user.logout.success", null, null));
        }

        //��ʾ�û�ɾ���� @see org.apache.shiro.web.filter.user.SysUserFilter
        if (!StringUtils.isEmpty(request.getParameter("notfound"))) {
            model.addAttribute(Constants.ERROR,
                messageSource.getMessage("user.notfound", null, null));
        }

        //��ʾ�û�������Աǿ���˳�
        if (!StringUtils.isEmpty(request.getParameter("forcelogout"))) {
            model.addAttribute(Constants.ERROR,
                messageSource.getMessage("user.forcelogout", null, null));
        }

        //��ʾ�û��������֤�����
        if (!StringUtils.isEmpty(request.getParameter("jcaptchaError"))) {
            model.addAttribute(Constants.ERROR,
                messageSource.getMessage("jcaptcha.validate.error", null, null));
        }

        //��ʾ�û������� @see org.apache.shiro.web.filter.user.SysUserFilter
        if (!StringUtils.isEmpty(request.getParameter("blocked"))) {
            User user = (User) request.getAttribute(Constants.CURRENT_USER);
            String reason = userStatusHistoryService.getLastReason(user);
            model.addAttribute(Constants.ERROR,
                messageSource.getMessage("user.blocked", new Object[] { reason }, null));
        }

        if (!StringUtils.isEmpty(request.getParameter("unknown"))) {
            model.addAttribute(Constants.ERROR,
                messageSource.getMessage("user.unknown.error", null, null));
        }

        //��¼ʧ���� ��ȡ������Ϣ
        Exception shiroLoginFailureEx = (Exception) request
            .getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        if (shiroLoginFailureEx != null) {
            model.addAttribute(Constants.ERROR, shiroLoginFailureEx.getMessage());
        }

        //����û�ֱ�ӵ���¼ҳ�� ���˳�һ��
        //ԭ��isAccessAllowedʵ����subject.isAuthenticated()---->������û���֤ͨ�� ���������
        // �����ᵼ�µ�¼һֱ��ѭ��
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            subject.logout();
        }

        //���ͬʱ���ڴ�����Ϣ �� ��ͨ��Ϣ  ֻ����������Ϣ
        if (model.containsAttribute(Constants.ERROR)) {
            model.remove(Constants.MESSAGE);
        }

        return "front/login";
    }

}
