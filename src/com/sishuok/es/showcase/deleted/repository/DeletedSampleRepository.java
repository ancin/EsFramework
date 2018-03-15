package com.sishuok.es.showcase.deleted.repository;

import org.es.framework.common.repository.BaseRepository;

import com.sishuok.es.showcase.deleted.entity.DeletedSample;

public interface DeletedSampleRepository extends BaseRepository<DeletedSample, Long> {

    DeletedSample findByName(String name);

}
