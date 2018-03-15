package com.sishuok.es.showcase.parentchild.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.es.framework.common.Constants;
import org.es.framework.common.entity.enums.BooleanEnum;
import org.es.framework.common.entity.search.SearchOperator;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.web.bind.annotation.FormModel;
import org.es.framework.common.web.bind.annotation.PageableDefaults;
import org.es.framework.common.web.controller.BaseCRUDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.showcase.parentchild.entity.Child;
import com.sishuok.es.showcase.parentchild.entity.Parent;
import com.sishuok.es.showcase.parentchild.entity.ParentChildType;
import com.sishuok.es.showcase.parentchild.service.ChildService;
import com.sishuok.es.showcase.parentchild.service.ParentService;

@Controller
@RequestMapping(value = "/showcase/parentchild/parent")
public class ParentController extends BaseCRUDController<Parent, Long> {

    private ParentService getParentService() {
        return (ParentService) baseService;
    }

    @Autowired
    private ChildService childService;

    protected ParentController() {
        setListAlsoSetCommonData(true);
        setResourceIdentity("showcase:parentchild");
    }

    @Override
    protected void setCommonData(Model model) {
        model.addAttribute("booleanList", BooleanEnum.values());
        model.addAttribute("typeList", ParentChildType.values());
    }

    @RequestMapping(value = "create/discard", method = RequestMethod.POST)
    @Override
    public String create(Model model, @Valid @ModelAttribute("m") Parent parent,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        throw new RuntimeException("discarded method");
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, @Valid @ModelAttribute("parent") Parent parent,
                         BindingResult result, @FormModel("childList") List<Child> childList,
                         RedirectAttributes redirectAttributes) {

        if (hasError(parent, result)) {
            return showCreateForm(model);
        }
        getParentService().save(parent, childList);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "创建成功");
        return redirectToUrl(null);
    }

    @Override
    @RequestMapping(value = "{id}/update", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("id") Parent parent, Model model) {
        model.addAttribute("childList", childService.findByParent(parent, null).getContent());
        return super.showUpdateForm(parent, model);
    }

    @RequestMapping(value = "{id}/update/discard", method = RequestMethod.POST)
    @Override
    public String update(Model model, @Valid @ModelAttribute("m") Parent parent,
                         BindingResult result,
                         @RequestParam(value = "BackURL", required = false) String backURL,
                         RedirectAttributes redirectAttributes) {
        throw new RuntimeException("discarded method");
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
    public String update(Model model, @Valid @ModelAttribute("parent") Parent parent,
                         BindingResult result, @FormModel("childList") List<Child> childList,
                         @RequestParam(value = "BackURL", required = false) String backURL,
                         RedirectAttributes redirectAttributes) {

        if (hasError(parent, result)) {
            return showUpdateForm(parent, model);
        }
        getParentService().update(parent, childList);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "修改成功");
        return redirectToUrl(backURL);
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
    @Override
    public String showDeleteForm(@PathVariable("id") Parent parent, Model model) {
        model.addAttribute("childList", childService.findByParent(parent, null).getContent());
        return super.showDeleteForm(parent, model);
    }

    /**
     * 验证失败返回true
     *
     * @param parent
     * @param result
     * @return
     */
    @Override
    protected boolean hasError(Parent parent, BindingResult result) {
        Assert.notNull(parent);

        //全局错误 前台使用<es:showGlobalError commandName="showcase/parent"/> 显示
        if (parent.getName().contains("admin")) {
            result.reject("name.must.not.admin");
        }

        return result.hasErrors();
    }

    //////////////////////////////////child////////////////////////////////////

    @RequestMapping(value = "{parent}/child", method = RequestMethod.GET)
    @PageableDefaults(value = Integer.MAX_VALUE, sort = "id=desc")
    public String listChild(Model model, @PathVariable("parent") Long parentId,
                            Searchable searchable) {

        this.permissionList.assertHasViewPermission();

        searchable.addSearchFilter("parent.id", SearchOperator.eq, parentId);

        model.addAttribute("page", childService.findAll(searchable));

        return "showcase/parentchild/child/list";
    }

    @RequestMapping(value = "child/create", method = RequestMethod.GET)
    public String showChildCreateForm(Model model) {

        this.permissionList.assertHasEditPermission();

        setCommonData(model);
        model.addAttribute(Constants.OP_NAME, "新增");
        if (!model.containsAttribute("child")) {
            model.addAttribute("child", new Child());
        }
        return "showcase/parentchild/child/editForm";
    }

    @RequestMapping(value = "child/{id}/update", method = RequestMethod.GET)
    public String showChildUpdateForm(Model model,
                                      @PathVariable("id") Child child,
                                      @RequestParam(value = "copy", defaultValue = "false") boolean isCopy) {

        this.permissionList.assertHasEditPermission();

        setCommonData(model);
        model.addAttribute(Constants.OP_NAME, isCopy ? "复制" : "修改");
        if (!model.containsAttribute("child")) {
            if (child == null) {
                child = new Child();
            }
            if (isCopy) {
                child.setId(null);
            }
            model.addAttribute("child", child);
        }
        return "showcase/parentchild/child/editForm";
    }

    @RequestMapping(value = "child/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Child deleteChild(@PathVariable("id") Child child) {

        this.permissionList.assertHasEditPermission();

        childService.delete(child);
        return child;
    }

    @RequestMapping(value = "child/batch/delete")
    @ResponseBody
    public Object deleteChildInBatch(@RequestParam(value = "ids", required = false) Long[] ids) {

        this.permissionList.assertHasEditPermission();

        childService.delete(ids);
        return ids;
    }

}
