package com.sishuok.es.showcase.sample.repository;

import org.es.framework.common.repository.BaseRepository;

import com.sishuok.es.showcase.sample.entity.Sample;

public interface SampleRepository extends BaseRepository<Sample, Long> {

    Sample findByName(String name);

}
