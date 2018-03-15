package com.sishuok.es.sys.group.service;

import java.util.Map;
import java.util.Set;

import org.es.framework.common.entity.search.SearchOperator;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sishuok.es.sys.group.entity.Group;
import com.sishuok.es.sys.group.repository.GroupRepository;

@Service
public class GroupService extends BaseService<Group, Long> {

    @Autowired
    private GroupRelationService groupRelationService;

    private GroupRepository getGroupRepository() {
        return (GroupRepository) baseRepository;
    }

    public Set<Map<String, Object>> findIdAndNames(Searchable searchable, String groupName) {

        searchable.addSearchFilter("name", SearchOperator.like, groupName);

        return Sets.newHashSet(Lists.transform(findAll(searchable).getContent(),
            new Function<Group, Map<String, Object>>() {
                @Override
                public Map<String, Object> apply(Group input) {
                    Map<String, Object> data = Maps.newHashMap();
                    data.put("label", input.getName());
                    data.put("value", input.getId());
                    return data;
                }
            }));
    }

    /**
	 * 
	 *
	 * @param userId
	 * @param organizationIds
	 * @return
	 */
    public Set<Long> findShowGroupIds(Long userId, Set<Long> organizationIds) {
        Set<Long> groupIds = Sets.newHashSet();
        groupIds.addAll(getGroupRepository().findDefaultGroupIds());
        groupIds.addAll(groupRelationService.findGroupIds(userId, organizationIds));

        for (Group group : findAll()) {
            if (Boolean.FALSE.equals(group.getShow())) {
                groupIds.remove(group.getId());
            }
        }

        return groupIds;
    }
}
