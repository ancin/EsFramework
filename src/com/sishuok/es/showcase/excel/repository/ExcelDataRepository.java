package com.sishuok.es.showcase.excel.repository;

import org.es.framework.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sishuok.es.showcase.excel.entity.ExcelData;

public interface ExcelDataRepository extends BaseRepository<ExcelData, Long> {

    public void truncate();

    @Modifying
    @Query(value = "insert into showcase_excel_data (id, content) values(?1,?2)", nativeQuery = true)
    public void save(Long id, String content);

}
