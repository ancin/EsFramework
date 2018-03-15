package com.sishuok.es.maintain.notification.exception;

public class TemplateNotFoundException extends TemplateException {
    /**  */
    private static final long serialVersionUID = 1L;

    public TemplateNotFoundException(String templateName) {
        super("notification.template.not.found", new Object[] { templateName });
    }
}
