package com.sishuok.es.sys.auth.web.controller;

import javax.validation.Valid;

import org.es.framework.common.Constants;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.web.bind.annotation.SearchableDefaults;
import org.es.framework.common.web.controller.BaseCRUDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.sys.auth.entity.Auth;
import com.sishuok.es.sys.auth.entity.AuthType;
import com.sishuok.es.sys.auth.service.AuthService;
import com.sishuok.es.sys.permission.service.RoleService;

@Controller
@RequestMapping(value = "/admin/sys/auth")
public class AuthController extends BaseCRUDController<Auth, Long> {

    @Autowired
    private RoleService roleService;

    public AuthController() {
        setListAlsoSetCommonData(true);
        setResourceIdentity("sys:auth");
    }

    private AuthService getAuthService() {
        return (AuthService) baseService;
    }

    @Override
    protected void setCommonData(Model model) {
        super.setCommonData(model);
        model.addAttribute("types", AuthType.values());

        Searchable searchable = Searchable.newSearchable();
        //        searchable.addSearchFilter("show", SearchOperator.eq, true);
        model.addAttribute("roles", roleService.findAllWithNoPageNoSort(searchable));
    }

    @SearchableDefaults(value = "type_eq=user")
    @Override
    public String list(Searchable searchable, Model model) {

        String typeName = String.valueOf(searchable.getValue("type_eq"));
        model.addAttribute("type", AuthType.valueOf(typeName));

        return super.list(searchable, model);
    }

    @Override
    @RequestMapping(value = "create/discarded", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        throw new RuntimeException("discard method");
    }

    @Override
    @RequestMapping(value = "create/discarded", method = RequestMethod.POST)
    public String create(Model model, @Valid @ModelAttribute("m") Auth m, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        throw new RuntimeException("discard method");
    }

    @RequestMapping(value = "{type}/create", method = RequestMethod.GET)
    public String showCreateFormWithType(@PathVariable("type") AuthType type, Model model) {
        Auth auth = new Auth();
        auth.setType(type);
        model.addAttribute("m", auth);
        return super.showCreateForm(model);
    }

    @RequestMapping(value = "{type}/create", method = RequestMethod.POST)
    public String createWithType(Model model,
                                 @RequestParam(value = "userIds", required = false) Long[] userIds,
                                 @RequestParam(value = "groupIds", required = false) Long[] groupIds,
                                 @RequestParam(value = "organizationIds", required = false) Long[] organizationIds,
                                 @RequestParam(value = "jobIds", required = false) Long[][] jobIds,
                                 @Valid @ModelAttribute("m") Auth m, BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        this.permissionList.assertHasCreatePermission();

        if (hasError(m, result)) {
            return showCreateForm(model);
        }

        if (m.getType() == AuthType.user) {
            getAuthService().addUserAuth(userIds, m);
        } else if (m.getType() == AuthType.user_group || m.getType() == AuthType.organization_group) {
            getAuthService().addGroupAuth(groupIds, m);
        } else if (m.getType() == AuthType.organization_job) {
            getAuthService().addOrganizationJobAuth(organizationIds, jobIds, m);
        }
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "�����ɹ�");
        return redirectToUrl("/admin/sys/auth?search.type_eq=" + m.getType());
    }

}
