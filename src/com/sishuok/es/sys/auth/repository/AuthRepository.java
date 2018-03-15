package com.sishuok.es.sys.auth.repository;

import java.util.Set;

import org.es.framework.common.repository.BaseRepository;

import com.sishuok.es.sys.auth.entity.Auth;

public interface AuthRepository extends BaseRepository<Auth, Long> {

    Auth findByUserId(Long userId);

    Auth findByGroupId(Long groupId);

    Auth findByOrganizationIdAndJobId(Long organizationId, Long jobId);

    public Set<Long> findRoleIds(Long userId, Set<Long> groupIds, Set<Long> organizationIds,
                                 Set<Long> jobIds, Set<Long[]> organizationJobIds);

}
