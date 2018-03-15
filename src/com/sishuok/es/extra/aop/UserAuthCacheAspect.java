package com.sishuok.es.extra.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.es.framework.common.cache.BaseCacheAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sishuok.es.sys.auth.entity.Auth;
import com.sishuok.es.sys.auth.service.AuthService;
import com.sishuok.es.sys.group.entity.Group;
import com.sishuok.es.sys.group.entity.GroupRelation;
import com.sishuok.es.sys.group.service.GroupRelationService;
import com.sishuok.es.sys.group.service.GroupService;
import com.sishuok.es.sys.organization.entity.Job;
import com.sishuok.es.sys.organization.entity.Organization;
import com.sishuok.es.sys.organization.service.JobService;
import com.sishuok.es.sys.organization.service.OrganizationService;
import com.sishuok.es.sys.permission.entity.Permission;
import com.sishuok.es.sys.permission.entity.Role;
import com.sishuok.es.sys.permission.service.PermissionService;
import com.sishuok.es.sys.permission.service.RoleService;
import com.sishuok.es.sys.resource.entity.Resource;
import com.sishuok.es.sys.resource.service.ResourceService;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.service.UserService;

/**
 * �û�Ȩ�޵�����
 * <p/>
 * 1�����������·���ʱ���ӻ���
 * {@link com.sishuok.es.sys.auth.service.UserAuthService#findRoles}
 * {@link com.sishuok.es.sys.auth.service.UserAuthService#findStringRoles}
 * {@link com.sishuok.es.sys.auth.service.UserAuthService#findStringPermissions}
 * <p/>
 * 2����Ȩ��Auth��
 * ����ɾ����Ȩʱ��
 * ������û���صģ�ֻɾ�û��ļ��ɣ�
 * ������ȫ������
 * <p/>
 * 3��1����Դ��Resource��
 * ���޸���Դʱ�ж��Ƿ����仯����resourceIdentity���Ƿ���ʾ������������建��
 * ��ɾ����Դʱ���建��
 * 3.2��Ȩ�ޣ�Permission��
 * ���޸�Ȩ��ʱ�ж��Ƿ����仯����permission���Ƿ���ʾ������������建��
 * ��ɾ��Ȩ��ʱ���建��
 * <p/>
 * 4����ɫ��Role��
 * ��ɾ����ɫʱ���뻺��
 * ���޸Ľ�ɫshow/role/resourcePermissions��ϵʱ���建��
 * <p/>
 * 5.1����֯����
 * ��ɾ��/�޸�show/parentId�ֶ�ʱ���建��
 * 5.2������ְ��
 * ��ɾ��/�޸�show/parentId�ֶ�ʱ���建��
 * <p/>
 * 6��1����
 * ���޸����Ĭ����/showʱ���建��
 * ��ɾ����ʱ���建��
 * <p/>
 * 6.2����ɾ�����ϵʱ
 * ������/�޸�/ɾ������ĳ���û��ģ�ֻ��������û��Ļ���
 * ���������л���
 * <p/>
 * 7���û�
 * �޸�ʱ�������֯����/����ְ����ˣ��������Լ��Ļ���
 * <p/>
 * �����Լ�ʱҲͬʱ����˵��Ļ���
 * <p/>
 * TODO
 * 1���첽ʧЧ����
 * 2��Ԥ��仺�棨���Ѵ˿�ʧЧ����ͨ���첽�����һ�Σ� Ŀǰֻ��ǰ100����
 * 3���Ӷ������� ���ʧЧ�ٲ��Ч��
 * <p/>
 * �˷�����һ��ȱ����� ֻҪ����һ�������л���ʧЧ��������
 * TODO ˼�����õ�������
 * <p/>
 */
@Component
@Aspect
public class UserAuthCacheAspect extends BaseCacheAspect {

    public UserAuthCacheAspect() {
        setCacheName("sys-authCache");
    }

    private final String            rolesKeyPrefix             = "roles-";
    private final String            stringRolesKeyPrefix       = "string-roles-";
    private final String            stringPermissionsKeyPrefix = "string-permissions-";

    @Autowired
    private ResourceMenuCacheAspect resourceMenuCacheAspect;

    @Autowired
    private AuthService             authService;
    @Autowired
    private ResourceService         resourceService;
    @Autowired
    private PermissionService       permissionService;
    @Autowired
    private RoleService             roleService;
    @Autowired
    private OrganizationService     organizationService;
    @Autowired
    private JobService              jobService;
    @Autowired
    private GroupService            groupService;
    @Autowired
    private GroupRelationService    groupRelationService;
	@Autowired(required = true)
    private UserService             userService;

    ////////////////////////////////////////////////////////////////////////////////
    ////�����
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * 2����Ȩ��Auth��
     * ����ɾ����Ȩʱ��
     * ������û���صģ�ֻɾ�û��ļ��ɣ�
     * ������ȫ������
     */
    @Pointcut(value = "target(com.sishuok.es.sys.auth.service.AuthService)")
    private void authServicePointcut() {
    }

    @Pointcut(value = "execution(* addGroupAuth(..)) "
                      + "|| execution(* addOrganizationJobAuth(..)) "
                      + "|| execution(* addOrganizationJobAuth(..))")
    private void authCacheEvictAllPointcut() {
    }

    @Pointcut(value = "(execution(* addUserAuth(*,..)) && args(arg, ..)) "
                      + "|| (execution(* update(*)) && args(arg)) "
                      + "|| (execution(* save(*)) && args(arg)) "
                      + "|| (execution(* delete(*)) && args(arg))", argNames = "arg")
    private void authCacheEvictAllOrSpecialPointcut(Object arg) {
    }

    /**
     * 3.1����Դ��Resource��
     * ���޸���Դʱ�ж��Ƿ����仯����resourceIdentity���Ƿ���ʾ������������建��
     * ��ɾ����Դʱ���建��
     */
    @Pointcut(value = "target(com.sishuok.es.sys.resource.service.ResourceService)")
    private void resourceServicePointcut() {
    }

    @Pointcut(value = "execution(* delete(..))")
    private void resourceCacheEvictAllPointcut() {
    }

    @Pointcut(value = "execution(* update(*)) && args(arg)", argNames = "arg")
    private void resourceMaybeCacheEvictAllPointcut(Resource arg) {
    }

    /**
     * 3.2��Ȩ�ޣ�Permission��
     * ���޸�Ȩ��ʱ�ж��Ƿ����仯����permission���Ƿ���ʾ������������建��
     * ��ɾ��Ȩ��ʱ���建��
     */
    @Pointcut(value = "target(com.sishuok.es.sys.permission.service.PermissionService)")
    private void permissionServicePointcut() {
    }

    @Pointcut(value = "execution(* delete(..))")
    private void permissionCacheEvictAllPointcut() {
    }

    @Pointcut(value = "execution(* update(*)) && args(arg)", argNames = "arg")
    private void permissionMaybeCacheEvictAllPointcut(Permission arg) {
    }

    /**
     * 4����ɫ��Role��
     * ��ɾ����ɫʱ���뻺��
     * ���޸Ľ�ɫshow/role/resourcePermissions��ϵʱ���建��
     */
    @Pointcut(value = "target(com.sishuok.es.sys.permission.service.RoleService)")
    private void roleServicePointcut() {
    }

    @Pointcut(value = "execution(* delete(..))")
    private void roleCacheEvictAllPointcut() {
    }

    @Pointcut(value = "execution(* update(*)) && args(arg)", argNames = "arg")
    private void roleMaybeCacheEvictAllPointcut(Role arg) {
    }

    /**
     * 5.1����֯����
     * ��ɾ��/�޸�show�ֶ�ʱ���建��
     */
    @Pointcut(value = "target(com.sishuok.es.sys.organization.service.OrganizationService)")
    private void organizationServicePointcut() {
    }

    @Pointcut(value = "execution(* delete(..))")
    private void organizationCacheEvictAllPointcut() {
    }

    @Pointcut(value = "execution(* update(*)) && args(arg)", argNames = "arg")
    private void organizationMaybeCacheEvictAllPointcut(Organization arg) {
    }

    /**
     * 5.2������ְ��
     * ��ɾ��/�޸�show�ֶ�ʱ���建��
     */
    @Pointcut(value = "target(com.sishuok.es.sys.organization.service.JobService)")
    private void jobServicePointcut() {
    }

    @Pointcut(value = "execution(* delete(..))")
    private void jobCacheEvictAllPointcut() {
    }

    @Pointcut(value = "execution(* update(*)) && args(arg)", argNames = "arg")
    private void jobMaybeCacheEvictAllPointcut(Job arg) {
    }

    /**
     * 6��1����
     * ���޸����Ĭ����/showʱ���建��
     * ��ɾ����ʱ���建��
     */
    @Pointcut(value = "target(com.sishuok.es.sys.group.service.GroupService)")
    private void groupServicePointcut() {
    }

    @Pointcut(value = "execution(* delete(..))")
    private void groupCacheEvictAllPointcut() {
    }

    @Pointcut(value = "execution(* update(*)) && args(arg)", argNames = "arg")
    private void groupMaybeCacheEvictAllPointcut(Group arg) {
    }

    /**
     * 6.2����ɾ�����ϵʱ
     * �����/�޸�/ɾ������ĳ���û��ģ�ֻ��������û��Ļ���
     * ���������������
     */
    @Pointcut(value = "target(com.sishuok.es.sys.group.service.GroupRelationService)")
    private void groupRelationServicePointcut() {
    }

    @Pointcut(value = "execution(* appendRelation(*,*))")
    private void groupRelationCacheEvictAllPointcut() {
    }

    @Pointcut(value = "(execution(* delete(*)) && args(arg)) "
                      + "|| (execution(* update(*)) && args(arg)) "
                      + "|| execution(* appendRelation(*,*,*,*)) && args(*,arg,*,*) ", argNames = "arg")
    private void groupRelationMaybeCacheEvictAllOrSpecialPointcut(Object arg) {
    }

    /**
     * 7���û�
     * �޸�ʱ�������֯����/����ְ����ˣ��������Լ��Ļ���
     */
    @Pointcut(value = "target(com.sishuok.es.sys.user.service.UserService)")
    private void userServicePointcut() {
    }

    @Pointcut(value = "execution(* delete(*)) && args(arg) || execution(* update(*)) && args(arg)", argNames = "arg")
    private void userCacheEvictSpecialPointcut(Object arg) {
    }

    @Pointcut(value = "target(com.sishuok.es.sys.auth.service.UserAuthService)")
    private void userAuthServicePointcut() {
    }

    @Pointcut(value = "execution(* findRoles(*)) && args(arg)", argNames = "arg")
    private void cacheFindRolesPointcut(User arg) {
    }

    @Pointcut(value = "execution(* findStringRoles(*)) && args(arg)", argNames = "arg")
    private void cacheFindStringRolesPointcut(User arg) {
    }

    @Pointcut(value = "execution(* findStringPermissions(*)) && args(arg)", argNames = "arg")
    private void cacheFindStringPermissionsPointcut(User arg) {
    }

    //////////////////////////////////////////////////////////////////////////////////
    ////��ǿ
    //////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////
    ////��ѯʱ �黺��/�ӻ���
    //////////////////////////////////////////////////////////////////////////////////
    @Around(value = "userAuthServicePointcut() && cacheFindRolesPointcut(arg)", argNames = "pjp,arg")
    public Object findRolesCacheableAdvice(ProceedingJoinPoint pjp, User arg) throws Throwable {
        User user = arg;

        String key = null;
        if (user != null) {
            key = rolesKey(user.getId());
        }

        Object retVal = get(key);

        if (retVal != null) {
            log.debug("cacheName:{}, method:findRolesCacheableAdvice, hit key:{}", cacheName, key);
            return retVal;
        }

        log.debug("cacheName:{}, method:findRolesCacheableAdvice, miss key:{}", cacheName, key);

        retVal = pjp.proceed();

        this.put(key, retVal);

        return retVal;
    }

    @Around(value = "userAuthServicePointcut() && cacheFindStringRolesPointcut(arg)", argNames = "pjp,arg")
    public Object findStringRolesCacheableAdvice(ProceedingJoinPoint pjp, User arg)
                                                                                   throws Throwable {
        User user = arg;

        String key = null;
        if (user != null) {
            key = stringRolesKey(user.getId());
        }

        Object retVal = get(key);

        if (retVal != null) {
            log.debug("cacheName:{}, method:findStringRolesCacheableAdvice, hit key:{}", cacheName,
                key);
            return retVal;
        }
        log.debug("cacheName:{}, method:findStringRolesCacheableAdvice, miss key:{}", cacheName,
            key);

        retVal = pjp.proceed();

        this.put(key, retVal);

        return retVal;
    }

    @Around(value = "userAuthServicePointcut() && cacheFindStringPermissionsPointcut(arg)", argNames = "pjp,arg")
    public Object findStringPermissionsCacheableAdvice(ProceedingJoinPoint pjp, User arg)
                                                                                         throws Throwable {
        User user = arg;

        String key = stringPermissionsKey(user.getId());

        Object retVal = get(key);

        if (retVal != null) {
            log.debug("cacheName:{}, method:findStringPermissionsCacheableAdvice, hit key:{}",
                cacheName, key);
            return retVal;
        }
        log.debug("cacheName:{}, method:findStringPermissionsCacheableAdvice, miss key:{}",
            cacheName, key);

        retVal = pjp.proceed();

        this.put(key, retVal);

        return retVal;
    }

    //////////////////////////////////////////////////////////////////////////////////
    ////�����������
    //////////////////////////////////////////////////////////////////////////////////
    @Before("(authServicePointcut() && authCacheEvictAllPointcut()) "
            + "|| (resourceServicePointcut() && resourceCacheEvictAllPointcut()) "
            + "|| (permissionServicePointcut() && permissionCacheEvictAllPointcut()) "
            + "|| (roleServicePointcut() && roleCacheEvictAllPointcut()) "
            + "|| (organizationServicePointcut() && organizationCacheEvictAllPointcut()) "
            + "|| (jobServicePointcut() && jobCacheEvictAllPointcut()) "
            + "|| (groupServicePointcut() && groupCacheEvictAllPointcut()) "
            + "|| (groupRelationServicePointcut() && groupRelationCacheEvictAllPointcut())")
    public void cacheClearAllAdvice() {
        log.debug("cacheName:{}, method:cacheClearAllAdvice, cache clear", cacheName);
        clear();
    }

    //////////////////////////////////////////////////////////////////////////////////
    ////��������ض�/ȫ������
    //////////////////////////////////////////////////////////////////////////////////

    /**
     * @param auth
     * @return ���������з���true ����false
     */
    private boolean evictWithAuth(Auth auth) {
        boolean needEvictSpecail = auth != null && auth.getUserId() != null
                                   && auth.getGroupId() == null && auth.getOrganizationId() == null;
        if (needEvictSpecail) {
            Long userId = auth.getUserId();
            log.debug("cacheName:{}, method:evictWithAuth, evict userId:{}", cacheName, userId);
            evict(userId);
            return false;
        } else {
            log.debug("cacheName:{}, method:evictWithAuth, cache clear", cacheName);
            clear();
            return true;
        }
    }

    @Before(value = "authServicePointcut() && authCacheEvictAllOrSpecialPointcut(arg)", argNames = "jp,arg")
    public void authCacheClearSpecialOrAllAdvice(JoinPoint jp, Object arg) {
        log.debug("cacheName:{}, method:authCacheClearSpecialOrAllAdvice begin", cacheName);
        String methodName = jp.getSignature().getName();
        if (arg instanceof Auth) {//ֻ���ĳ���û��ļ���
            Auth auth = (Auth) arg;

            log.debug(
                "cacheName:{}, method:authCacheClearSpecialOrAllAdvice delegate to evictWithAuth",
                cacheName);
            evictWithAuth(auth);
        } else if ("delete".equals(methodName)) { //ɾ������
            if (arg instanceof Long) { //ɾ������
                Long authId = (Long) arg;
                Auth auth = authService.findOne(authId);

                log.debug(
                    "cacheName:{}, method:authCacheClearSpecialOrAllAdvice delegate to evictWithAuth",
                    cacheName);
                evictWithAuth(auth);
            } else if (arg instanceof Long[]) { //����ɾ��
                for (Long authId : ((Long[]) arg)) {
                    Auth auth = authService.findOne(authId);

                    log.debug(
                        "cacheName:{}, method:authCacheClearSpecialOrAllAdvice delegate to evictWithAuth",
                        cacheName);
                    if (evictWithAuth(auth)) {//�����յ������� ֱ�ӷ���
                        return;
                    }
                }
            } else if ("addUserAuth".equals(methodName)) {
                Long[] userIds = (Long[]) arg;

                log.debug(
                    "cacheName:{}, method:authCacheClearSpecialOrAllAdvice, evict user ids:{}",
                    cacheName, Arrays.toString(userIds));
                evict(userIds);
            }
        }
    }

    @Before(value = "resourceServicePointcut() && resourceMaybeCacheEvictAllPointcut(arg)", argNames = "arg")
    public void resourceMaybeCacheClearAllAdvice(Resource arg) {
        Resource resource = arg;
        if (resource == null) {
            return;
        }
        Resource dbResource = resourceService.findOne(resource.getId());
        if (dbResource == null) {
            return;
        }

        //ֻ�е�show/identity�����ı�ʱ��������
        if (!dbResource.getShow().equals(resource.getShow())
            || !dbResource.getIdentity().equals(resource.getIdentity())) {

            log.debug("cacheName:{}, method:resourceMaybeCacheClearAllAdvice, cache clear",
                cacheName);
            clear();
        }
    }

    @Before(value = "permissionServicePointcut() && permissionMaybeCacheEvictAllPointcut(arg)", argNames = "arg")
    public void permissionMaybeCacheClearAllAdvice(Permission arg) {

        Permission permission = arg;
        if (permission == null) {
            return;
        }
        Permission dbPermission = permissionService.findOne(permission.getId());
        if (dbPermission == null) {
            return;
        }

        //ֻ�е�show/permission�����ı�ʱ��������
        if (!dbPermission.getShow().equals(permission.getShow())
            || !dbPermission.getPermission().equals(permission.getPermission())) {

            log.debug("cacheName:{}, method:permissionMaybeCacheClearAllAdvice, cache clear",
                cacheName);
            clear();
        }
    }

    @Before(value = "roleServicePointcut() && roleMaybeCacheEvictAllPointcut(arg)", argNames = "arg")
    public void roleMaybeCacheClearAllAdvice(Role arg) {
        Role role = arg;
        if (role == null) {
            return;
        }
        Role dbRole = roleService.findOne(role.getId());
        if (dbRole == null) {
            return;
        }

        //ֻ�е�show/role�����ı�ʱ��������
        if (!dbRole.getShow().equals(role.getShow())
            || !dbRole.getRole().equals(role.getRole())
            || !(dbRole.getResourcePermissions().size() == role.getResourcePermissions().size() && dbRole
                .getResourcePermissions().containsAll(role.getResourcePermissions()))) {

            log.debug("cacheName:{}, method:roleMaybeCacheClearAllAdvice, cache clear", cacheName);
            clear();
        }
    }

    @Before(value = "organizationServicePointcut() && organizationMaybeCacheEvictAllPointcut(arg)", argNames = "arg")
    public void organizationMaybeCacheClearAllAdvice(Organization arg) {

        Organization organization = arg;
        if (organization == null) {
            return;
        }
        Organization dbOrganization = organizationService.findOne(organization.getId());
        if (dbOrganization == null) {
            return;
        }

        //ֻ�е�show/parentId�����ı�ʱ��������
        if (!dbOrganization.getShow().equals(organization.getShow())
            || !dbOrganization.getParentId().equals(organization.getParentId())) {

            log.debug("cacheName:{}, method:organizationMaybeCacheClearAllAdvice, cache clear",
                cacheName);
            clear();
        }
    }

    @Before(value = "jobServicePointcut() && jobMaybeCacheEvictAllPointcut(arg)", argNames = "arg")
    public void jobMaybeCacheClearAllAdvice(Job arg) {
        Job job = arg;
        if (job == null) {
            return;
        }
        Job dbJob = jobService.findOne(job.getId());
        if (dbJob == null) {
            return;
        }

        //ֻ�е�show/parentId�����ı�ʱ��������
        if (!dbJob.getShow().equals(job.getShow())
            || !dbJob.getParentId().equals(job.getParentId())) {

            log.debug("cacheName:{}, method:jobMaybeCacheClearAllAdvice, cache clear", cacheName);
            clear();
        }
    }

    @Before(value = "groupServicePointcut() && groupMaybeCacheEvictAllPointcut(arg)", argNames = "arg")
    public void groupMaybeCacheClearAllAdvice(Group arg) {
        Group group = arg;
        if (group == null) {
            return;
        }
        Group dbGroup = groupService.findOne(group.getId());
        if (dbGroup == null) {
            return;
        }

        //ֻ�е��޸����Ĭ����/showʱ��������
        if (!dbGroup.getShow().equals(group.getShow())
            || !dbGroup.getDefaultGroup().equals(group.getDefaultGroup())) {

            log.debug("cacheName:{}, method:groupMaybeCacheClearAllAdvice, cache clear", cacheName);
            clear();
        }
    }

    /**
     * @param r
     * @return ���������� ����true������false
     */
    private boolean evictForGroupRelation(GroupRelation r) {
        //����Ƿ�ĳ���û����������л���
        if (r.getStartUserId() != null || r.getEndUserId() != null || r.getOrganizationId() != null) {

            log.debug("cacheName:{}, method:evictForGroupRelation, cache clear", cacheName);
            clear();
            return true;
        }
        if (r.getUserId() != null) {// �����/�޸�/ɾ������ĳ���û��ģ�ֻ��������û��Ļ���
            evict(r.getUserId());
            GroupRelation dbR = groupRelationService.findOne(r.getId());
            if (dbR != null && !dbR.getUserId().equals(r.getUserId())) { //���a�û��滻Ϊb�û�ʱ���������û��Ļ���

                log.debug("cacheName:{}, method:evictForGroupRelation, evict userId:{}", cacheName,
                    dbR.getUserId());
                evict(dbR.getUserId());
            }
        }
        return false;
    }

    @Before(value = "groupRelationServicePointcut() && groupRelationMaybeCacheEvictAllOrSpecialPointcut(arg)", argNames = "jp,arg")
    public void groupRelationMaybeCacheClearAllOrSpecialAdvice(JoinPoint jp, Object arg) {
        String methodName = jp.getSignature().getName();
        if (arg instanceof GroupRelation) {
            GroupRelation r = (GroupRelation) arg;

            log.debug(
                "cacheName:{}, method:groupRelationMaybeCacheClearAllOrSpecialAdvice delagate to evictForGroupRelation",
                cacheName);
            if (evictForGroupRelation(r)) {
                return;
            }

        } else if ("delete".equals(methodName)) {//ɾ�����
            if (arg instanceof Long) {
                Long rId = (Long) arg;
                GroupRelation r = groupRelationService.findOne(rId);

                log.debug(
                    "cacheName:{}, method:groupRelationMaybeCacheClearAllOrSpecialAdvice delagate to evictForGroupRelation",
                    cacheName);
                if (evictForGroupRelation(r)) {
                    return;
                }
            } else if (arg instanceof Long[]) {
                for (Long rId : (Long[]) arg) {
                    GroupRelation r = groupRelationService.findOne(rId);

                    log.debug(
                        "cacheName:{}, method:groupRelationMaybeCacheClearAllOrSpecialAdvice delagate to evictForGroupRelation",
                        cacheName);
                    if (evictForGroupRelation(r)) {
                        return;
                    }
                }

            }
        } else if ("appendRelation".equals(methodName)) {//��ӵ����
            Long[] userIds = (Long[]) arg;

            log.debug(
                "cacheName:{}, method:groupRelationMaybeCacheClearAllOrSpecialAdvice, evict user ids:{}",
                cacheName, Arrays.toString(userIds));
            evict(userIds);
        }
    }

    @Before(value = "userServicePointcut() && userCacheEvictSpecialPointcut(arg)", argNames = "jp,arg")
    public void userMaybeCacheClearSpecialAdvice(JoinPoint jp, Object arg) {
        String methodName = jp.getSignature().getName();
        if ("update".equals(methodName)) {
            User user = (User) arg;
            User dbUser = userService.findOne(user.getId());

            if (!(user.getOrganizationJobs().size() == dbUser.getOrganizationJobs().size() && dbUser
                .getOrganizationJobs().containsAll(user.getOrganizationJobs()))) {

                log.debug(
                    "cacheName:{}, method:userMaybeCacheClearSpecialAdvice, evict user id:{}",
                    cacheName, user.getId());
                evict(user.getId());
            }

        } else if ("delete".equals(methodName)) {//ɾ�����
            if (arg instanceof Long) {
                Long userId = (Long) arg;

                log.debug(
                    "cacheName:{}, method:userMaybeCacheClearSpecialAdvice, evict user id:{}",
                    cacheName, userId);
                evict(userId);
            } else if (arg instanceof Long[]) {

                Long[] userIds = (Long[]) arg;

                log.debug(
                    "cacheName:{}, method:userMaybeCacheClearSpecialAdvice, evict user ids:{}",
                    cacheName, Arrays.toString(userIds));

                evict(userIds);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    ////�������
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void clear() {
        super.clear();
        resourceMenuCacheAspect.clear();//��Ȩ�޹��� ͬʱ����˵���
    }

    public void evict(Long[] userIds) {
        for (Long userId : userIds) {
            evict(userId);
        }
    }

    public void evict(Long userId) {
        evict(rolesKey(userId));
        evict(stringRolesKey(userId));
        evict(stringPermissionsKey(userId));

        resourceMenuCacheAspect.evict(userId);//��Ȩ�޹��� ͬʱ����˵���
    }

    private String rolesKey(Long userId) {
        return this.rolesKeyPrefix + userId;
    }

    private String stringRolesKey(Long userId) {
        return this.stringRolesKeyPrefix + userId;
    }

    private String stringPermissionsKey(Long userId) {
        return this.stringPermissionsKeyPrefix + userId;
    }

}
