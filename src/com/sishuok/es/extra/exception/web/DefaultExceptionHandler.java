package com.sishuok.es.extra.exception.web;

import org.apache.shiro.authz.UnauthorizedException;
import org.es.framework.common.utils.LogUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.sishuok.es.extra.exception.web.entity.ExceptionResponse;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: DefaultExceptionHandler.java, v 0.1 2014年11月19日 下午2:58:51 kejun.song Exp $
 */
@ControllerAdvice
public class DefaultExceptionHandler {

    /**
     * 没有权限 异常
     * <p/>
     * 后续根据不同的需求定制即可
     */
    @ExceptionHandler({ UnauthorizedException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView processUnauthenticatedException(NativeWebRequest request,
                                                        UnauthorizedException e) {
        LogUtils.logError("用户权限验证失败", e);
        ExceptionResponse exceptionResponse = ExceptionResponse.from(e);

        ModelAndView mv = new ModelAndView();
        mv.addObject("error", exceptionResponse);
        mv.setViewName("error/exception");

        return mv;
    }

}
