package com.sishuok.es.showcase.upload.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.es.framework.common.web.controller.BaseCRUDController;
import org.es.framework.common.web.upload.FileUploadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.showcase.upload.entity.Upload;

/**
 * �ļ��ϴ�/����
 * 
 */
@Controller
@RequestMapping(value = "showcase/upload")
public class UploadController extends BaseCRUDController<Upload, Long> {

    public UploadController() {
        setResourceIdentity("showcase:upload");
    }

    //������Ĭ�ϵ�create����Ϊ�±ߵ�create���ж������������޷�����Ĭ�ϵ�create�����Ϊ��ʹ�ø�url ���ǰѸ����url�ĵ�
    @RequestMapping(value = "create/discard", method = RequestMethod.POST)
    @Override
    public String create(Model model, @Valid @ModelAttribute("m") Upload upload,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        throw new RuntimeException("discarded method");
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, HttpServletRequest request,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         @Valid @ModelAttribute("m") Upload upload, BindingResult result,
                         RedirectAttributes redirectAttributes) {

        if (!file.isEmpty()) {
            upload.setSrc(FileUploadUtils.upload(request, file, result));
        }
        return super.create(model, upload, result, redirectAttributes);
    }

    @RequestMapping(value = "{id}/update/discard", method = RequestMethod.POST)
    @Override
    public String update(Model model, @Valid @ModelAttribute("m") Upload upload,
                         BindingResult result,
                         @RequestParam(value = "BackURL", required = false) String backURL,
                         RedirectAttributes redirectAttributes) {
        throw new RuntimeException("discarded method");
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
    public String update(Model model, HttpServletRequest request,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         @Valid @ModelAttribute("m") Upload upload, BindingResult result,
                         @RequestParam(value = "BackURL") String backURL,
                         RedirectAttributes redirectAttributes) {

        if (!file.isEmpty()) {
            upload.setSrc(FileUploadUtils.upload(request, file, result));
        }
        return super.update(model, upload, result, backURL, redirectAttributes);
    }

}
