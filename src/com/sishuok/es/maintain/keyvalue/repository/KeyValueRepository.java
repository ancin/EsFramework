package com.sishuok.es.maintain.keyvalue.repository;

import org.es.framework.common.repository.BaseRepository;

import com.sishuok.es.maintain.keyvalue.entity.KeyValue;

public interface KeyValueRepository extends BaseRepository<KeyValue, Long> {

    KeyValue findByKey(String key);

}
