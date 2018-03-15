package com.sishuok.es.maintain.icon.service;

import org.es.framework.common.service.BaseService;
import org.springframework.stereotype.Service;

import com.sishuok.es.maintain.icon.entity.Icon;
import com.sishuok.es.maintain.icon.repository.IconRepository;

@Service
public class IconService extends BaseService<Icon, Long> {

    private IconRepository getIconRepository() {
        return (IconRepository) baseRepository;
    }

    public Icon findByIdentity(String identity) {
        return getIconRepository().findByIdentity(identity);
    }
}
