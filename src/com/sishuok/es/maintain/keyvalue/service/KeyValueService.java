package com.sishuok.es.maintain.keyvalue.service;

import org.es.framework.common.service.BaseService;
import org.springframework.stereotype.Service;

import com.sishuok.es.maintain.keyvalue.entity.KeyValue;
import com.sishuok.es.maintain.keyvalue.repository.KeyValueRepository;

@Service
public class KeyValueService extends BaseService<KeyValue, Long> {

    private KeyValueRepository getKeyValueRepository() {
        return (KeyValueRepository) baseRepository;
    }

    public KeyValue findByKey(String key) {
        return getKeyValueRepository().findByKey(key);
    }

}
