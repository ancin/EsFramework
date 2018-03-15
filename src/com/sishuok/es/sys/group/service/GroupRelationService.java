package com.sishuok.es.sys.group.service;

import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.es.framework.common.service.BaseService;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.sishuok.es.sys.group.entity.GroupRelation;
import com.sishuok.es.sys.group.repository.GroupRelationRepository;

@Service
public class GroupRelationService extends BaseService<GroupRelation, Long> {

    private GroupRelationRepository getGroupRelationRepository() {
        return (GroupRelationRepository) baseRepository;
    }

    public void appendRelation(Long groupId, Long[] organizationIds) {
        if (ArrayUtils.isEmpty(organizationIds)) {
            return;
        }
        for (Long organizationId : organizationIds) {
            if (organizationId == null) {
                continue;
            }
            GroupRelation r = getGroupRelationRepository().findByGroupIdAndOrganizationId(groupId,
                organizationId);
            if (r == null) {
                r = new GroupRelation();
                r.setGroupId(groupId);
                r.setOrganizationId(organizationId);
                save(r);
            }
        }
    }

    public void appendRelation(Long groupId, Long[] userIds, Long[] startUserIds, Long[] endUserIds) {
        if (ArrayUtils.isEmpty(userIds) && ArrayUtils.isEmpty(startUserIds)) {
            return;
        }
        if (!ArrayUtils.isEmpty(userIds)) {
            for (Long userId : userIds) {
                if (userId == null) {
                    continue;
                }
                GroupRelation r = getGroupRelationRepository().findByGroupIdAndUserId(groupId,
                    userId);
                if (r == null) {
                    r = new GroupRelation();
                    r.setGroupId(groupId);
                    r.setUserId(userId);
                    save(r);
                }
            }
        }

        if (!ArrayUtils.isEmpty(startUserIds)) {
            for (int i = 0, l = startUserIds.length; i < l; i++) {
                Long startUserId = startUserIds[i];
                Long endUserId = endUserIds[i];
                GroupRelation r = getGroupRelationRepository()
                    .findByGroupIdAndStartUserIdLessThanEqualAndEndUserIdGreaterThanEqual(groupId,
                        startUserId, endUserId);

                if (r == null) {
                    getGroupRelationRepository().deleteInRange(startUserId, endUserId);
                    r = new GroupRelation();
                    r.setGroupId(groupId);
                    r.setStartUserId(startUserId);
                    r.setEndUserId(endUserId);
                    save(r);
                }

            }
        }
    }

    public Set<Long> findGroupIds(Long userId, Set<Long> organizationIds) {
        if (organizationIds.isEmpty()) {
            return Sets.newHashSet(getGroupRelationRepository().findGroupIds(userId));
        }

        return Sets.newHashSet(getGroupRelationRepository().findGroupIds(userId, organizationIds));
    }

}
