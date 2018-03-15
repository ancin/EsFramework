package org.apache.shiro.web.filter.jcaptcha;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.es.framework.common.web.jcaptcha.JCaptcha;

/**
 * ��֤�������
 * 
 */
public class JCaptchaValidateFilter extends AccessControlFilter {

    private boolean jcaptchaEbabled = true;

    private String  jcaptchaParam   = "jcaptchaCode";

    private String  jcapatchaErrorUrl;

    /**
     * �Ƿ���jcaptcha
     *
     * @param jcaptchaEbabled
     */
    public void setJcaptchaEbabled(boolean jcaptchaEbabled) {
        this.jcaptchaEbabled = jcaptchaEbabled;
    }

    /**
     * ǰ̨�������֤��
     *
     * @param jcaptchaParam
     */
    public void setJcaptchaParam(String jcaptchaParam) {
        this.jcaptchaParam = jcaptchaParam;
    }

    public void setJcapatchaErrorUrl(String jcapatchaErrorUrl) {
        this.jcapatchaErrorUrl = jcapatchaErrorUrl;
    }

    public String getJcapatchaErrorUrl() {
        return jcapatchaErrorUrl;
    }

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
                                                                                                    throws Exception {
        request.setAttribute("jcaptchaEbabled", jcaptchaEbabled);
        return super.onPreHandle(request, response, mappedValue);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
                                      Object mappedValue) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        //��֤����� ���Ǳ����ύ ��������
        if (jcaptchaEbabled == false
            || !"post".equals(httpServletRequest.getMethod().toLowerCase())) {
            return true;
        }
        return JCaptcha.validateResponse(httpServletRequest,
            httpServletRequest.getParameter(jcaptchaParam));
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
                                                                                      throws Exception {
        redirectToLogin(request, response);
        return true;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response)
                                                                                    throws IOException {
        WebUtils.issueRedirect(request, response, getJcapatchaErrorUrl());
    }

}