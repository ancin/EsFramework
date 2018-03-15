package com.sishuok.es.showcase.deleted.service;

import org.es.framework.common.service.BaseService;
import org.springframework.stereotype.Service;

import com.sishuok.es.showcase.deleted.entity.DeletedSample;
import com.sishuok.es.showcase.deleted.repository.DeletedSampleRepository;

@Service
public class DeletedSampleService extends BaseService<DeletedSample, Long> {

    private DeletedSampleRepository getSampleRepository() {
        return (DeletedSampleRepository) baseRepository;
    }

    public DeletedSample findByName(String name) {
        return getSampleRepository().findByName(name);
    }

}
