package com.sishuok.es.showcase.excel.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.es.framework.common.entity.search.SearchOperator;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.repository.RepositoryHelper;
import org.es.framework.common.service.BaseService;
import org.es.framework.common.utils.FileCharset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sishuok.es.maintain.editor.web.controller.utils.CompressUtils;
import com.sishuok.es.maintain.notification.service.NotificationApi;
import com.sishuok.es.showcase.excel.entity.ExcelData;
import com.sishuok.es.showcase.excel.repository.ExcelDataRepository;
import com.sishuok.es.sys.user.entity.User;

@Service
public class ExcelDataService extends BaseService<ExcelData, Long> {

    private final Logger log                  = LoggerFactory.getLogger(ExcelDataService.class);

    private final int    batchSize            = 1000;                                           //�������С
    private final int    pageSize             = 1000;                                           //��ѯʱÿҳ��С

    /**
     * �����ļ�������С ���������С��ѹ��
     */
    private final int    MAX_EXPORT_FILE_SIZE = 10 * 1024 * 1024;                               //10MB

    private ExcelDataRepository getExcelDataRepository() {
        return (ExcelDataRepository) baseRepository;
    }

    @Autowired
    private NotificationApi notificationApi;

    private final String    storePath              = "upload/excel";
    private final String    EXPORT_FILENAME_PREFIX = "excel";

    public void setNotificationApi(final NotificationApi notificationApi) {
        this.notificationApi = notificationApi;
    }

    @Async
    public void initOneMillionData(final User user) {

        ExcelDataService proxy = (ExcelDataService) AopContext.currentProxy();

        long beginTime = System.currentTimeMillis();

        getExcelDataRepository().truncate();

        final int ONE_MILLION = 1000000; //100w
        for (int i = batchSize; i <= ONE_MILLION; i += batchSize) {
            //����ʹ��AopContext.currentProxy() ��Ϊtask:annotation-drivenû�б�¶proxy����
            proxy.doBatchSave(i - batchSize);
        }

        long endTime = System.currentTimeMillis();

        Map<String, Object> context = Maps.newHashMap();
        context.put("seconds", (endTime - beginTime) / 1000);
        notificationApi.notify(user.getId(), "excelInitDataSuccess", context);

    }

    public void doBatchSave(final int fromId) {
        for (int i = 1; i <= batchSize; i++) {
            Long id = Long.valueOf(fromId + i);
            String content = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
            getExcelDataRepository().save(id, content);
        }
    }

    /**
     * ���������ͻ ���ǣ���������
     * @param dataList
     */
    public void doBatchSave(final List<ExcelData> dataList) {
        for (ExcelData data : dataList) {
            ExcelData dbData = findOne(data.getId());
            if (dbData == null) {
                getExcelDataRepository().save(data.getId(), data.getContent());
            } else {
                dbData.setContent(data.getContent());
                update(dbData);
            }
        }
    }

    /**
     * csv��ʽ
     * @param user
     * @param is
     */
    @Async
    public void importCvs(final User user, final InputStream is) {

        ExcelDataService proxy = ((ExcelDataService) AopContext.currentProxy());
        BufferedInputStream bis = null;
        try {
            long beginTime = System.currentTimeMillis();

            bis = new BufferedInputStream(is);
            String encoding = FileCharset.getCharset(bis);

            LineIterator iterator = IOUtils.lineIterator(bis, encoding);

            String separator = ",";
            int totalSize = 0; //�ܴ�С

            final List<ExcelData> dataList = Lists.newArrayList();

            if (iterator.hasNext()) {
                iterator.nextLine();//������һ�б���
            }

            while (iterator.hasNext()) {

                totalSize++;

                String line = iterator.nextLine();
                String[] dataArray = StringUtils.split(line, separator);

                ExcelData data = new ExcelData();
                data.setId(Long.valueOf(dataArray[0]));
                data.setContent(dataArray[1]);
                dataList.add(data);

                if (totalSize % batchSize == 0) {
                    try {
                        proxy.doBatchSave(dataList);
                    } catch (Exception e) {
                        Long fromId = dataList.get(0).getId();
                        Long endId = dataList.get(dataList.size() - 1).getId();
                        log.error("from " + fromId + " to " + endId + ", error", e);
                    }
                    dataList.clear();
                }
            }

            if (dataList.size() > 0) {
                proxy.doBatchSave(dataList);
            }

            long endTime = System.currentTimeMillis();

            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
            notificationApi.notify(user.getId(), "excelImportSuccess", context);
        } catch (Exception e) {
            log.error("excel import error", e);
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
            notificationApi.notify(user.getId(), "excelImportError", context);
        } finally {
            IOUtils.closeQuietly(bis);
        }
    }

    /**
     * ���� excel 2003 biff��ʽ
     * �����xml��ʽ�� ����ʹ��SAX��δ���ԣ�
     * @param user
     * @param is
     */
    @Async
    public void importExcel2003(final User user, final InputStream is) {

        ExcelDataService proxy = ((ExcelDataService) AopContext.currentProxy());

        BufferedInputStream bis = null;
        InputStream dis = null;
        try {
            long beginTime = System.currentTimeMillis();

            List<ExcelData> dataList = Lists.newArrayList();

            //������
            bis = new BufferedInputStream(is);
            // ���� org.apache.poi.poifs.filesystem.Filesystem
            POIFSFileSystem poifs = new POIFSFileSystem(bis);
            // �������� �õ� Workbook(excel ����)��
            dis = poifs.createDocumentInputStream("Workbook");
            // ���� HSSFRequest
            HSSFRequest req = new HSSFRequest();

            // ��Ӽ�����
            req.addListenerForAllRecords(new Excel2003ImportListener(proxy, dataList, batchSize));
            //  �����¼�����
            HSSFEventFactory factory = new HSSFEventFactory();
            // �����ĵ������������¼�
            factory.processEvents(req, dis);

            //�����ʣ�µĲ���batchSize��С
            if (dataList.size() > 0) {
                proxy.doBatchSave(dataList);
            }

            long endTime = System.currentTimeMillis();

            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
            notificationApi.notify(user.getId(), "excelImportSuccess", context);
        } catch (Exception e) {
            log.error("excel import error", e);
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
            notificationApi.notify(user.getId(), "excelImportError", context);
        } finally {
            // �ر�������
            IOUtils.closeQuietly(bis);
            // �ر��ĵ���
            IOUtils.closeQuietly(dis);
        }
    }

    @Async
    public void importExcel2007(final User user, final InputStream is) {

        ExcelDataService proxy = ((ExcelDataService) AopContext.currentProxy());

        BufferedInputStream bis = null;
        try {
            long beginTime = System.currentTimeMillis();

            List<ExcelData> dataList = Lists.newArrayList();

            bis = new BufferedInputStream(is);
            OPCPackage pkg = OPCPackage.open(bis);
            XSSFReader r = new XSSFReader(pkg);

            XMLReader parser = XMLReaderFactory.createXMLReader();
            ContentHandler handler = new Excel2007ImportSheetHandler(proxy, dataList, batchSize);
            parser.setContentHandler(handler);

            Iterator<InputStream> sheets = r.getSheetsData();
            while (sheets.hasNext()) {
                InputStream sheet = null;
                try {
                    sheet = sheets.next();
                    InputSource sheetSource = new InputSource(sheet);
                    parser.parse(sheetSource);
                } catch (Exception e) {
                    throw e;
                } finally {
                    IOUtils.closeQuietly(sheet);
                }
            }

            //�����ʣ�µĲ���batchSize��С
            if (dataList.size() > 0) {
                proxy.doBatchSave(dataList);
            }

            long endTime = System.currentTimeMillis();
            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
            notificationApi.notify(user.getId(), "excelImportSuccess", context);
        } catch (Exception e) {
            log.error("excel import error", e);
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
            notificationApi.notify(user.getId(), "excelImportError", context);
        } finally {
            IOUtils.closeQuietly(bis);
        }
    }

    @Async
    public void exportCvs(final User user, final String contextRootPath, final Searchable searchable) {
        String encoding = "gbk";
        int perSheetRows = 60000; //ÿ��sheet 6w��
        int totalRows = 0;
        String separator = ",";
        Long maxId = 0L;

        String fileName = generateFilename(user, contextRootPath, "csv");
        File file = new File(fileName);
        BufferedOutputStream out = null;
        try {
            long beginTime = System.currentTimeMillis();

            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write("���,����\n".getBytes(encoding));

            while (true) {
                totalRows = 0;
                Page<ExcelData> page = null;
                do {
                    searchable.setPage(0, pageSize);
                    //�Ż���ҳ����
                    if (!searchable.containsSearchKey("id_in")) {
                        searchable.addSearchFilter("id", SearchOperator.gt, maxId);
                    }
                    page = findAll(searchable);
                    for (ExcelData data : page.getContent()) {
                        out.write(String.valueOf(data.getId()).getBytes(encoding));
                        out.write(separator.getBytes(encoding));
                        out.write((data.getContent()).replace(separator, ";").getBytes(encoding));
                        out.write("\n".getBytes(encoding));
                        maxId = Math.max(maxId, data.getId());
                        totalRows++;
                    }
                    //clear entity manager
                    RepositoryHelper.clear();
                } while (page.hasNextPage() && totalRows <= perSheetRows);

                if (!page.hasNextPage()) {
                    break;
                }
            }

            IOUtils.closeQuietly(out);

            if (needCompress(file)) {
                fileName = compressAndDeleteOriginal(fileName);
            }

            long endTime = System.currentTimeMillis();

            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
            context.put("url", fileName.replace(contextRootPath, ""));
            notificationApi.notify(user.getId(), "excelExportSuccess", context);
        } catch (Exception e) {
            IOUtils.closeQuietly(out);
            log.error("excel export error", e);
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
            notificationApi.notify(user.getId(), "excelExportError", context);
        }
    }

    /**
     * д���workbook
     * 1�����û�һ��vbs �ű��ϲ�
     * 2�����û�дһ��c#����ϲ�
     * ������ô�鷳 ��Ҫʱ��д�ɣ�������ֱ�����û�װoffice 2007 ���򵥡�
     * @param user
     * @param contextRootPath
     * @param searchable
     */
    @Async
    public void exportExcel2003WithOneSheetPerWorkBook(final User user,
                                                       final String contextRootPath,
                                                       final Searchable searchable) {
        int workbookCount = 0;
        List<String> workbookFileNames = new ArrayList<String>();
        int perSheetRows = 60000; //ÿ��sheet 6w��
        int totalRows = 0;
        String extension = "xls";

        int pageSize = 1000;
        Long maxId = 0L;

        BufferedOutputStream out = null;
        try {
            long beginTime = System.currentTimeMillis();

            while (true) {
                workbookCount++;
                String fileName = generateFilename(user, contextRootPath, workbookCount, extension);
                workbookFileNames.add(fileName);
                File file = new File(fileName);

                HSSFWorkbook wb = new HSSFWorkbook();
                Sheet sheet = wb.createSheet();
                Row headerRow = sheet.createRow(0);
                Cell idHeaderCell = headerRow.createCell(0);
                idHeaderCell.setCellValue("���");
                Cell contentHeaderCell = headerRow.createCell(1);
                contentHeaderCell.setCellValue("����");

                totalRows = 1;

                Page<ExcelData> page = null;

                do {
                    searchable.setPage(0, pageSize);
                    //�Ż���ҳ����
                    if (!searchable.containsSearchKey("id_in")) {
                        searchable.addSearchFilter("id", SearchOperator.gt, maxId);
                    }
                    page = findAll(searchable);

                    for (ExcelData data : page.getContent()) {
                        Row row = sheet.createRow(totalRows);
                        Cell idCell = row.createCell(0);
                        idCell.setCellValue(data.getId());
                        Cell contentCell = row.createCell(1);
                        contentCell.setCellValue(data.getContent());
                        maxId = Math.max(maxId, data.getId());
                        totalRows++;
                    }
                    //clear entity manager
                    RepositoryHelper.clear();
                } while (page.hasNextPage() && totalRows <= perSheetRows);

                out = new BufferedOutputStream(new FileOutputStream(file));
                wb.write(out);

                IOUtils.closeQuietly(out);

                if (!page.hasNextPage()) {
                    break;
                }
            }

            String fileName = workbookFileNames.get(0);
            if (workbookCount > 1 || needCompress(new File(fileName))) {
                fileName = fileName.substring(0, fileName.lastIndexOf("_")) + ".zip";
                //ȥ������
                compressAndDeleteOriginal(fileName, workbookFileNames.toArray(new String[0]));
            } else {
                String newFileName = fileName.substring(0, fileName.lastIndexOf("_")) + "."
                                     + extension;
                FileUtils.moveFile(new File(fileName), new File(newFileName));
                fileName = newFileName;
            }

            long endTime = System.currentTimeMillis();

            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
            context.put("url", fileName.replace(contextRootPath, ""));
            notificationApi.notify(user.getId(), "excelExportSuccess", context);
        } catch (Exception e) {
            e.printStackTrace();
            //�Է���һ
            IOUtils.closeQuietly(out);
            log.error("excel export error", e);
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
            notificationApi.notify(user.getId(), "excelExportError", context);
        }
    }

    /**
     * excel 2003
     * 1������ģ�壬���Ϊ��XML���
     * 2����Ҫ�ڵ���֮ǰ��������ģ�壨���ȵ�excel�ж���ø�ʽ����֧�ָ��Ӹ�ʽ����ͼƬ�ȣ���Ȼ���������
     * 3�����Ӹ�ʽ�뿼��testExportExcel2003_3
     * <p/>
     * ���ɵ��ļ��޴󡣡�
     * <p/>
     * ��һ����дhtml��ȱ�㲻֧�ֶ�sheet��
     * @param user
     * @param contextRootPath
     * @param searchable
     */
    @Async
    public void exportExcel2003WithXml(final User user, final String contextRootPath,
                                       final Searchable searchable) {

        int perSheetRows = 60000; //ÿ��sheet 6w��
        int totalSheets = 0;
        int totalRows = 0;
        Long maxId = 0L;
        String templateEncoding = "utf-8";

        String fileName = generateFilename(user, contextRootPath, "xls");
        File file = new File(fileName);
        BufferedOutputStream out = null;
        try {
            long beginTime = System.currentTimeMillis();

            String workBookHeader = IOUtils.toString(ExcelDataService.class
                .getResourceAsStream("excel_2003_xml_workbook_header.txt"));
            String workBookFooter = IOUtils.toString(ExcelDataService.class
                .getResourceAsStream("excel_2003_xml_workbook_footer.txt"));
            String sheetHeader = IOUtils.toString(ExcelDataService.class
                .getResourceAsStream("excel_2003_xml_sheet_header.txt"));
            String sheetFooter = IOUtils.toString(ExcelDataService.class
                .getResourceAsStream("excel_2003_xml_sheet_footer.txt"));
            String rowTemplate = IOUtils.toString(ExcelDataService.class
                .getResourceAsStream("excel_2003_xml_row.txt"));

            out = new BufferedOutputStream(new FileOutputStream(file));

            out.write(workBookHeader.getBytes(templateEncoding));

            while (true) {
                totalSheets++;

                out.write(sheetHeader.replace("{sheetName}", "Sheet" + totalSheets).getBytes(
                    templateEncoding));

                Page<ExcelData> page = null;

                totalRows = 1;
                do {
                    searchable.setPage(0, pageSize);
                    //�Ż���ҳ����
                    if (!searchable.containsSearchKey("id_in")) {
                        searchable.addSearchFilter("id", SearchOperator.gt, maxId);
                    }
                    page = findAll(searchable);

                    for (ExcelData data : page.getContent()) {
                        out.write(rowTemplate.replace("{id}", String.valueOf(data.getId()))
                            .replace("{content}", data.getContent()).getBytes(templateEncoding));
                        maxId = Math.max(maxId, data.getId());
                        totalRows++;
                    }
                    //clear entity manager
                    RepositoryHelper.clear();
                } while (page.hasNextPage() && totalRows <= perSheetRows);

                out.write(sheetFooter.getBytes(templateEncoding));

                if (!page.hasNextPage()) {
                    break;
                }
            }

            out.write(workBookFooter.getBytes(templateEncoding));

            IOUtils.closeQuietly(out);

            if (needCompress(file)) {
                fileName = compressAndDeleteOriginal(fileName);
            }

            long endTime = System.currentTimeMillis();

            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
            context.put("url", fileName.replace(contextRootPath, ""));
            notificationApi.notify(user.getId(), "excelExportSuccess", context);
        } catch (Exception e) {
            e.printStackTrace();
            IOUtils.closeQuietly(out);
            log.error("excel export error", e);
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
            notificationApi.notify(user.getId(), "excelExportError", context);
        }
    }

    /**
     * excel 2003
     * ��֧�ִ�������
     * ÿ��sheet���65536��(��Ϊ��usermodelģ�ͣ�������д���ڴ� ���flush��ȥ ��֧�ִ�����������)
     * @param user
     * @param contextRootPath
     * @param searchable
     */
    @Async
    public void exportExcel2003WithUsermodel(final User user, final String contextRootPath,
                                             final Searchable searchable) {
        int perSheetRows = 60000; //ÿ��sheet 6w��
        int totalRows = 0;
        Long maxId = 0L;

        String fileName = generateFilename(user, contextRootPath, "xls");
        File file = new File(fileName);
        BufferedOutputStream out = null;
        try {
            long beginTime = System.currentTimeMillis();

            HSSFWorkbook wb = new HSSFWorkbook();
            while (true) {
                Sheet sheet = wb.createSheet();
                Row headerRow = sheet.createRow(0);
                Cell idHeaderCell = headerRow.createCell(0);
                idHeaderCell.setCellValue("���");
                Cell contentHeaderCell = headerRow.createCell(1);
                contentHeaderCell.setCellValue("����");

                totalRows = 1;
                Page<ExcelData> page = null;
                do {
                    searchable.setPage(0, pageSize);
                    //�Ż���ҳ����
                    if (!searchable.containsSearchKey("id_in")) {
                        searchable.addSearchFilter("id", SearchOperator.gt, maxId);
                    }
                    page = findAll(searchable);

                    for (ExcelData data : page.getContent()) {
                        Row row = sheet.createRow(totalRows);
                        Cell idCell = row.createCell(0);
                        idCell.setCellValue(data.getId());
                        Cell contentCell = row.createCell(1);
                        contentCell.setCellValue(data.getContent());
                        maxId = Math.max(maxId, data.getId());
                        totalRows++;
                    }
                    //clear entity manager
                    RepositoryHelper.clear();
                } while (page.hasNextPage() && totalRows <= perSheetRows);

                if (!page.hasNextPage()) {
                    break;
                }
            }

            out = new BufferedOutputStream(new FileOutputStream(file));
            wb.write(out);

            IOUtils.closeQuietly(out);

            if (needCompress(file)) {
                fileName = compressAndDeleteOriginal(fileName);
            }

            long endTime = System.currentTimeMillis();

            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
            context.put("url", fileName.replace(contextRootPath, ""));
            notificationApi.notify(user.getId(), "excelExportSuccess", context);
        } catch (Exception e) {
            IOUtils.closeQuietly(out);
            log.error("excel export error", e);
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
            notificationApi.notify(user.getId(), "excelExportError", context);
        }
    }

    /**
     * ֧�ִ�����������
     * excel 2007 ÿ��sheet���1048576��
     * @param user
     * @param contextRootPath
     * @param searchable
     */
    @Async
    public void exportExcel2007(final User user, final String contextRootPath,
                                final Searchable searchable) {

        int rowAccessWindowSize = 1000; //�ڴ��б������������������д������
        int perSheetRows = 100000; //ÿ��sheet 10w��
        int totalRows = 0; //ͳ��������
        Long maxId = 0L;//��ǰ��ѯ������������id �Ż���ҳ��

        String fileName = generateFilename(user, contextRootPath, "xlsx");
        File file = new File(fileName);
        BufferedOutputStream out = null;
        SXSSFWorkbook wb = null;
        try {
            long beginTime = System.currentTimeMillis();

            wb = new SXSSFWorkbook(rowAccessWindowSize);
            wb.setCompressTempFiles(true);//���ɵ���ʱ�ļ�������gzipѹ��

            while (true) {

                Sheet sheet = wb.createSheet();
                Row headerRow = sheet.createRow(0);
                Cell idHeaderCell = headerRow.createCell(0);
                idHeaderCell.setCellValue("���");
                Cell contentHeaderCell = headerRow.createCell(1);
                contentHeaderCell.setCellValue("����");

                totalRows = 1;

                Page<ExcelData> page = null;

                do {
                    searchable.setPage(0, pageSize);
                    //�Ż���ҳ����
                    if (!searchable.containsSearchKey("id_in")) {
                        searchable.addSearchFilter("id", SearchOperator.gt, maxId);
                    }
                    page = findAll(searchable);

                    for (ExcelData data : page.getContent()) {
                        Row row = sheet.createRow(totalRows);
                        Cell idCell = row.createCell(0);
                        idCell.setCellValue(data.getId());
                        Cell contentCell = row.createCell(1);
                        contentCell.setCellValue(data.getContent());
                        maxId = Math.max(maxId, data.getId());
                        totalRows++;
                    }
                    //clear entity manager
                    RepositoryHelper.clear();
                } while (page.hasNextPage() && totalRows <= perSheetRows);

                if (!page.hasNextPage()) {
                    break;
                }
            }
            out = new BufferedOutputStream(new FileOutputStream(file));
            wb.write(out);

            IOUtils.closeQuietly(out);

            if (needCompress(file)) {
                fileName = compressAndDeleteOriginal(fileName);
            }

            long endTime = System.currentTimeMillis();

            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
            context.put("url", fileName.replace(contextRootPath, ""));
            notificationApi.notify(user.getId(), "excelExportSuccess", context);
        } catch (Exception e) {
            IOUtils.closeQuietly(out);
            log.error("excel export error", e);
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
            notificationApi.notify(user.getId(), "excelExportError", context);
        } finally {
            // ����������������ڴ����ϵ���ʱ�ļ�
            wb.dispose();
        }
    }

    private String compressAndDeleteOriginal(final String filename) {
        String newFileName = FilenameUtils.removeExtension(filename) + ".zip";
        compressAndDeleteOriginal(newFileName, filename);
        return newFileName;
    }

    private void compressAndDeleteOriginal(final String newFileName,
                                           final String... needCompressFilenames) {
        CompressUtils.zip(newFileName, needCompressFilenames);
        for (String needCompressFilename : needCompressFilenames) {
            FileUtils.deleteQuietly(new File(needCompressFilename));
        }
    }

    private boolean needCompress(final File file) {
        return file.length() > MAX_EXPORT_FILE_SIZE;
    }

    /**
     * ����Ҫ�������ļ���
     * @param user
     * @param contextRootPath
     * @param extension
     * @return
     */
    private String generateFilename(final User user, final String contextRootPath,
                                    final String extension) {
        return generateFilename(user, contextRootPath, null, extension);
    }

    private String generateFilename(final User user, final String contextRootPath, Integer index,
                                    final String extension) {
        String path = FilenameUtils.concat(contextRootPath, storePath);
        path = FilenameUtils.concat(path, user.getUsername());
        path = FilenameUtils.concat(path,
            EXPORT_FILENAME_PREFIX + "_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")
                    + (index != null ? ("_" + index) : "") + "." + extension);

        File file = new File(path);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            return path;
        }
        return generateFilename(user, contextRootPath, extension);
    }

}
