package com.sishuok.es.sys.user.service;

import java.util.Date;
import java.util.List;

import org.es.framework.common.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sishuok.es.sys.user.entity.UserOnline;
import com.sishuok.es.sys.user.repository.UserOnlineRepository;

@Service
public class UserOnlineService extends BaseService<UserOnline, String> {

    private UserOnlineRepository getUserOnlineRepository() {
        return (UserOnlineRepository) baseRepository;
    }

    /**
     * ����
     *
     * @param userOnline
     */
    public void online(UserOnline userOnline) {
        save(userOnline);
    }

    /**
     * ����
     *
     * @param sid
     */
    public void offline(String sid) {
        UserOnline userOnline = findOne(sid);
        if (userOnline != null) {
            delete(userOnline);
        }
        //�ο� �����¼�ϴη��ʼ�¼
        //�˴�ʹ�����ݿ�Ĵ��������ͬ��
        //        if(userOnline.getUserId() == null) {
        //            userLastOnlineService.lastOnline(UserLastOnline.fromUserOnline(userOnline));
        //        }
    }

    /**
     * ��������
     *
     * @param needOfflineIdList
     */
    public void batchOffline(List<String> needOfflineIdList) {
        getUserOnlineRepository().batchDelete(needOfflineIdList);
    }

    /**
     * ��Ч��UserOnline
     *
     * @return
     */
    public Page<UserOnline> findExpiredUserOnlineList(Date expiredDate, Pageable pageable) {
        return getUserOnlineRepository().findExpiredUserOnlineList(expiredDate, pageable);
    }

}
