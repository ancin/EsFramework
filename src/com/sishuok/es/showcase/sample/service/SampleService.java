package com.sishuok.es.showcase.sample.service;

import org.es.framework.common.service.BaseService;
import org.springframework.stereotype.Service;

import com.sishuok.es.showcase.sample.entity.Sample;
import com.sishuok.es.showcase.sample.repository.SampleRepository;

@Service
public class SampleService extends BaseService<Sample, Long> {

    private SampleRepository getSampleRepository() {
        return (SampleRepository) baseRepository;
    }

    public Sample findByName(String name) {
        return getSampleRepository().findByName(name);
    }

}
