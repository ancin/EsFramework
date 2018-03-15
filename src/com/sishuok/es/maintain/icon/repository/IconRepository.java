package com.sishuok.es.maintain.icon.repository;

import org.es.framework.common.repository.BaseRepository;

import com.sishuok.es.maintain.icon.entity.Icon;

public interface IconRepository extends BaseRepository<Icon, Long> {
    Icon findByIdentity(String identity);
}
