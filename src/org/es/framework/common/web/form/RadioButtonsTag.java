/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.es.framework.common.web.form;

import javax.servlet.jsp.JspException;

import org.es.framework.common.web.form.bind.SearchBindStatus;
import org.springframework.web.servlet.support.BindStatus;

/***
 *  ȡֵʱ
 * 1����ȡparameter
 * 2������Ҳ�������attribute (page--->request--->session--->application)
 * 
 * @author kejun.song
 * @version $Id: RadioButtonsTag.java, v 0.1 2014��11��19�� ����11:34:51 kejun.song Exp $
 */
public class RadioButtonsTag extends org.springframework.web.servlet.tags.form.RadioButtonsTag {

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
