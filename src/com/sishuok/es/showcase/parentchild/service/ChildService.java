package com.sishuok.es.showcase.parentchild.service;

import java.util.List;

import org.es.framework.common.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sishuok.es.showcase.parentchild.entity.Child;
import com.sishuok.es.showcase.parentchild.entity.Parent;
import com.sishuok.es.showcase.parentchild.repository.ChildRepository;

@Service
public class ChildService extends BaseService<Child, Long> {

    private ChildRepository getChildRepository() {
        return (ChildRepository) baseRepository;
    }

    public ChildService() {
    }

    public Page<Child> findByParent(Parent parent, Pageable pageable) {
        return getChildRepository().findByParent(parent, pageable);
    }

    Page<Child> findByParents(List<Parent> parents, Pageable pageable) {
        return getChildRepository().findByParents(parents, pageable);
    }

    public void deleteByParent(Parent parent) {
        getChildRepository().deleteByParent(parent);
    }
}
