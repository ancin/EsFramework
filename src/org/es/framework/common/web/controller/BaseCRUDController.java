package org.es.framework.common.web.controller;

import java.io.Serializable;

import javax.validation.Valid;

import org.es.framework.common.Constants;
import org.es.framework.common.entity.AbstractEntity;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.service.BaseService;
import org.es.framework.common.web.bind.annotation.PageableDefaults;
import org.es.framework.common.web.controller.permission.PermissionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/***
 * ����CRUD ������
 * 
 * @author kejun.song
 * @version $Id: BaseCRUDController.java, v 0.1 2014��11��19�� ����11:24:39 kejun.song Exp $
 */
@SuppressWarnings("rawtypes")
public abstract class BaseCRUDController<M extends AbstractEntity, ID extends Serializable>
                                                                                            extends
                                                                                            BaseController<M, ID> {

    protected BaseService<M, ID> baseService;

    private boolean              listAlsoSetCommonData = false;

    protected PermissionList     permissionList        = null;

    /**
     * ���û���service
     *
     * @param baseService
     */
    @Autowired
    public void setBaseService(BaseService<M, ID> baseService) {
        this.baseService = baseService;
    }

    /**
     * �б�Ҳ����common data
     */
    public void setListAlsoSetCommonData(boolean listAlsoSetCommonData) {
        this.listAlsoSetCommonData = listAlsoSetCommonData;
    }

    /**
     * Ȩ��ǰ׺����sys:user
     * �����ɵ�����Ȩ��Ϊ sys:user:create
     */
    public void setResourceIdentity(String resourceIdentity) {
        if (!StringUtils.isEmpty(resourceIdentity)) {
            permissionList = PermissionList.newPermissionList(resourceIdentity);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public String list(Searchable searchable, Model model) {

        if (permissionList != null) {
            this.permissionList.assertHasViewPermission();
        }

        model.addAttribute("page", baseService.findAll(searchable));
        if (listAlsoSetCommonData) {
            setCommonData(model);
        }
        return viewName("list");
    }

    /**
     * �����ر������
     *
     * @param searchable
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, headers = "table=true")
    @PageableDefaults(sort = "id=desc")
    public String listTable(Searchable searchable, Model model) {
        list(searchable, model);
        return viewName("listTable");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable("id") M m) {

        if (permissionList != null) {
            this.permissionList.assertHasViewPermission();
        }

        setCommonData(model);
        model.addAttribute("m", m);
        model.addAttribute(Constants.OP_NAME, "�鿴");
        return viewName("editForm");
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {

        if (permissionList != null) {
            this.permissionList.assertHasCreatePermission();
        }

        setCommonData(model);
        model.addAttribute(Constants.OP_NAME, "����");
        if (!model.containsAttribute("m")) {
            model.addAttribute("m", newModel());
        }
        return viewName("editForm");
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, @Valid @ModelAttribute("m") M m, BindingResult result,
                         RedirectAttributes redirectAttributes) {

        if (permissionList != null) {
            this.permissionList.assertHasCreatePermission();
        }

        if (hasError(m, result)) {
            return showCreateForm(model);
        }
        baseService.save(m);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "�����ɹ�");
        return redirectToUrl(null);
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("id") M m, Model model) {

        if (permissionList != null) {
            this.permissionList.assertHasUpdatePermission();
        }

        setCommonData(model);
        model.addAttribute(Constants.OP_NAME, "�޸�");
        model.addAttribute("m", m);
        return viewName("editForm");
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
    public String update(Model model,
                         @Valid @ModelAttribute("m") M m,
                         BindingResult result,
                         @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
                         RedirectAttributes redirectAttributes) {

        if (permissionList != null) {
            this.permissionList.assertHasUpdatePermission();
        }

        if (hasError(m, result)) {
            return showUpdateForm(m, model);
        }
        baseService.update(m);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "�޸ĳɹ�");
        return redirectToUrl(backURL);
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
    public String showDeleteForm(@PathVariable("id") M m, Model model) {

        if (permissionList != null) {
            this.permissionList.assertHasDeletePermission();
        }

        setCommonData(model);
        model.addAttribute(Constants.OP_NAME, "ɾ��");
        model.addAttribute("m", m);
        return viewName("editForm");
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    public String delete(@PathVariable("id") M m,
                         @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
                         RedirectAttributes redirectAttributes) {

        if (permissionList != null) {
            this.permissionList.assertHasDeletePermission();
        }

        baseService.delete(m);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "ɾ���ɹ�");
        return redirectToUrl(backURL);
    }

    @RequestMapping(value = "batch/delete", method = { RequestMethod.GET, RequestMethod.POST })
    public String deleteInBatch(@RequestParam(value = "ids", required = false) ID[] ids,
                                @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
                                RedirectAttributes redirectAttributes) {

        if (permissionList != null) {
            this.permissionList.assertHasDeletePermission();
        }

        baseService.delete(ids);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "ɾ���ɹ�");
        return redirectToUrl(backURL);
    }

}
