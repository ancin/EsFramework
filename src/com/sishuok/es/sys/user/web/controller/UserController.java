package com.sishuok.es.sys.user.web.controller;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.es.framework.common.Constants;
import org.es.framework.common.entity.enums.BooleanEnum;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.web.bind.annotation.PageableDefaults;
import org.es.framework.common.web.bind.annotation.SearchableDefaults;
import org.es.framework.common.web.controller.BaseCRUDController;
import org.es.framework.common.web.validate.ValidateResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.sys.organization.entity.Job;
import com.sishuok.es.sys.organization.entity.Organization;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.entity.UserOrganizationJob;
import com.sishuok.es.sys.user.entity.UserStatus;
import com.sishuok.es.sys.user.service.UserService;
import com.sishuok.es.sys.user.web.bind.annotation.CurrentUser;

@Controller("adminUserController")
@RequestMapping(value = "/admin/sys/user")
public class UserController extends BaseCRUDController<User, Long> {

    private UserService getUserService() {
        return (UserService) baseService;
    }

    public UserController() {
        setResourceIdentity("sys:user");
    }

    @Override
    protected void setCommonData(Model model) {
        model.addAttribute("statusList", UserStatus.values());
        model.addAttribute("booleanList", BooleanEnum.values());
    }

    @RequestMapping(value = "main", method = RequestMethod.GET)
    public String main(Model model) {
        return viewName("main");
    }

    @RequestMapping(value = "tree", method = RequestMethod.GET)
    public String tree(Model model) {
        return viewName("tree");
    }

    @Override
    @RequestMapping(value = "list/discard", method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public String list(Searchable searchable, Model model) {
        throw new RuntimeException("discarded method");
    }

    @RequestMapping(value = { "" }, method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    @SearchableDefaults(value = "deleted_eq=0")
    public String listAll(Searchable searchable, Model model) {
        return list(null, null, searchable, model);
    }

    @RequestMapping(value = { "{organization}/{job}" }, method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    @SearchableDefaults(value = "deleted_eq=0")
    public String list(@PathVariable("organization") Organization organization,
                       @PathVariable("job") Job job, Searchable searchable, Model model) {

        setCommonData(model);

        if (organization != null && !organization.isRoot()) {
            searchable.addSearchParam("organizationId", organization.getId());
        }
        if (job != null && !job.isRoot()) {
            searchable.addSearchParam("jobId", job.getId());
        }

        return super.list(searchable, model);
    }

    @RequestMapping(value = "create/discard", method = RequestMethod.POST)
    @Override
    public String create(Model model, @Valid @ModelAttribute("m") User m, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        throw new RuntimeException("discarded method");
    }

    @RequestMapping(value = "{id}/update/discard", method = RequestMethod.POST)
    @Override
    public String update(Model model,
                         @Valid @ModelAttribute("m") User m,
                         BindingResult result,
                         @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
                         RedirectAttributes redirectAttributes) {
        throw new RuntimeException("discarded method");
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createWithOrganization(Model model,
                                         @Valid @ModelAttribute("m") User m,
                                         BindingResult result,
                                         @RequestParam(value = "organizationId", required = false) Long[] organizationIds,
                                         @RequestParam(value = "jobId", required = false) Long[][] jobIds,
                                         RedirectAttributes redirectAttributes) {

        fillUserOrganization(m, organizationIds, jobIds);

        return super.create(model, m, result, redirectAttributes);
    }

    private void fillUserOrganization(User m, Long[] organizationIds, Long[][] jobIds) {
        if (ArrayUtils.isEmpty(organizationIds)) {
            return;
        }
        for (int i = 0, l = organizationIds.length; i < l; i++) {

            //������/�޸�һ�� spring���Զ�split����������--->������
            if (l == 1) {
                for (int j = 0, l2 = jobIds.length; j < l2; j++) {
                    UserOrganizationJob userOrganizationJob = new UserOrganizationJob();
                    userOrganizationJob.setOrganizationId(organizationIds[i]);
                    userOrganizationJob.setJobId(jobIds[j][0]);
                    m.addOrganizationJob(userOrganizationJob);
                }
            } else {
                Long[] jobId = jobIds[i];
                for (int j = 0, l2 = jobId.length; j < l2; j++) {
                    UserOrganizationJob userOrganizationJob = new UserOrganizationJob();
                    userOrganizationJob.setOrganizationId(organizationIds[i]);
                    userOrganizationJob.setJobId(jobId[j]);
                    m.addOrganizationJob(userOrganizationJob);
                }
            }

        }
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
    public String updateWithOrganization(Model model,
                                         @Valid @ModelAttribute("m") User m,
                                         BindingResult result,
                                         @RequestParam(value = "organizationId", required = false) Long[] organizationIds,
                                         @RequestParam(value = "jobId", required = false) Long[][] jobIds,
                                         @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
                                         RedirectAttributes redirectAttributes) {

        fillUserOrganization(m, organizationIds, jobIds);

        return super.update(model, m, result, backURL, redirectAttributes);
    }

    @RequestMapping(value = "changePassword")
    public String changePassword(HttpServletRequest request, @RequestParam("ids") Long[] ids,
                                 @RequestParam("newPassword") String newPassword,
                                 @CurrentUser User opUser, RedirectAttributes redirectAttributes) {

        getUserService().changePassword(opUser, ids, newPassword);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "���ܳɹ���");

        return redirectToUrl((String) request.getAttribute(Constants.BACK_URL));
    }

    @RequestMapping(value = "changeStatus/{newStatus}")
    public String changeStatus(HttpServletRequest request, @RequestParam("ids") Long[] ids,
                               @PathVariable("newStatus") UserStatus newStatus,
                               @RequestParam("reason") String reason, @CurrentUser User opUser,
                               RedirectAttributes redirectAttributes) {

        getUserService().changeStatus(opUser, ids, newStatus, reason);

        if (newStatus == UserStatus.normal) {
            redirectAttributes.addFlashAttribute(Constants.MESSAGE, "���ɹ���");
        } else if (newStatus == UserStatus.blocked) {
            redirectAttributes.addFlashAttribute(Constants.MESSAGE, "����ɹ���");
        }

        return redirectToUrl((String) request.getAttribute(Constants.BACK_URL));
    }

    @RequestMapping(value = "recycle")
    public String recycle(HttpServletRequest request, @RequestParam("ids") Long[] ids,
                          RedirectAttributes redirectAttributes) {
        for (Long id : ids) {
            User user = getUserService().findOne(id);
            user.setDeleted(Boolean.FALSE);
            getUserService().update(user);
        }
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "��ԭ�ɹ���");
        return redirectToUrl((String) request.getAttribute(Constants.BACK_URL));
    }

    @RequestMapping("{user}/organizations")
    public String permissions(@PathVariable("user") User user) {
        return viewName("organizationsTable");
    }

    @RequestMapping("ajax/autocomplete")
    @PageableDefaults(value = 30)
    @ResponseBody
    public Set<Map<String, Object>> autocomplete(Searchable searchable,
                                                 @RequestParam("term") String term) {

        return getUserService().findIdAndNames(searchable, term);
    }

    /**
     * ��֤���ظ�ʽ
     * ������[fieldId, 1|0, msg]
     * �����[[fieldId, 1|0, msg],[fieldId, 1|0, msg]]
     *
     * @param fieldId
     * @param fieldValue
     * @return
     */
    @RequestMapping(value = "validate", method = RequestMethod.GET)
    @ResponseBody
    public Object validate(@RequestParam("fieldId") String fieldId,
                           @RequestParam("fieldValue") String fieldValue,
                           @RequestParam(value = "id", required = false) Long id) {

        ValidateResponse response = ValidateResponse.newInstance();

        if ("username".equals(fieldId)) {
            User user = getUserService().findByUsername(fieldValue);
            if (user == null || (user.getId().equals(id) && user.getUsername().equals(fieldValue))) {
                //���msg ��Ϊ�� ��������ʾ��
                response.validateSuccess(fieldId, "");
            } else {
                response.validateFail(fieldId, "�û����ѱ�������ʹ��");
            }
        }

        if ("email".equals(fieldId)) {
            User user = getUserService().findByEmail(fieldValue);
            if (user == null || (user.getId().equals(id) && user.getEmail().equals(fieldValue))) {
                //���msg ��Ϊ�� ��������ʾ��
                response.validateSuccess(fieldId, "");
            } else {
                response.validateFail(fieldId, "�����ѱ�������ʹ��");
            }
        }

        if ("mobilePhoneNumber".equals(fieldId)) {
            User user = getUserService().findByMobilePhoneNumber(fieldValue);
            if (user == null
                || (user.getId().equals(id) && user.getMobilePhoneNumber().equals(fieldValue))) {
                //���msg ��Ϊ�� ��������ʾ��
                response.validateSuccess(fieldId, "");
            } else {
                response.validateFail(fieldId, "�ֻ����ѱ�������ʹ��");
            }
        }

        return response.result();
    }

}
