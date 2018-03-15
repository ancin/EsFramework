package com.sishuok.es.maintain.staticresource.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.es.framework.common.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sishuok.es.maintain.staticresource.web.controller.utils.YuiCompressorUtils;

/**
 *
 * ��̬��Դ�汾����
 * Ҫ��
 *  ������Ҫ�汾���Ƶľ�̬��Դ ��.jspfΪ��׺ �ŵ�webapp/WEB-INF/jsp/common��
 * ��������ɨ����Щ�ļ�ʵʩ�汾����
 *
 *
 */
@SuppressWarnings("rawtypes")
@Controller
@RequestMapping("/admin/maintain/staticResource")
@RequiresPermissions("maintain:staticResource:*")
public class StaticResourceVersionController extends BaseController {

    private final String   versionedResourcePath = "/WEB-INF/jsp/common";
    private final Pattern  scriptPattern         = Pattern
                                                     .compile(
                                                         "(.*<script.* src=[\'\"])(.*?)(\\??)(\\d*)([\'\"].*>.*)",
                                                         Pattern.CASE_INSENSITIVE);
    private final Pattern  linkPattern           = Pattern
                                                     .compile(
                                                         "(.*<link.* href=[\'\"])(.*?)(\\??)(\\d*)([\'\"].*>.*)",
                                                         Pattern.CASE_INSENSITIVE);

    @Autowired
    private ServletContext sc;

    @RequestMapping()
    public String list(Model model) throws IOException {

        String realPath = sc.getRealPath(versionedResourcePath);

        model.addAttribute("resources", findStaticResources(realPath));

        return viewName("list");
    }

    @RequestMapping(value = "incVersion", method = RequestMethod.POST)
    @ResponseBody
    public String incVersion(@RequestParam("fileName") String fileName,
                             @RequestParam("content") String content,
                             @RequestParam("newVersion") String newVersion) throws IOException {

        String realPath = sc.getRealPath(versionedResourcePath);

        return versionedStaticResourceContent(realPath + fileName, content, newVersion);

    }

    @RequestMapping(value = "batchIncVersion", method = RequestMethod.POST)
    @ResponseBody
    public String batchIncVersion(@RequestParam("fileNames[]") String[] fileNames,
                                  @RequestParam("contents[]") String[] contents,
                                  @RequestParam("newVersions[]") String[] newVersions)
                                                                                      throws IOException {

        String realPath = sc.getRealPath(versionedResourcePath);

        for (int i = 0, l = fileNames.length; i < l; i++) {
            versionedStaticResourceContent(realPath + fileNames[i], contents[i], newVersions[i]);
        }

        return "�����ɹ�";

    }

    @RequestMapping(value = "clearVersion", method = RequestMethod.POST)
    @ResponseBody
    public String clearVersion(@RequestParam("fileNames[]") String[] fileNames,
                               @RequestParam("contents[]") String[] contents) throws IOException {

        String realPath = sc.getRealPath(versionedResourcePath);

        for (int i = 0, l = fileNames.length; i < l; i++) {
            versionedStaticResourceContent(realPath + fileNames[i], contents[i], null);
        }

        return "�����ɹ�";

    }

    @RequestMapping("compress")
    @ResponseBody
    public String compress(@RequestParam("fileName") String fileName,
                           @RequestParam("content") String content) {

        String rootRealPath = sc.getRealPath("/WEB-INF");
        String versionedResourceRealPath = sc.getRealPath(versionedResourcePath);

        try {
            String minFilePath = compressStaticResource(rootRealPath, versionedResourceRealPath
                                                                      + fileName, content);
            return "ѹ���ɹ���ѹ���õ��ļ�Ϊ��" + minFilePath;
        } catch (Exception e) {
            return "ѹ��ʧ�ܣ�" + e.getMessage();
        }
    }

    @RequestMapping("batchCompress")
    @ResponseBody
    public String batchCompress(@RequestParam("fileNames[]") String[] fileNames,
                                @RequestParam("contents[]") String[] contents) throws IOException {

        String rootRealPath = sc.getRealPath("/WEB-INF");
        String versionedResourceRealPath = sc.getRealPath(versionedResourcePath);

        StringBuilder success = new StringBuilder();
        StringBuilder error = new StringBuilder();

        for (int i = 0, l = fileNames.length; i < l; i++) {
            try {
                String fileName = fileNames[i];
                String content = contents[i];
                String minFilePath = compressStaticResource(rootRealPath, versionedResourceRealPath
                                                                          + fileName, content);
                success.append("ѹ���ɹ���ѹ���õ��ļ�Ϊ��" + minFilePath + "<br/>");
            } catch (Exception e) {
                error.append("ѹ��ʧ�ܣ�" + e.getMessage() + "<br/>");
            }
        }

        return success.insert(0, "�ɹ���ѹ����<br/>").append("<br/>ʧ�ܵ�ѹ����<br/>").append(error).toString();
    }

    /**
     * �л��汾
     * @param fileName
     * @param content
     * @return
     */
    @RequestMapping("switch")
    @ResponseBody
    public Object switchStaticResource(@RequestParam("fileName") String fileName,
                                       @RequestParam("content") String content,
                                       @RequestParam(value = "min", required = false, defaultValue = "false") boolean isMin

    ) {

        Map<String, Object> data = Maps.newHashMap();
        data.put("msg", "�л��ɹ�");
        data.put("success", true);

        String rootRealPath = sc.getRealPath("/WEB-INF");
        String versionedResourceRealPath = sc.getRealPath(versionedResourcePath);

        try {
            StaticResource resource = switchStaticResourceContent(rootRealPath,
                versionedResourceRealPath, fileName, content, isMin);
            data.put("content", resource.getContent());
            data.put("url", resource.getUrl());
            return data;
        } catch (Exception e) {
            data.put("msg", "�л�ʧ�ܣ�" + e.getMessage());
            data.put("success", false);
            return data;
        }
    }

    /**
     * �����л��汾
     * @param fileNames
     * @param contents
     * @return
     * @throws IOException
     */
    @RequestMapping("batchSwitch")
    @ResponseBody
    public String batchSwitchStaticResource(@RequestParam("fileNames[]") String[] fileNames,
                                            @RequestParam("contents[]") String[] contents,
                                            @RequestParam(value = "min", required = false, defaultValue = "false") boolean isMin

    ) throws IOException {

        String rootRealPath = sc.getRealPath("/WEB-INF");
        String versionedResourceRealPath = sc.getRealPath(versionedResourcePath);

        StringBuilder success = new StringBuilder();
        StringBuilder error = new StringBuilder();

        for (int i = 0, l = fileNames.length; i < l; i++) {
            try {
                String fileName = fileNames[i];
                String content = contents[i];
                StaticResource resource = switchStaticResourceContent(rootRealPath,
                    versionedResourceRealPath, fileName, content, isMin);
                success.append("�л��ɹ����л������ļ�Ϊ��" + resource.getUrl() + "<br/>");
            } catch (Exception e) {
                error.append("�л�ʧ�ܣ�" + e.getMessage() + "<br/>");
            }
        }

        return success.insert(0, "�ɹ����л���<br/>").append("<br/>ʧ�ܵ��л���<br/>").append(error).toString();
    }

    private StaticResource switchStaticResourceContent(String rootRealPath,
                                                       String versionedResourceRealPath,
                                                       String fileName, String content,
                                                       boolean isMin) throws IOException {

        StaticResource resource = extractResource(fileName, content);
        String filePath = resource.getUrl();
        filePath = filePath.replace("${ctx}", rootRealPath);

        if (isMin) {
            File file = new File(YuiCompressorUtils.getCompressFileName(filePath));
            if (!file.exists()) {
                throw new RuntimeException("����ѹ���ļ���" + resource.getUrl());
            }
        } else {
            File file = new File(YuiCompressorUtils.getNoneCompressFileName(filePath));
            if (!file.exists()) {
                throw new RuntimeException("û��ѹ���ļ���Ӧ�ķ�ѹ���棺" + resource.getUrl());
            }
        }

        content = StringEscapeUtils.unescapeXml(content);

        File file = new File(versionedResourceRealPath + fileName);

        List<String> contents = FileUtils.readLines(file);

        for (int i = 0, l = contents.size(); i < l; i++) {
            String fileContent = contents.get(i);
            if (content.equals(fileContent)) {
                Matcher matcher = scriptPattern.matcher(content);
                if (!matcher.matches()) {
                    matcher = linkPattern.matcher(content);
                }
                String newUrl = isMin ? YuiCompressorUtils.getCompressFileName(resource.getUrl())
                    : YuiCompressorUtils.getNoneCompressFileName(resource.getUrl());

                content = matcher.replaceAll("$1" + Matcher.quoteReplacement(newUrl) + "$3$4$5");
                contents.set(i, content);

                resource.setContent(content);
                resource.setUrl(newUrl);

                break;
            }
        }
        FileUtils.writeLines(file, contents);

        return resource;
    }

    private String compressStaticResource(String rootRealPath, String includeFilePath,
                                          String content) {
        StaticResource resource = extractResource(includeFilePath, content);
        String filePath = resource.getUrl();
        filePath = filePath.replace("${ctx}", rootRealPath);

        if (YuiCompressorUtils.hasCompress(filePath)) {
            throw new RuntimeException("[" + filePath + "]�ļ��Ѿ���ѹ�������ˣ�����Ҫ��ѹ����");
        }

        if (filePath.startsWith("http://")) {
            throw new RuntimeException("[" + filePath + "]�ļ��ǻ������ϵģ��޷�ѹ��");
        }

        String minFilePath = YuiCompressorUtils.compress(filePath);

        return minFilePath.replace(rootRealPath, "${ctx}");

    }

    private String versionedStaticResourceContent(String fileRealPath, String content,
                                                  String newVersion) throws IOException {

        content = StringEscapeUtils.unescapeXml(content);
        if (newVersion != null && newVersion.equals("1")) {
            newVersion = "?" + newVersion;
        }

        File file = new File(fileRealPath);

        List<String> contents = FileUtils.readLines(file);

        for (int i = 0, l = contents.size(); i < l; i++) {
            String fileContent = contents.get(i);
            if (content.equals(fileContent)) {
                Matcher matcher = scriptPattern.matcher(content);
                if (!matcher.matches()) {
                    matcher = linkPattern.matcher(content);
                }
                if (newVersion == null) { //ɾ���汾
                    content = matcher.replaceAll("$1$2$5");
                } else {
                    content = matcher.replaceAll("$1$2$3" + newVersion + "$5");
                }
                contents.set(i, content);
                break;
            }
        }
        FileUtils.writeLines(file, contents);

        return content;
    }

    private Map<String, List<StaticResource>> findStaticResources(String realPath)
                                                                                  throws IOException {

        final Map<String, List<StaticResource>> resources = Maps.newTreeMap();

        final int realPathLength = realPath.length();

        Collection<File> files = FileUtils.listFiles(new File(realPath), new String[] { "jspf" },
            true);

        for (File file : files) {

            String fileName = file.getAbsolutePath().substring(realPathLength);
            List<String> contents = FileUtils.readLines(file);

            List<StaticResource> resourceList = resources.get(fileName);
            if (resourceList == null) {
                resourceList = Lists.newArrayList();
            }

            for (String content : contents) {
                if (!StringUtils.isEmpty(content)) {
                    StaticResource resource = extractResource(fileName, content);
                    if (resource != null) {
                        resourceList.add(resource);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(resourceList)) {
                resources.put(fileName, resourceList);
            }
        }

        return resources;
    }

    private StaticResource extractResource(String fileName, String content) {

        Matcher matcher = scriptPattern.matcher(content);
        if (!matcher.matches()) {
            matcher = linkPattern.matcher(content);
        }

        if (!matcher.matches()) {
            return null;
        }

        String url = matcher.group(2);
        String version = "";
        version = matcher.group(4);

        StaticResource resource = new StaticResource(fileName, content);
        resource.setUrl(url);
        resource.setVersion(version);

        return resource;
    }

    public static class StaticResource {
        private String fileName;
        private String content;
        private String url;
        private String version;

        private StaticResource(String fileName, String content) {
            this.fileName = fileName;
            this.content = content;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return "StaticResource{" + "fileName='" + fileName + '\'' + ", content='" + content
                   + '\'' + ", url='" + url + '\'' + ", version=" + version + '}';
        }
    }

}