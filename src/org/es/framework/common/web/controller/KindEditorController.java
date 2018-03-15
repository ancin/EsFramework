package org.es.framework.common.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.FilenameUtils;
import org.es.framework.common.utils.LogUtils;
import org.es.framework.common.web.upload.FileUploadUtils;
import org.es.framework.common.web.upload.exception.FileNameLengthLimitExceededException;
import org.es.framework.common.web.upload.exception.InvalidExtensionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping(value = "/kindeditor")
public class KindEditorController {

    //ͼƬmime����
    private static final String[] IMAGE_EXTENSION      = FileUploadUtils.IMAGE_EXTENSION;

    //����mime����
    private static final String[] ATTACHMENT_EXTENSION = FileUploadUtils.DEFAULT_ALLOWED_EXTENSION;

    //flash mime����
    private static final String[] FLASH_EXTENSION      = FileUploadUtils.FLASH_EXTENSION;

    //swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb
    private static final String[] MEDIA_EXTENSION      = FileUploadUtils.MEDIA_EXTENSION;

    //����ϴ���С �ֽ�Ϊ��λ
    private final long            maxSize              = FileUploadUtils.DEFAULT_MAX_SIZE;
    //�ļ��ϴ����صĸ�Ŀ¼
    private final String          baseDir              = FileUploadUtils.getDefaultBaseDir();

    @Autowired
    private MessageSource         messageSource;

    /**
     * ���������
     * dir ��ʾ����  file image flash media
     * <p/>
     * ���ص����ݸ�ʽ
     * ����ʱ
     * {
     * error : 1
     * message : ����ʱ����Ϣ
     * }
     * <p/>
     * ��ȷʱ
     * {
     * error:0
     * url:�ϴ���ĵ�ַ
     * title:����
     * }
     *
     * @param response
     * @param request
     * @param dir
     * @param file
     * @return
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(HttpServletResponse response, HttpServletRequest request,
                         @RequestParam(value = "dir", required = false) String dir,
                         @RequestParam(value = "imgFile", required = false) MultipartFile file) {

        response.setContentType("text/html; charset=UTF-8");

        String[] allowedExtension = extractAllowedExtension(dir);

        try {
            String url = FileUploadUtils.upload(request, baseDir, file, allowedExtension, maxSize,
                true);
            return successResponse(request, file.getOriginalFilename(), url);

        } catch (IOException e) {
            LogUtils.logError("file upload error", e);
            return errorResponse("upload.server.error");
        } catch (InvalidExtensionException.InvalidImageExtensionException e) {
            return errorResponse("upload.not.allow.image.extension");
        } catch (InvalidExtensionException.InvalidFlashExtensionException e) {
            return errorResponse("upload.not.allow.flash.extension");
        } catch (InvalidExtensionException.InvalidMediaExtensionException e) {
            return errorResponse("upload.not.allow.media.extension");
        } catch (InvalidExtensionException e) {
            return errorResponse("upload.not.allow.extension");
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            return errorResponse("upload.exceed.maxSize");
        } catch (FileNameLengthLimitExceededException e) {
            return errorResponse("upload.filename.exceed.length");
        }

    }

    /**
     * ���������
     * path ��ǰ���ʵ�Ŀ¼�����·���� ��..����Ҫ�ų���
     * order ����  NAME SIZE TYPE
     * dir �ļ����� file image flash media
     * <p/>
     * <p/>
     * ���ص����ݸ�ʽ
     * ����ʱ��ǰ̨�޴�����ʾ��todo �����Լ������� ��ʾ������Ϣ����ͣ���ڵ�ǰҳ�棩
     * "�ַ���"
     * <p/>
     * ��ȷʱ
     * {"current_url":��ǰ��ַ�����ԣ�,
     * "current_dir_path":��ǰĿ¼����ԣ�,
     * "moveup_dir_path":�ϼ�Ŀ¼�����Ը��ݵ�ǰĿ¼�������,
     * "file_list":[//�ļ��б�
     * �ļ���                  �ļ���С     �ļ�����      �Ƿ�����ļ�    �Ƿ���Ŀ¼   �Ƿ�����Ƭ        ʱ��
     * {"filename":"My Pictures","filesize":0,"filetype":"","has_file":true,"is_dir":true,"is_photo":false,"datetime":"2013-03-09 11:41:17"}
     * ],
     * "total_count":�ļ���Ŀ¼����
     * }
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "filemanager", method = RequestMethod.GET)
    @ResponseBody
    public Object fileManager(HttpServletRequest request,
                              @RequestParam(value = "order", required = false, defaultValue = "NAME") String order,
                              @RequestParam(value = "dir", required = false, defaultValue = "file") String dir,
                              @RequestParam(value = "path", required = false, defaultValue = "") String currentDirPath) {

        //��Ŀ¼·��
        String rootPath = FileUploadUtils.extractUploadDir(request) + "/" + baseDir;
        String rootUrl = request.getContextPath() + "/" + baseDir;

        //��һ��Ŀ¼
        String moveupDirPath = "";
        if (!"".equals(currentDirPath)) {
            String str = currentDirPath.substring(0, currentDirPath.length() - 1);
            moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1)
                : "";
        }

        //������ʹ��..�ƶ�����һ��Ŀ¼
        if (currentDirPath.indexOf("..") >= 0) {
            return "���ʾܾ���Ŀ¼�в��ܳ���..";
        }
        //���һ���ַ�����/
        if (!"".equals(currentDirPath) && !currentDirPath.endsWith("/")) {
            return "�������Ϸ���Ŀ¼��Ҫ�� / ��β";
        }
        //Ŀ¼�����ڻ���Ŀ¼
        File currentPathFile = new File(rootPath + "/" + currentDirPath);
        if (!currentPathFile.exists() || !currentPathFile.isDirectory()) {
            return "Ŀ¼������";
        }

        //����Ŀ¼ȡ���ļ���Ϣ
        List<Map<String, Object>> fileMetaInfoList = Lists.newArrayList();

        List<String> allowedExtension = Arrays.asList(extractAllowedExtension(dir));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (currentPathFile.listFiles() != null) {
            for (File file : currentPathFile.listFiles()) {

                Map<String, Object> fileMetaInfo = Maps.newHashMap();
                String fileName = file.getName();

                if (file.isDirectory()) {
                    fileMetaInfo.put("is_dir", true);
                    fileMetaInfo.put("has_file", (file.listFiles() != null));
                    fileMetaInfo.put("filesize", 0L);
                    fileMetaInfo.put("is_photo", false);
                    fileMetaInfo.put("filetype", "");
                } else if (file.isFile()) {
                    String fileExt = FilenameUtils.getExtension(fileName);
                    if (!allowedExtension.contains(FilenameUtils.getExtension(fileName))) {
                        continue;
                    }
                    fileMetaInfo.put("is_dir", false);
                    fileMetaInfo.put("has_file", false);
                    fileMetaInfo.put("filesize", file.length());
                    fileMetaInfo.put("is_photo",
                        Arrays.<String> asList(FileUploadUtils.IMAGE_EXTENSION).contains(fileExt));
                    fileMetaInfo.put("filetype", fileExt);
                }
                fileMetaInfo.put("filename", fileName);
                fileMetaInfo.put("datetime", df.format(file.lastModified()));

                fileMetaInfoList.add(fileMetaInfo);
            }
        }

        if ("size".equalsIgnoreCase(order)) {
            Collections.sort(fileMetaInfoList, new SizeComparator());
        } else if ("type".equalsIgnoreCase(order)) {
            Collections.sort(fileMetaInfoList, new TypeComparator());
        } else {
            Collections.sort(fileMetaInfoList, new NameComparator());
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("moveup_dir_path", moveupDirPath);
        result.put("current_dir_path", currentDirPath);
        result.put("current_url", rootUrl + "/" + currentDirPath);
        result.put("total_count", fileMetaInfoList.size());
        result.put("file_list", fileMetaInfoList);

        return result;
    }

    private String successResponse(HttpServletRequest request, String filename, String url) {
        return "{\"error\":0, \"url\":\"" + request.getContextPath() + "/" + url
               + "\", \"title\":\"" + filename + "    \"}";
    }

    private String errorResponse(String errorCode) {
        String message = messageSource.getMessage(errorCode, null, null);
        if (message.contains("<br/>")) {
            message = message.replace("<br/>", "\\n");
        }
        return "{\"error\":1, \"message\":\"" + message + "\"}";
    }

    private String[] extractAllowedExtension(String dir) {
        if ("image".equals(dir)) {
            return IMAGE_EXTENSION;
        } else if ("flash".equals(dir)) {
            return FLASH_EXTENSION;
        } else if ("media".equals(dir)) {
            return MEDIA_EXTENSION;
        } else {
            return ATTACHMENT_EXTENSION;
        }

    }

    @SuppressWarnings("rawtypes")
    public class NameComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            Map mapA = (Map) a;
            Map mapB = (Map) b;
            if (((Boolean) mapA.get("is_dir")) && !((Boolean) mapB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) mapA.get("is_dir")) && ((Boolean) mapB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) mapA.get("filename")).compareTo((String) mapB.get("filename"));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public class SizeComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            Map mapA = (Map) a;
            Map mapB = (Map) b;
            if (((Boolean) mapA.get("is_dir")) && !((Boolean) mapB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) mapA.get("is_dir")) && ((Boolean) mapB.get("is_dir"))) {
                return 1;
            } else {
                if (((Long) mapA.get("filesize")) > ((Long) mapB.get("filesize"))) {
                    return 1;
                } else if (((Long) mapA.get("filesize")) < ((Long) mapB.get("filesize"))) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public class TypeComparator implements Comparator {

        @Override
        public int compare(Object a, Object b) {
            Map mapA = (Map) a;
            Map mapB = (Map) b;
            if (((Boolean) mapA.get("is_dir")) && !((Boolean) mapB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) mapA.get("is_dir")) && ((Boolean) mapB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) mapA.get("filetype")).compareTo((String) mapB.get("filetype"));
            }
        }
    }

}
