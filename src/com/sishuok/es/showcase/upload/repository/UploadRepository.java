package com.sishuok.es.showcase.upload.repository;

import org.es.framework.common.repository.BaseRepository;

import com.sishuok.es.showcase.upload.entity.Upload;

public interface UploadRepository extends BaseRepository<Upload, Long> {

    Upload findByName(String name);

}
