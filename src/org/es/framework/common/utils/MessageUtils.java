package org.es.framework.common.utils;

import org.springframework.context.MessageSource;

public class MessageUtils {

    private static MessageSource messageSource;

    public static String message(String code, Object... args) {
        if (messageSource == null) {
            messageSource = SpringUtils.getBean(MessageSource.class);
        }
        return messageSource.getMessage(code, args, null);
    }

}
