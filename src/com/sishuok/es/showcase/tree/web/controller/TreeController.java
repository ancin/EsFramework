package com.sishuok.es.showcase.tree.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.es.framework.common.Constants;
import org.es.framework.common.plugin.web.controller.BaseTreeableController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.showcase.tree.entity.Tree;

@Controller
@RequestMapping(value = "/showcase/tree")
public class TreeController extends BaseTreeableController<Tree, Long> {

    public TreeController() {
        setResourceIdentity("showcase:tree");
    }

    @RequestMapping(value = "/changeStatus/{newStatus}")
    public String changeStatus(HttpServletRequest request,
                               @PathVariable("newStatus") Boolean newStatus,
                               @RequestParam("ids") Long[] ids,
                               RedirectAttributes redirectAttributes) {

        this.permissionList.assertHasUpdatePermission();

        for (Long id : ids) {
            Tree tree = baseService.findOne(id);
            tree.setShow(newStatus);
            baseService.update(tree);
        }
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "²Ù×÷³É¹¦£¡");

        return "redirect:" + request.getAttribute(Constants.BACK_URL);
    }

}
