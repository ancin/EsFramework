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
 * @version $Id: TextareaTag.java, v 0.1 2014��11��19�� ����11:35:27 kejun.song Exp $
 */
public class TextareaTag extends org.springframework.web.servlet.tags.form.TextareaTag {

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