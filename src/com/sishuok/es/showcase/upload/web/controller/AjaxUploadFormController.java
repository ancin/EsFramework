package com.sishuok.es.showcase.upload.web.controller;

import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.es.framework.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.showcase.upload.entity.Upload;
import com.sishuok.es.showcase.upload.service.UploadService;

/**
 * 文件上传/下载
 *
 */
@Controller
@RequestMapping(value = "showcase/upload/ajax")
public class AjaxUploadFormController {

    @Autowired
    private UploadService uploadService;

    @RequiresPermissions("showcase:upload:create")
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        model.addAttribute(Constants.OP_NAME, "新增");
        if (!model.containsAttribute("upload")) {
            model.addAttribute("upload", new Upload());
        }
        return "showcase/upload/ajax/editForm";
    }

    @RequiresPermissions("showcase:upload:create")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@Valid Upload upload, RedirectAttributes redirectAttributes) {

        uploadService.save(upload);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "创建文件成功");
        return "redirect:/showcase/upload";
    }

}
