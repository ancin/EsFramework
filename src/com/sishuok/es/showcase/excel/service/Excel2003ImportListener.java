package com.sishuok.es.showcase.excel.service;

import java.util.List;

import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;

import com.sishuok.es.showcase.excel.entity.ExcelData;

class Excel2003ImportListener implements HSSFListener {

    int                            batchSize;       //�������С
    int                            totalSize = 0;   //�ܴ�С
    private SSTRecord              sstrec;

    List<ExcelData>                dataList;
    ExcelData                      current   = null;
    private final ExcelDataService excelDataService;

    Excel2003ImportListener(final ExcelDataService excelDataService,
                            final List<ExcelData> dataList, final int batchSize) {
        this.excelDataService = excelDataService;
        this.dataList = dataList;
        this.batchSize = batchSize;
    }

    @SuppressWarnings("static-access")
    @Override
    public void processRecord(final Record record) {
        switch (record.getSid()) {
            case BOFRecord.sid:
                //��ʼ������workboot sheet ��
                BOFRecord bof = (BOFRecord) record;
                if (bof.getType() == bof.TYPE_WORKBOOK) {
                    //workbook
                } else if (bof.getType() == bof.TYPE_WORKSHEET) {
                    //sheet
                }
                break;
            case BoundSheetRecord.sid:
                //��ʼ����BundleSheet
                BoundSheetRecord bsr = (BoundSheetRecord) record;
                //bsr.getSheetname() �õ�sheet name
                break;
            case RowRecord.sid:
                //��ʼ������
                RowRecord rowrec = (RowRecord) record;
                break;
            case SSTRecord.sid:
                // SSTRecords�洢����Excel��ʹ�õ�����ΨһString������
                sstrec = (SSTRecord) record;
                break;
            case NumberRecord.sid:
            case LabelSSTRecord.sid:
                if (record instanceof NumberRecord) {
                    //����һ��Number���͵ĵ�Ԫ��ֵ
                    NumberRecord numrec = (NumberRecord) record;
                    //numrec.getRow()  numrec.getColumn()   numrec.getValue()

                    if (numrec.getRow() == 0) {
                        //��һ�� ����
                        break;
                    } else if (numrec.getColumn() == 0) { //��һ��
                        current = new ExcelData();
                        current.setId(Double.valueOf(numrec.getValue()).longValue());
                    } else if (numrec.getColumn() == 1) {//�ڶ���
                        current.setContent(String.valueOf(Double.valueOf(numrec.getValue())
                            .longValue()));
                        add(current);
                    }
                    break;

                } else if (record instanceof LabelSSTRecord) {
                    //����һ��String���͵ĵ�Ԫ��ֵ���洢��SSTRecord��
                    LabelSSTRecord lrec = (LabelSSTRecord) record;

                    if (lrec.getRow() == 0) {
                        //��һ�� ����
                        break;
                    } else if (lrec.getColumn() == 0) { //��һ��
                        current = new ExcelData();
                        String value = sstrec.getString(lrec.getSSTIndex()).getString();
                        current.setId(Double.valueOf(value).longValue());
                    } else if (lrec.getColumn() == 1) {//�ڶ���
                        String value = sstrec.getString(lrec.getSSTIndex()).getString();
                        current.setContent(value);
                        add(current);
                    }
                    break;
                }
                break;
        }

    }

    private void add(final ExcelData current) {

        dataList.add(current);

        totalSize++;

        //���һ����Ԫ��ʱ �ж��Ƿ��д��
        if (totalSize % batchSize == 0) {
            excelDataService.doBatchSave(dataList);
            dataList.clear();
        }
    }
}
