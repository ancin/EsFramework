/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.showcase.upload.web.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.es.framework.common.Constants;
import org.es.framework.common.web.upload.FileUploadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ajax�����ļ��ϴ�/����
 * <p>User: Zhang Kaitao
 * <p>Date: 13-2-11 ����8:46
 * <p>Version: 1.0
 */
@Controller
public class BatchAjaxUploadController {

    //����ϴ���С �ֽ�Ϊ��λ
    private final long     maxSize          = FileUploadUtils.DEFAULT_MAX_SIZE;
    //������ļ���������
    private final String[] allowedExtension = FileUploadUtils.DEFAULT_ALLOWED_EXTENSION;
    //�ļ��ϴ����صĸ�Ŀ¼
    private final String   baseDir          = FileUploadUtils.getDefaultBaseDir();

    @RequiresPermissions("showcase:upload:create")
    @RequestMapping(value = "ajaxUpload", method = RequestMethod.GET)
    public String showAjaxUploadForm(Model model) {
        model.addAttribute(Constants.OP_NAME, "����");
        return "showcase/upload/ajax/uploadForm";
    }

}
