package org.es.framework.common.web.form;

import org.springframework.util.ObjectUtils;
import org.springframework.web.util.HtmlUtils;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: ValueFormatter.java, v 0.1 2014年11月19日 上午11:35:40 kejun.song Exp $
 */
public class ValueFormatter {
    public static String getDisplayString(Object value, boolean htmlEscape) {
        String displayValue = ObjectUtils.getDisplayString(value);
        return (htmlEscape ? HtmlUtils.htmlEscape(displayValue) : displayValue);
    }
}
