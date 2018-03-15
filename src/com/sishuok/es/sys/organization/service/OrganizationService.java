package com.sishuok.es.sys.organization.service;

import java.util.Iterator;
import java.util.Set;

import org.es.framework.common.plugin.serivce.BaseTreeableService;
import org.springframework.stereotype.Service;

import com.sishuok.es.sys.organization.entity.Organization;

@Service
public class OrganizationService extends BaseTreeableService<Organization, Long> {

    /**
     * 
     * @param organizationIds
     * @param organizationJobIds
     */
    public void filterForCanShow(Set<Long> organizationIds, Set<Long[]> organizationJobIds) {

        Iterator<Long> iter1 = organizationIds.iterator();

        while (iter1.hasNext()) {
            Long id = iter1.next();
            Organization o = findOne(id);
            if (o == null || Boolean.FALSE.equals(o.getShow())) {
                iter1.remove();
            }
        }

        Iterator<Long[]> iter2 = organizationJobIds.iterator();

        while (iter2.hasNext()) {
            Long id = iter2.next()[0];
            Organization o = findOne(id);
            if (o == null || Boolean.FALSE.equals(o.getShow())) {
                iter2.remove();
            }
        }

    }
}
