package org.apache.shiro.web.filter.user;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.es.framework.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.entity.UserStatus;
import com.sishuok.es.sys.user.service.UserService;

/**
 * ��֤�û�������
 * 1���û��Ƿ�ɾ��
 * 2���û��Ƿ�����
 */
public class SysUserFilter extends AccessControlFilter {

	@Autowired(required = true)
    private UserService userService;

    /**
     * �û�ɾ���˺��ض���ĵ�ַ
     */
    private String      userNotfoundUrl;
    /**
     * �û��������ض���ĵ�ַ
     */
    private String      userBlockedUrl;
    /**
     * δ֪����
     */
    private String      userUnknownErrorUrl;

    public String getUserNotfoundUrl() {
        return userNotfoundUrl;
    }

    public void setUserNotfoundUrl(String userNotfoundUrl) {
        this.userNotfoundUrl = userNotfoundUrl;
    }

    public String getUserBlockedUrl() {
        return userBlockedUrl;
    }

    public void setUserBlockedUrl(String userBlockedUrl) {
        this.userBlockedUrl = userBlockedUrl;
    }

    public String getUserUnknownErrorUrl() {
        return userUnknownErrorUrl;
    }

    public void setUserUnknownErrorUrl(String userUnknownErrorUrl) {
        this.userUnknownErrorUrl = userUnknownErrorUrl;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (subject == null) {
            return true;
        }

        String username = (String) subject.getPrincipal();
        //�˴�ע�⻺�� ��ֹ�����Ĳ�ѯdb
        User user = userService.findByUsername(username);
        //�ѵ�ǰ�û��ŵ�session��
        request.setAttribute(Constants.CURRENT_USER, user);
        //druid�����Ҫ
        ((HttpServletRequest) request).getSession().setAttribute(Constants.CURRENT_USERNAME,
            username);

        return true;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
                                      Object mappedValue) throws Exception {
        User user = (User) request.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return true;
        }

        if (Boolean.TRUE.equals(user.getDeleted()) || user.getStatus() == UserStatus.blocked) {
            getSubject(request, response).logout();
            saveRequestAndRedirectToLogin(request, response);
            return false;
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
                                                                                      throws Exception {
        getSubject(request, response).logout();
        saveRequestAndRedirectToLogin(request, response);
        return true;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response)
                                                                                    throws IOException {
        User user = (User) request.getAttribute(Constants.CURRENT_USER);
        String url = null;
        if (Boolean.TRUE.equals(user.getDeleted())) {
            url = getUserNotfoundUrl();
        } else if (user.getStatus() == UserStatus.blocked) {
            url = getUserBlockedUrl();
        } else {
            url = getUserUnknownErrorUrl();
        }

        WebUtils.issueRedirect(request, response, url);
    }

}
