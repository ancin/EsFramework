package com.sishuok.es.sys.user.repository;

import org.es.framework.common.repository.BaseRepository;

import com.sishuok.es.sys.user.entity.UserLastOnline;

public interface UserLastOnlineRepository extends BaseRepository<UserLastOnline, Long> {

    UserLastOnline findByUserId(Long userId);
}
