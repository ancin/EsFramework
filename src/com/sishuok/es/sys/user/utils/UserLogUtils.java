package com.sishuok.es.sys.user.utils;

import org.es.framework.common.utils.IpUtils;
import org.es.framework.common.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UserLogUtils {

    private static final Logger SYS_USER_LOGGER = LoggerFactory.getLogger("es-sys-user");

    public Logger getSysUserLog() {
        return SYS_USER_LOGGER;
    }

    public static void log(String username, String op, String msg, Object... args) {
        StringBuilder s = new StringBuilder();
        s.append(LogUtils.getBlock(getIp()));
        s.append(LogUtils.getBlock(username));
        s.append(LogUtils.getBlock(op));
        s.append(LogUtils.getBlock(msg));

        SYS_USER_LOGGER.info(s.toString(), args);
    }

    @SuppressWarnings("unused")
    public static Object getIp() {
        RequestAttributes requestAttributes = null;

        try {
            RequestContextHolder.currentRequestAttributes();
        } catch (Exception e) {
            //ignore
        }

        if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
            return IpUtils.getIpAddr(((ServletRequestAttributes) requestAttributes).getRequest());
        }

        return "unknown";

    }

}
