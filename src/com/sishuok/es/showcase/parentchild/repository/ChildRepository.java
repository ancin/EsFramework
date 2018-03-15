package com.sishuok.es.showcase.parentchild.repository;

import java.util.List;

import org.es.framework.common.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sishuok.es.showcase.parentchild.entity.Child;
import com.sishuok.es.showcase.parentchild.entity.Parent;

public interface ChildRepository extends BaseRepository<Child, Long> {

    @Query(value = "select o from Child o where o.parent=?1")
    Page<Child> findByParent(Parent parent, Pageable pageable);

    @Query(value = "select o from Child o where o.parent in(?1)")
    Page<Child> findByParents(List<Parent> parents, Pageable pageable);

    @Modifying
    @Query(value = "delete from Child where parent = ?1")
    void deleteByParent(Parent parent);
}
