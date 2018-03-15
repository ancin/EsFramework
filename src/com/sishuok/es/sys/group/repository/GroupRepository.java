package com.sishuok.es.sys.group.repository;

import java.util.List;

import org.es.framework.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import com.sishuok.es.sys.group.entity.Group;

public interface GroupRepository extends BaseRepository<Group, Long> {

    @Query("select id from Group where defaultGroup=true and show=true")
    List<Long> findDefaultGroupIds();

}
