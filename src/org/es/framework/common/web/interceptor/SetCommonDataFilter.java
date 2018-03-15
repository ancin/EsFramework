package org.es.framework.common.web.interceptor;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.es.framework.common.web.filter.BaseFilter;

/**
 * ����ͨ�����ݵ�Filter
 * <p/>
 * ʹ��Filterʱ �ļ��ϴ�ʱ getParameterʱΪnull ���Ըĳ�Interceptor
 * <p/>
 * 1��ctx---->request.contextPath
 * 2��currentURL---->��ǰ��ַ
 * 
 */
public class SetCommonDataFilter extends BaseFilter {

    private final SetCommonDataInterceptor setCommonDataInterceptor = new SetCommonDataInterceptor();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                                                                                                     throws IOException,
                                                                                                     ServletException {
        try {
            //ʹ��Filterʱ �ļ��ϴ�ʱ getParameterʱΪnull ���Ըĳ�Interceptor
            setCommonDataInterceptor.preHandle(request, response, null);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        chain.doFilter(request, response);
    }
}
