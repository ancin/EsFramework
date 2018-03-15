package org.es.framework.common.web.upload;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.es.framework.common.utils.LogUtils;
import org.es.framework.common.utils.security.Coder;
import org.es.framework.common.web.upload.exception.FileNameLengthLimitExceededException;
import org.es.framework.common.web.upload.exception.InvalidExtensionException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * �ļ��ϴ�������
 * 
 */
public class FileUploadUtils {

    //Ĭ�ϴ�С 50M
    public static final long     DEFAULT_MAX_SIZE          = 52428800;

    //Ĭ���ϴ��ĵ�ַ
    private static String        defaultBaseDir            = "upload";

    //Ĭ�ϵ��ļ�����󳤶�
    public static final int      DEFAULT_FILE_NAME_LENGTH  = 200;

    public static final String[] IMAGE_EXTENSION           = { "bmp", "gif", "jpg", "jpeg", "png" };

    public static final String[] FLASH_EXTENSION           = { "swf", "flv" };

    public static final String[] MEDIA_EXTENSION           = { "swf", "flv", "mp3", "wav", "wma",
            "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb" };

    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
                                                           //ͼƬ
            "bmp", "gif", "jpg", "jpeg", "png",
            //word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            //ѹ���ļ�
            "rar", "zip", "gz", "bz2",
            //pdf
            "pdf"                                         };

    private static int           counter                   = 0;

    public static void setDefaultBaseDir(String defaultBaseDir) {
        FileUploadUtils.defaultBaseDir = defaultBaseDir;
    }

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    /**
     * ��Ĭ�����ý����ļ��ϴ�
     *
     * @param request ��ǰ����
     * @param file    �ϴ����ļ�
     * @param result  ��ӳ�����Ϣ
     * @return
     */
    public static final String upload(HttpServletRequest request, MultipartFile file,
                                      BindingResult result) {
        return upload(request, file, result, DEFAULT_ALLOWED_EXTENSION);
    }

    /**
     * ��Ĭ�����ý����ļ��ϴ�
     *
     * @param request          ��ǰ����
     * @param file             �ϴ����ļ�
     * @param result           ��ӳ�����Ϣ
     * @param allowedExtension �����ϴ����ļ�����
     * @return
     */
    public static final String upload(HttpServletRequest request, MultipartFile file,
                                      BindingResult result, String[] allowedExtension) {
        try {
            return upload(request, getDefaultBaseDir(), file, allowedExtension, DEFAULT_MAX_SIZE,
                true);
        } catch (IOException e) {
            LogUtils.logError("file upload error", e);
            result.reject("upload.server.error");
        } catch (InvalidExtensionException.InvalidImageExtensionException e) {
            result.reject("upload.not.allow.image.extension");
        } catch (InvalidExtensionException.InvalidFlashExtensionException e) {
            result.reject("upload.not.allow.flash.extension");
        } catch (InvalidExtensionException.InvalidMediaExtensionException e) {
            result.reject("upload.not.allow.media.extension");
        } catch (InvalidExtensionException e) {
            result.reject("upload.not.allow.extension");
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            result.reject("upload.exceed.maxSize");
        } catch (FileNameLengthLimitExceededException e) {
            result.reject("upload.filename.exceed.length");
        }
        return null;
    }

    /**
     * �ļ��ϴ�
     *
     * @param request          ��ǰ���� ����������ȡ Ӧ�������ĸ�
     * @param baseDir          ���Ӧ�õĻ�Ŀ¼
     * @param file             �ϴ����ļ�
     * @param allowedExtension ������ļ����� null ��ʾ��������
     * @param maxSize          ����ϴ��Ĵ�С -1 ��ʾ������
     *@param needDatePathAndRandomName �Ƿ���Ҫ����Ŀ¼������ļ���ǰ׺
     * @return �����ϴ��ɹ����ļ���
     * @throws InvalidExtensionException      ���MIME���Ͳ�����
     * @throws FileSizeLimitExceededException �����������С
     * @throws FileNameLengthLimitExceededException
     *                                        �ļ���̫��
     * @throws IOException                    �����д�ļ�����ʱ
     */
    public static final String upload(HttpServletRequest request, String baseDir,
                                      MultipartFile file, String[] allowedExtension, long maxSize,
                                      boolean needDatePathAndRandomName)
                                                                        throws InvalidExtensionException,
                                                                        FileSizeLimitExceededException,
                                                                        IOException,
                                                                        FileNameLengthLimitExceededException {

        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(file.getOriginalFilename(),
                fileNamelength, FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file, allowedExtension, maxSize);
        String filename = extractFilename(file, baseDir, needDatePathAndRandomName);

        File desc = getAbsoluteFile(extractUploadDir(request), filename);

        file.transferTo(desc);
        return filename;
    }

    private static final File getAbsoluteFile(String uploadDir, String filename) throws IOException {

        uploadDir = FilenameUtils.normalizeNoEndSeparator(uploadDir);

        File desc = new File(uploadDir + File.separator + filename);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    public static final String extractFilename(MultipartFile file, String baseDir,
                                               boolean needDatePathAndRandomName)
                                                                                 throws UnsupportedEncodingException {
        String filename = file.getOriginalFilename();
        int slashIndex = filename.indexOf("/");
        if (slashIndex >= 0) {
            filename = filename.substring(slashIndex + 1);
        }
        if (needDatePathAndRandomName) {
            filename = baseDir + File.separator + datePath() + File.separator
                       + encodingFilename(filename);
        } else {
            filename = baseDir + File.separator + filename;
        }

        return filename;
    }

    /**
     * �����ļ���,Ĭ�ϸ�ʽΪ��
     * 1��'_'�滻Ϊ ' '
     * 2��hex(md5(filename + now nano time + counter++))
     * 3��[2]_ԭʼ�ļ���
     *
     * @param filename
     * @return
     */
    private static final String encodingFilename(String filename) {
        filename = filename.replace("_", " ");
        filename = Coder.encryptMD5(filename + System.nanoTime() + counter++) + "_" + filename;
        return filename;
    }

    /**
     * ����·�� ����/��/��  ��2013/01/03
     *
     * @return
     */
    private static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * �Ƿ������ļ��ϴ�
     *
     * @param file             �ϴ����ļ�
     * @param allowedExtension �ļ�����  null ��ʾ��������
     * @param maxSize          ����С �ֽ�Ϊ��λ -1��ʾ������
     * @return
     * @throws InvalidExtensionException      ���MIME���Ͳ�����
     * @throws FileSizeLimitExceededException �����������С
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension,
                                           long maxSize) throws InvalidExtensionException,
                                                        FileSizeLimitExceededException {

        String filename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(
                    allowedExtension, extension, filename);
            } else if (allowedExtension == FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(
                    allowedExtension, extension, filename);
            } else if (allowedExtension == MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(
                    allowedExtension, extension, filename);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, filename);
            }
        }

        long size = file.getSize();
        if (maxSize != -1 && size > maxSize) {
            throw new FileSizeLimitExceededException("not allowed upload upload", size, maxSize);
        }
    }

    /**
     * �ж�MIME�����Ƿ��������MIME����
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ��ȡ�ϴ��ĸ�Ŀ¼ Ĭ����Ӧ�õĸ�
     *
     * @param request
     * @return
     */
    public static final String extractUploadDir(HttpServletRequest request) {
        return request.getServletContext().getRealPath("/");
    }

    public static final void delete(HttpServletRequest request, String filename) throws IOException {
        if (StringUtils.isEmpty(filename)) {
            return;
        }
        File desc = getAbsoluteFile(extractUploadDir(request), filename);
        if (desc.exists()) {
            desc.delete();
        }
    }
}
