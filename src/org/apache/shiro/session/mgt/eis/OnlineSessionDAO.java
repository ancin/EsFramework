package org.apache.shiro.session.mgt.eis;

import java.io.Serializable;
import java.util.Date;

import org.apache.shiro.ShiroConstants;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.OnlineSession;
import org.apache.shiro.session.mgt.OnlineSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sishuok.es.sys.user.entity.UserOnline;
import com.sishuok.es.sys.user.service.UserOnlineService;

/***
 * ����db�Ĳ��� ����ʹ�� �첽+���л���
 * 
 * @author kejun.song
 * @version $Id: OnlineSessionDAO.java, v 0.1 2014��11��19�� ����2:45:41 kejun.song Exp $
 */
public class OnlineSessionDAO extends EnterpriseCacheSessionDAO {
    /**
     * �ϴ�ͬ�����ݿ��ʱ���
     */
    private static final String  LAST_SYNC_DB_TIMESTAMP = OnlineSessionDAO.class.getName()
                                                          + "LAST_SYNC_DB_TIMESTAMP";

    @Autowired
    private UserOnlineService    userOnlineService;

    @Autowired
    private OnlineSessionFactory onlineSessionFactory;

    /**
     * ͬ��session�����ݿ������ ��λΪ���루Ĭ��5���ӣ�
     */
    private long                 dbSyncPeriod           = 5 * 60 * 1000;

    public void setDbSyncPeriod(long dbSyncPeriod) {
        this.dbSyncPeriod = dbSyncPeriod;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        UserOnline userOnline = userOnlineService.findOne(String.valueOf(sessionId));
        if (userOnline == null) {
            return null;
        }
        return onlineSessionFactory.createSession(userOnline);
    }

    /**
     * ���Ựͬ����DB
     *
     * @param session
     */
    public void syncToDb(OnlineSession session) {

        Date lastSyncTimestamp = (Date) session.getAttribute(LAST_SYNC_DB_TIMESTAMP);

        //���Ự�е����Ըı�ʱ-->ǿ��ͬ��
        //����������Զ�ʧӰ�첻�� ���Կ��ǰ���鹦��ȥ��
        if (lastSyncTimestamp != null) {
            boolean needSync = true;
            long deltaTime = session.getLastAccessTime().getTime() - lastSyncTimestamp.getTime();
            if (deltaTime < dbSyncPeriod) { //ʱ����  ����ͬ��
                needSync = false;
            }
            boolean isGuest = session.getUserId() == null || session.getUserId() == 0L;

            //��������ο� ��session ���ݱ���� ͬ��
            if (isGuest == false && session.isAttributeChanged()) {
                needSync = true;
            }

            if (needSync == false) {
                return;
            }
        }

        session.setAttribute(LAST_SYNC_DB_TIMESTAMP, session.getLastAccessTime());

        //������� ���ñ�ʶ
        if (session.isAttributeChanged()) {
            session.resetAttributeChanged();
        }

        userOnlineService.online(UserOnline.fromOnlineSession(session));

    }

    /**
     * �Ự����ʱ ���ߴ���
     *
     * @param session
     */
    @Override
    protected void doDelete(Session session) {
        OnlineSession onlineSession = (OnlineSession) session;
        //��ʱ����ɾ���Ĵ�ʱ�Ͳ�ɾ����
        if (onlineSession.getAttribute(ShiroConstants.ONLY_CLEAR_CACHE) == null) {
            try {
                userOnlineService.offline(String.valueOf(onlineSession.getId()));
            } catch (Exception e) {
                //��ʹɾ��ʧ��Ҳ����ν
            }
        }

    }

}
