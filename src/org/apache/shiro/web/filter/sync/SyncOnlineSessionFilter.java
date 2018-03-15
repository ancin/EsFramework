package org.apache.shiro.web.filter.sync;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.ShiroConstants;
import org.apache.shiro.session.mgt.OnlineSession;
import org.apache.shiro.session.mgt.eis.OnlineSessionDAO;
import org.apache.shiro.web.filter.PathMatchingFilter;

/**
 * ͬ����ǰ�Ự���ݵ����ݿ�
 * 
 * 
 */
public class SyncOnlineSessionFilter extends PathMatchingFilter {

    private OnlineSessionDAO onlineSessionDAO;

    public void setOnlineSessionDAO(OnlineSessionDAO onlineSessionDAO) {
        this.onlineSessionDAO = onlineSessionDAO;
    }

    /**
     * ͬ���Ự���ݵ�DB һ���������ͬ��һ�� ��ֹ���ദ��  ��Ҫ�ŵ�Shiro������֮ǰ
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        OnlineSession session = (OnlineSession) request.getAttribute(ShiroConstants.ONLINE_SESSION);
        //���session stop�� Ҳ��ͬ��
        if (session != null && session.getStopTimestamp() == null) {
            onlineSessionDAO.syncToDb(session);
        }
        return true;
    }

}
