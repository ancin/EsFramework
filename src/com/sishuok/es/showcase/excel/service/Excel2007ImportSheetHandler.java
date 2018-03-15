package com.sishuok.es.showcase.excel.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.Lists;
import com.sishuok.es.showcase.excel.entity.ExcelData;

class Excel2007ImportSheetHandler extends DefaultHandler {

    private final Logger           log             = LoggerFactory
                                                       .getLogger(Excel2007ImportSheetHandler.class);

    private final int              batchSize;                                                        //�������С
    private int                    totalSize       = 0;                                              //������

    private int                    rowNumber       = 1;
    private String                 lastContents;
    private final List<ExcelData>  dataList;
    private final ExcelDataService excelDataService;

    private final List<String>     currentCellData = Lists.newArrayList();

    Excel2007ImportSheetHandler(final ExcelDataService excelDataService,
                                final List<ExcelData> dataList, final int batchSize) {
        this.excelDataService = excelDataService;
        this.dataList = dataList;
        this.batchSize = batchSize;

    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
                                                                                              throws SAXException {
        if ("row".equals(name)) {//������п�ʼ ���cell���� ����
            rowNumber = Integer.valueOf(attributes.getValue("r"));//��ǰ�к�
            if (rowNumber == 1) {
                return;
            }
            currentCellData.clear();
        }

        lastContents = "";
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

        if ("row".equals(name)) {//������п�ʼ ���cell���� ����
            if (rowNumber == 1) {
                return;
            }
            ExcelData data = new ExcelData();
            data.setId(Double.valueOf(currentCellData.get(0)).longValue());
            data.setContent(currentCellData.get(1));
            dataList.add(data);

            totalSize++;

            if (totalSize % batchSize == 0) {
                try {
                    excelDataService.doBatchSave(dataList);
                } catch (Exception e) {
                    Long fromId = dataList.get(0).getId();
                    Long endId = dataList.get(dataList.size() - 1).getId();
                    log.error("from " + fromId + " to " + endId + ", error", e);
                }
                dataList.clear();
            }
        }

        if ("c".equals(name)) {//������˳���������
            currentCellData.add(lastContents);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        lastContents += new String(ch, start, length);
    }
}