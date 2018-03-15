/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.es.framework.common.web.form;

import javax.servlet.jsp.JspException;

import org.es.framework.common.web.form.bind.SearchBindStatus;
import org.springframework.web.servlet.support.BindStatus;

/**
 * 取值时
 * 1、先取parameter
 * 2、如果找不到再找attribute (page--->request--->session--->application)
 * 
 */
public class CheckboxesTag extends org.springframework.web.servlet.tags.form.CheckboxesTag {

    /**  */
    private static final long serialVersionUID = 1L;
    private BindStatus        bindStatus       = null;

    @Override
    protected BindStatus getBindStatus() throws JspException {
        if (this.bindStatus == null) {
            this.bindStatus = SearchBindStatus.create(pageContext, getName(), getRequestContext(),
                false);
        }
        return this.bindStatus;
    }

    @Override
    protected String getPropertyPath() throws JspException {
        return getPath();
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.bindStatus = null;
    }

}
