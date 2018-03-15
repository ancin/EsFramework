package com.sishuok.es.sys.group.repository;

import java.util.List;
import java.util.Set;

import org.es.framework.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sishuok.es.sys.group.entity.GroupRelation;

public interface GroupRelationRepository extends BaseRepository<GroupRelation, Long> {

    GroupRelation findByGroupIdAndUserId(Long groupId, Long userId);

    /**
     * ��Χ�� �����ָ����Χ�� ��û��Ҫ������һ�� �統ǰ��[10,20] ������ݿ���[9,21] 10<=9 and 21>=20
     *
     * @param groupId
     * @param startUserId
     * @param endUserId
     * @return
     */
    GroupRelation findByGroupIdAndStartUserIdLessThanEqualAndEndUserIdGreaterThanEqual(Long groupId,
                                                                                       Long startUserId,
                                                                                       Long endUserId);

    /**
     * ɾ�������ڵ����� ��Ϊ֮ǰ�Ѿ���һ���������������
     *
     * @param startUserId
     * @param endUserId
     */
    @Modifying
    @Query("delete from GroupRelation where (startUserId>=?1 and endUserId<=?2) or (userId>=?1 and userId<=?2)")
    void deleteInRange(Long startUserId, Long endUserId);

    GroupRelation findByGroupIdAndOrganizationId(Long groupId, Long organizationId);

    @Query("select groupId from GroupRelation where userId=?1 or (startUserId<=?1 and endUserId>=?1))")
    List<Long> findGroupIds(Long userId);

    @Query("select groupId from GroupRelation where userId=?1 or (startUserId<=?1 and endUserId>=?1) or (organizationId in (?2))")
    List<Long> findGroupIds(Long userId, Set<Long> organizationIds);

    //����ɾ���û� ��Ϊ�û������߼�ɾ��
    @Modifying
    @Query("delete from GroupRelation r where "
           + "not exists (select 1 from Group g where r.groupId = g.id) or "
           + "not exists(select 1 from Organization o where r.organizationId = o.id)")
    void clearDeletedGroupRelation();

}
