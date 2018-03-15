package org.apache.shiro.web.filter.authc;

import javax.servlet.ServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;

import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.service.UserService;

/**
 * ���ڼ����޸ģ�
 * 1��onLoginFailure ʱ ���쳣��ӵ�request attribute�� �������쳣����
 * 2����¼�ɹ�ʱ���ɹ�ҳ���ض���
 * 2.1�����ǰһ��ҳ���ǵ�¼ҳ�棬-->2.3
 * 2.2�������SavedRequest �򷵻ص�SavedRequest
 * 2.3��������ݵ�ǰ��¼���û��������ص�����Ա��ҳ/ǰ̨��ҳ
 * <p/>
 * 
 */
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

	@Autowired(required = true)
    UserService userService;

    @Override
    protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
        request.setAttribute(getFailureKeyAttribute(), ae);
    }

    /**
     * Ĭ�ϵĳɹ���ַ
     */
    private String defaultSuccessUrl;
    /**
     * ����ԱĬ�ϵĳɹ���ַ
     */
    private String adminDefaultSuccessUrl;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setDefaultSuccessUrl(String defaultSuccessUrl) {
        this.defaultSuccessUrl = defaultSuccessUrl;
    }

    public void setAdminDefaultSuccessUrl(String adminDefaultSuccessUrl) {
        this.adminDefaultSuccessUrl = adminDefaultSuccessUrl;
    }

    public String getDefaultSuccessUrl() {
        return defaultSuccessUrl;
    }

    public String getAdminDefaultSuccessUrl() {
        return adminDefaultSuccessUrl;
    }

    /**
     * �����û�ѡ��ɹ���ַ
     *
     * @return
     */
    @Override
    public String getSuccessUrl() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUsername(username);
        if (user != null && Boolean.TRUE.equals(user.getAdmin())) {
            return getAdminDefaultSuccessUrl();
        }
        return getDefaultSuccessUrl();
    }
}
