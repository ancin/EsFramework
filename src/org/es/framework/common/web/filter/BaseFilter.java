package org.es.framework.common.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Ant�﷨
 * ��ο� http://jinnianshilongnian.iteye.com/blog/1416322
 * <p/>
 * ���÷�ʽ
 * <filter>
 * <filter-name>TestFilter</filter-name>
 * <filter-class>com.sishuok.web.filter.TestFilter</filter-class>
 * <!-- url�ָ��������� ���� �ո� �ֺ�  ����  �������ͺ��������ǿ�ѡ-->
 * <init-param>
 * <param-name>blackListURL</param-name> <!-- ���ú�����url ��ʾ���߹�������url order��1 -->
 * <param-value>
 * /aa
 * /bb/**
 * /cc/*
 * </param-value>
 * </init-param>
 * <init-param>
 * <param-name>whiteListURL</param-name> <!-- ���ð�����url ��ʾ�߹�������url order��2-->
 * <param-value>
 * /dd;/ee,/ff /list
 * </param-value>
 * </init-param>
 * </filter>
 * <filter-mapping>
 * <filter-name>TestFilter</filter-name>
 * <url-pattern>/*</url-pattern>
 * </filter-mapping>
 * <p/>
 * ���������ܣ�
 *
 */
public abstract class BaseFilter implements Filter {

    private FilterConfig      config            = null;

    private final String[]    NULL_STRING_ARRAY = new String[0];
    private final String      URL_SPLIT_PATTERN = "[, ;\r\n]";                          //����  �ո� �ֺ�  ����

    private final PathMatcher pathMatcher       = new AntPathMatcher();

    private final Logger      logger            = LoggerFactory.getLogger("url.filter");

    /**
     * ������
     */
    private String[]          whiteListURLs     = null;

    /**
     * ������
     */
    private String[]          blackListURLs     = null;

    @Override
    public final void init(FilterConfig config) throws ServletException {
        this.config = config;
        this.initConfig();
        this.init();
    }

    /**
     * ���า��
     *
     * @throws ServletException
     */
    public void init() throws ServletException {

    }

    /**
     * 1��������ƥ��
     * 2��������ƥ��
     */
    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                                                                                                   throws IOException,
                                                                                                   ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String currentURL = httpRequest.getServletPath();

        logger.debug("url filter : current url : [{}]", currentURL);

        if (isBlackURL(currentURL)) {
            chain.doFilter(request, response);
            return;
        }

        if (!isWhiteURL(currentURL)) {
            chain.doFilter(request, response);
            return;
        }
        doFilter(httpRequest, httpResponse, chain);
        return;
    }

    private boolean isWhiteURL(String currentURL) {
        for (String whiteURL : whiteListURLs) {
            if (pathMatcher.match(whiteURL, currentURL)) {
                logger.debug("url filter : white url list matches : [{}] match [{}] continue",
                    currentURL, whiteURL);
                return true;
            }
        }
        logger.debug("url filter : white url list not matches : [{}] not match [{}]", currentURL,
            Arrays.toString(whiteListURLs));
        return false;
    }

    private boolean isBlackURL(String currentURL) {
        for (String blackURL : blackListURLs) {
            if (pathMatcher.match(blackURL, currentURL)) {
                logger.debug("url filter : black url list matches : [{}] match [{}] break",
                    currentURL, blackURL);
                return true;
            }
        }
        logger.debug("url filter : black url list not matches : [{}] not match [{}]", currentURL,
            Arrays.toString(blackListURLs));
        return false;
    }

    /**
     * ���า��
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                                                                                                     throws IOException,
                                                                                                     ServletException {
        chain.doFilter(request, response);
    }

    /**
     * ���า��
     */
    @Override
    public void destroy() {

    }

    private void initConfig() {
        String whiteListURLStr = this.config.getInitParameter("whiteListURL");
        whiteListURLs = strToArray(whiteListURLStr);

        String blackListURLStr = this.config.getInitParameter("blackListURL");
        blackListURLs = strToArray(blackListURLStr);

    }

    private String[] strToArray(String urlStr) {
        if (urlStr == null) {
            return NULL_STRING_ARRAY;
        }
        String[] urlArray = urlStr.split(URL_SPLIT_PATTERN);

        List<String> urlList = new ArrayList<String>();

        for (String url : urlArray) {
            url = url.trim();
            if (url.length() == 0) {
                continue;
            }

            urlList.add(url);
        }

        return urlList.toArray(NULL_STRING_ARRAY);
    }

    public FilterConfig getConfig() {
        return config;
    }
}
