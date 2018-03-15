package com.sishuok.es.sys.resource.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.es.framework.common.entity.search.SearchOperator;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.plugin.serivce.BaseTreeableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sishuok.es.sys.auth.service.UserAuthService;
import com.sishuok.es.sys.resource.entity.Resource;
import com.sishuok.es.sys.resource.entity.tmp.Menu;
import com.sishuok.es.sys.user.entity.User;

@Service
public class ResourceService extends BaseTreeableService<Resource, Long> {

    @Autowired
    private UserAuthService userAuthService;

    /**
     * �õ���ʵ����Դ��ʶ  �� ����:����
     * @param resource
     * @return
     */
    public String findActualResourceIdentity(Resource resource) {

        if (resource == null) {
            return null;
        }

        StringBuilder s = new StringBuilder(resource.getIdentity());

        boolean hasResourceIdentity = !StringUtils.isEmpty(resource.getIdentity());

        Resource parent = findOne(resource.getParentId());
        while (parent != null) {
            if (!StringUtils.isEmpty(parent.getIdentity())) {
                s.insert(0, parent.getIdentity() + ":");
                hasResourceIdentity = true;
            }
            parent = findOne(parent.getParentId());
        }

        //����û�û������ ��Դ��ʶ  �Ҹ�Ҳû�У���ô��Ϊ��
        if (!hasResourceIdentity) {
            return "";
        }

        //������һ���ַ���: ��Ϊ����Ҫ������ɾ��֮
        int length = s.length();
        if (length > 0 && s.lastIndexOf(":") == length - 1) {
            s.deleteCharAt(length - 1);
        }

        //����ж��� ���ƴһ��*
        boolean hasChildren = false;
        for (Resource r : findAll()) {
            if (resource.getId().equals(r.getParentId())) {
                hasChildren = true;
                break;
            }
        }
        if (hasChildren) {
            s.append(":*");
        }

        return s.toString();
    }

    public List<Menu> findMenus(User user) {
        Searchable searchable = Searchable.newSearchable()
            .addSearchFilter("show", SearchOperator.eq, true)
            .addSort(new Sort(Sort.Direction.DESC, "parentId", "weight"));

        List<Resource> resources = findAllWithSort(searchable);

        Set<String> userPermissions = userAuthService.findStringPermissions(user);

        Iterator<Resource> iter = resources.iterator();
        while (iter.hasNext()) {
            if (!hasPermission(iter.next(), userPermissions)) {
                iter.remove();
            }
        }

        return convertToMenus(resources);
    }

    private boolean hasPermission(Resource resource, Set<String> userPermissions) {
        String actualResourceIdentity = findActualResourceIdentity(resource);
        if (StringUtils.isEmpty(actualResourceIdentity)) {
            return true;
        }

        for (String permission : userPermissions) {
            if (hasPermission(permission, actualResourceIdentity)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermission(String permission, String actualResourceIdentity) {

        //�õ�Ȩ���ַ����е� ��Դ���֣���a:b:create --->��Դ��a:b
        String permissionResourceIdentity = permission.substring(0, permission.lastIndexOf(":"));

        //���Ȩ���ַ����е���Դ �� ����ԴΪǰ׺ ����Ȩ�� ��a:b ����a:b��Ȩ��
        if (permissionResourceIdentity.startsWith(actualResourceIdentity)) {
            return true;
        }

        //ģʽƥ��
        WildcardPermission p1 = new WildcardPermission(permissionResourceIdentity);
        WildcardPermission p2 = new WildcardPermission(actualResourceIdentity);

        return p1.implies(p2) || p2.implies(p1);
    }

    @SuppressWarnings("unchecked")
    public static List<Menu> convertToMenus(List<Resource> resources) {

        if (resources.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        Menu root = convertToMenu(resources.remove(resources.size() - 1));

        recursiveMenu(root, resources);
        List<Menu> menus = root.getChildren();
        removeNoLeafMenu(menus);

        return menus;
    }

    private static void removeNoLeafMenu(List<Menu> menus) {
        if (menus.size() == 0) {
            return;
        }
        for (int i = menus.size() - 1; i >= 0; i--) {
            Menu m = menus.get(i);
            removeNoLeafMenu(m.getChildren());
            if (!m.isHasChildren() && StringUtils.isEmpty(m.getUrl())) {
                menus.remove(i);
            }
        }
    }

    private static void recursiveMenu(Menu menu, List<Resource> resources) {
        for (int i = resources.size() - 1; i >= 0; i--) {
            Resource resource = resources.get(i);
            if (resource.getParentId().equals(menu.getId())) {
                menu.getChildren().add(convertToMenu(resource));
                resources.remove(i);
            }
        }

        for (Menu subMenu : menu.getChildren()) {
            recursiveMenu(subMenu, resources);
        }
    }

    private static Menu convertToMenu(Resource resource) {
        return new Menu(resource.getId(), resource.getName(), resource.getIcon(), resource.getUrl());
    }

}
