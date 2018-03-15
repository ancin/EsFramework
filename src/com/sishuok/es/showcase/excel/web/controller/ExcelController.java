package com.sishuok.es.showcase.excel.web.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.es.framework.common.Constants;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.web.controller.BaseCRUDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.showcase.excel.entity.ExcelData;
import com.sishuok.es.showcase.excel.service.ExcelDataService;
import com.sishuok.es.showcase.excel.web.controller.entity.ExcelDataType;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.web.bind.annotation.CurrentUser;

@Controller
@RequestMapping(value = "/showcase/excel")
public class ExcelController extends BaseCRUDController<ExcelData, Long> {

    private ExcelDataService getExcelDataService() {
        return (ExcelDataService) baseService;
    }

    @Autowired
    private ServletContext servletContext;

    /**
     * ����excel�洢��Ŀ¼
     */
    private String         contextRootPath;

    public ExcelController() {
        setResourceIdentity("showcase:excel");
    }

    @PostConstruct
    public void afterPropertiesSet() {
        contextRootPath = servletContext.getRealPath("/");
    }

    /**
     * ��ʼ��100w����
     * @return
     */
    @RequestMapping("/init")
    public String initOneMillionData(@CurrentUser User user, RedirectAttributes redirectAttributes) {
        getExcelDataService().initOneMillionData(user);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE,
            "�����Ѿ��ύ������ִ�У��������Ƚϴ�ִ����ɺ����ҳ�����Ͻǵġ��ҵ�֪ͨ����֪ͨ��");
        return redirectToUrl(null);
    }

    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public String showImportExcelForm(@RequestParam("type") ExcelDataType type, Model model) {
        model.addAttribute("type", type);
        return viewName("importForm");
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importExcel(@CurrentUser User user, @RequestParam("type") ExcelDataType type,
                              @RequestParam("file") MultipartFile file, Model model,
                              RedirectAttributes redirectAttributes) throws IOException {

        if (!canImport(file, model)) {
            return showImportExcelForm(type, model);
        }

        InputStream is = file.getInputStream();

        switch (type) {
            case csv:
                getExcelDataService().importCvs(user, is);
                break;
            case excel2003:
                getExcelDataService().importExcel2003(user, is);
                break;
            case excel2007:
                getExcelDataService().importExcel2007(user, is);
                break;
            default:
                //none
        }

        redirectAttributes
            .addFlashAttribute(Constants.MESSAGE, "�����������ύ������ִ����ɺ����ҳ�����Ͻǵġ��ҵ�֪ͨ����֪ͨ��");
        return redirectToUrl(null);
    }

    private boolean canImport(final MultipartFile file, final Model model) {
        if (file == null || file.isEmpty()) {
            model.addAttribute(Constants.ERROR, "��ѡ��Ҫ������ļ�");
            return false;
        }

        String filename = file.getOriginalFilename().toLowerCase();
        if (!(filename.endsWith("csv") || filename.endsWith("xls") || filename.endsWith("xlsx"))) {
            model.addAttribute(Constants.ERROR, "������ļ���ʽ��������ĸ�ʽ��csv��xls��xlsx");
            return false;
        }

        return true;
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public String exportExcel(@CurrentUser User user, Searchable searchable,
                              @RequestParam("type") ExcelDataType type,
                              RedirectAttributes redirectAttributes) throws IOException {

        switch (type) {
            case csv:
                getExcelDataService().exportCvs(user, contextRootPath, searchable);
                break;
            case excel2003_sheet:
                getExcelDataService().exportExcel2003WithOneSheetPerWorkBook(user, contextRootPath,
                    searchable);
                break;
            case excel2003_xml:
                getExcelDataService().exportExcel2003WithXml(user, contextRootPath, searchable);
                break;
            case excel2003_usermodel:
                getExcelDataService().exportExcel2003WithUsermodel(user, contextRootPath,
                    searchable);
                break;
            case excel2007:
                getExcelDataService().exportExcel2007(user, contextRootPath, searchable);
                break;
            default:
                //none
        }

        redirectAttributes
            .addFlashAttribute(Constants.MESSAGE, "�����������ύ������ִ����ɺ����ҳ�����Ͻǵġ��ҵ�֪ͨ����֪ͨ��");
        return redirectToUrl(null);
    }

}
