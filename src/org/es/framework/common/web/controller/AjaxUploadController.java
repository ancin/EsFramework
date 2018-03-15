package org.es.framework.common.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.es.framework.common.Constants;
import org.es.framework.common.utils.ImagesUtils;
import org.es.framework.common.utils.LogUtils;
import org.es.framework.common.utils.MessageUtils;
import org.es.framework.common.web.entity.AjaxUploadResponse;
import org.es.framework.common.web.upload.FileUploadUtils;
import org.es.framework.common.web.upload.exception.FileNameLengthLimitExceededException;
import org.es.framework.common.web.upload.exception.InvalidExtensionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/***
 * ajax文件上传/下载
 * 
 * @author kejun.song
 * @version $Id: AjaxUploadController.java, v 0.1 2014年11月19日 上午11:23:13 kejun.song Exp $
 */
@Controller
public class AjaxUploadController {

    //最大上传大小 字节为单位
    private final long     maxSize          = FileUploadUtils.DEFAULT_MAX_SIZE;
    //允许的文件内容类型
    private final String[] allowedExtension = FileUploadUtils.DEFAULT_ALLOWED_EXTENSION;
    //文件上传下载的父目录
    private final String   baseDir          = FileUploadUtils.getDefaultBaseDir();

    /**
     * @param request
     * @param files
     * @return
     */
    @RequestMapping(value = "ajaxUpload", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUploadResponse ajaxUpload(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestParam(value = "files[]", required = false) MultipartFile[] files) {

        //The file upload plugin makes use of an Iframe Transport module for browsers like Microsoft Internet Explorer and Opera, which do not yet support XMLHTTPRequest file uploads.
        response.setContentType("text/plain");

        AjaxUploadResponse ajaxUploadResponse = new AjaxUploadResponse();

        if (ArrayUtils.isEmpty(files)) {
            return ajaxUploadResponse;
        }

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            long size = file.getSize();

            try {
                String url = FileUploadUtils.upload(request, baseDir, file, allowedExtension,
                    maxSize, true);
                String deleteURL = "/ajaxUpload/delete?filename="
                                   + URLEncoder.encode(url, Constants.ENCODING);
                if (ImagesUtils.isImage(filename)) {
                    ajaxUploadResponse.add(filename, size, url, url, deleteURL);
                } else {
                    ajaxUploadResponse.add(filename, size, url, deleteURL);
                }
                continue;
            } catch (IOException e) {
                LogUtils.logError("file upload error", e);
                ajaxUploadResponse.add(filename, size, MessageUtils.message("upload.server.error"));
                continue;
            } catch (InvalidExtensionException e) {
                ajaxUploadResponse.add(filename, size,
                    MessageUtils.message("upload.not.allow.extension"));
                continue;
            } catch (FileUploadBase.FileSizeLimitExceededException e) {
                ajaxUploadResponse.add(filename, size,
                    MessageUtils.message("upload.exceed.maxSize"));
                continue;
            } catch (FileNameLengthLimitExceededException e) {
                ajaxUploadResponse.add(filename, size,
                    MessageUtils.message("upload.filename.exceed.length"));
                continue;
            }
        }
        return ajaxUploadResponse;
    }

    @RequestMapping(value = "ajaxUpload/delete", method = RequestMethod.POST)
    @ResponseBody
    public String ajaxUploadDelete(HttpServletRequest request,
                                   @RequestParam(value = "filename") String filename)
                                                                                     throws Exception {

        if (StringUtils.isEmpty(filename) || filename.contains("\\.\\.")) {
            return "";
        }
        filename = URLDecoder.decode(filename, Constants.ENCODING);

        String filePath = FileUploadUtils.extractUploadDir(request) + "/" + filename;

        File file = new File(filePath);
        file.deleteOnExit();

        return "";
    }
}
