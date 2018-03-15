/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.es.framework.common.web.form.bind;

import javax.servlet.jsp.PageContext;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.HtmlUtils;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: SearchBindStatus.java, v 0.1 2014年11月19日 上午11:35:59 kejun.song Exp $
 */
public class SearchBindStatus extends BindStatus {

    private static final String pathToUse = "___search";

    private Object              value;
    private boolean             htmlEscape;

    private SearchBindStatus(RequestContext requestContext, String path, boolean htmlEscape)
                                                                                            throws IllegalStateException {
        super(requestContext, path, htmlEscape);
    }

    public static BindStatus create(PageContext pageContext, String name,
                                    RequestContext requestContext, boolean htmlEscape) {
        pageContext.getRequest().setAttribute(pathToUse, SearchModel.EMPTY);
        SearchBindStatus bindStatus = new SearchBindStatus(requestContext, pathToUse, htmlEscape);
        bindStatus.value = getValue(pageContext, name);
        bindStatus.htmlEscape = htmlEscape;
        return bindStatus;
    }

    public static Object getValue(PageContext pageContext, String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Object value = null;
        String[] parameters = pageContext.getRequest().getParameterValues(name);
        if (parameters != null && parameters.length == 1) {
            value = parameters[0];
        } else {
            value = parameters;
        }
        return value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Object getActualValue() {
        return value;
    }

    @Override
    public String getDisplayValue() {
        if (this.value instanceof String) {
            return (String) this.value;
        }
        if (this.value != null) {
            return (this.htmlEscape ? HtmlUtils.htmlEscape(this.value.toString()) : this.value
                .toString());
        }
        return "";
    }

    private static class SearchModel {
        public static final SearchModel EMPTY = new SearchModel();
    }

}
