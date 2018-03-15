package org.es.framework.common.web.controller;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.common.Constants;
import org.es.framework.common.web.upload.FileUploadUtils;
import org.es.framework.common.web.utils.DownloadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DownloadController {

    /**
     * ��������ǰ����ʾ��ǰ׺
     */
    private final String prefixFilename = "[es���ּ�]";

    @RequestMapping(value = "/download")
    public String download(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(value = "filename") String filename) throws Exception {

        filename = filename.replace("/", "\\");

        if (StringUtils.isEmpty(filename) || filename.contains("\\.\\.")) {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("�����ص��ļ������ڣ�");
            return null;
        }
        filename = URLDecoder.decode(filename, Constants.ENCODING);

        String filePath = FileUploadUtils.extractUploadDir(request) + "/" + filename;

        DownloadUtils.download(request, response, filePath, prefixFilename + filename);

        return null;
    }

}
