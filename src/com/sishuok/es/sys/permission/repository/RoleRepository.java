package com.sishuok.es.sys.permission.repository;

import org.es.framework.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import com.sishuok.es.sys.permission.entity.Role;
import com.sishuok.es.sys.permission.entity.RoleResourcePermission;

public interface RoleRepository extends BaseRepository<Role, Long> {

    @Query("from RoleResourcePermission where role=?1 and resourceId=?2")
    RoleResourcePermission findRoleResourcePermission(Role role, Long resourceId);
}
