package com.sishuok.es.extra.exception.web.entity;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.shiro.authz.UnauthorizedException;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: ExceptionResponse.java, v 0.1 2014��11��19�� ����2:59:22 kejun.song Exp $
 */
public class ExceptionResponse {

    private String exception;

    private String message;

    private String stackTrace;

    private ExceptionResponse() {

    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString() {
        return "ExceptionResponse{" + "exception='" + exception + '\'' + ", message='" + message
               + '\'' + ", stackTrace='" + stackTrace + '\'' + '}';
    }

    public static ExceptionResponse from(Throwable e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();

        String errorMessage = "<h3 style='display: inline;'>�����ˣ�</h3><br/>������Ϣ��"
                              + convertMessage(e);

        exceptionResponse.setMessage(errorMessage);

        exceptionResponse.setException(e.getClass().getName());

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        exceptionResponse.setStackTrace(stringWriter.toString());

        return exceptionResponse;
    }

    private static String convertMessage(Throwable e) {

        String errorMessage = e.getMessage();
        //��֤ʧ��
        if (e instanceof UnauthorizedException) {
            if (errorMessage.startsWith("Subject does not have permission")) {
                errorMessage = errorMessage.replaceAll("Subject does not have permission",
                    "��û�в���Ȩ�ޣ�����ϵ����Ա���Ȩ��");
            }
            if (errorMessage.startsWith("User is not permitted")) {
                errorMessage = errorMessage.replaceAll("User is not permitted",
                    "��û�в���Ȩ�ޣ�����ϵ����Ա���Ȩ��");
            }
            if (errorMessage.startsWith("Subject does not have role")) {
                errorMessage = errorMessage.replaceAll("Subject does not have role",
                    "��û�в���Ȩ�ޣ�����ϵ����Ա��ӽ�ɫ");
            }
        }

        return errorMessage;
    }
}
