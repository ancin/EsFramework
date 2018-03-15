package com.sishuok.es.sys.permission.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.es.framework.common.Constants;
import org.es.framework.common.entity.enums.AvailableEnum;
import org.es.framework.common.web.controller.BaseCRUDController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sishuok.es.sys.permission.entity.Permission;

@Controller
@RequestMapping(value = "/admin/sys/permission/permission")
public class PermissionController extends BaseCRUDController<Permission, Long> {

    public PermissionController() {
        setResourceIdentity("sys:permission");
    }

    @Override
    protected void setCommonData(Model model) {
        super.setCommonData(model);
        model.addAttribute("availableList", AvailableEnum.values());
    }

    @RequestMapping(value = "/changeStatus/{newStatus}")
    public String changeStatus(HttpServletRequest request,
                               @PathVariable("newStatus") Boolean newStatus,
                               @RequestParam("ids") Long[] ids) {

        this.permissionList.assertHasUpdatePermission();

        for (Long id : ids) {
            Permission permission = baseService.findOne(id);
            permission.setShow(newStatus);
            baseService.update(permission);
        }

        return "redirect:" + request.getAttribute(Constants.BACK_URL);
    }

}
