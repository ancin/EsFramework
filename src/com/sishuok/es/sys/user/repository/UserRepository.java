package com.sishuok.es.sys.user.repository;

import org.es.framework.common.repository.BaseRepository;
import org.es.framework.common.repository.support.annotation.SearchableQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.entity.UserOrganizationJob;

@SearchableQuery(callbackClass = UserSearchCallback.class)
public interface UserRepository extends BaseRepository<User, Long> {

    User findByUsername(String username);

    User findByMobilePhoneNumber(String mobilePhoneNumber);

    User findByEmail(String email);

    @Query("from UserOrganizationJob where user=?1 and organizationId=?2 and jobId=?3")
    UserOrganizationJob findUserOrganization(User user, Long organizationId, Long jobId);

    @Query("select uoj from UserOrganizationJob uoj where not exists(select 1 from Job j where uoj.jobId=j.id) or not exists(select 1 from Organization o where uoj.organizationId=o.id)")
    Page<UserOrganizationJob> findUserOrganizationJobOnNotExistsOrganizationOrJob(Pageable pageable);

    @Modifying
    @Query("delete from UserOrganizationJob uoj where not exists(select 1 from User u where uoj.user=u)")
    void deleteUserOrganizationJobOnNotExistsUser();
}
