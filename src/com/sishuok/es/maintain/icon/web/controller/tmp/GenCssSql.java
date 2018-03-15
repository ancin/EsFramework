/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.icon.web.controller.tmp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

public class GenCssSql {

    @SuppressWarnings("unused")
    private static void readUploadFile() throws IOException {
        String fromFile = "E:\\workspace\\git\\es\\web\\src\\main\\webapp\\WEB-INF\\static\\comp\\zTree\\css\\zTreeStyle\\img\\diy\\icon.txt";
        String toFile = "C:\\Documents and Settings\\Administrator\\桌面\\b.sql";
        String template = "insert into `maintain_icon` (`id`, `identity`, `img_src`, `type`, `width`, `height`) values(%1$d, '%2$s', '%3$s', 'upload_file', %4$d, %5$d);";

        List<String> list = FileUtils.readLines(new File(fromFile));
        FileWriter writer = new FileWriter(toFile);

        int count = 300;
        for (int i = 0, l = list.size(); i < l; i += 2) {
            writer.write(String.format(template, count++, list.get(i), list.get(i + 1), 16, 16));
            writer.write("\r\n");
        }

        writer.close();
    }

    @SuppressWarnings("unused")
    private static void readClass() throws IOException {
        String fromFile = "C:\\Documents and Settings\\Administrator\\桌面\\a.txt";
        String toFile = "C:\\Documents and Settings\\Administrator\\桌面\\b.sql";
        String template = "insert into `maintain_icon` (`id`, `identity`, `css_class`, `type`) values(%1$d, '%2$s', '%2$s', 'css_class');;";

        List<String> cssClassList = FileUtils.readLines(new File(fromFile));
        List<String> hasReadList = Lists.newArrayList();
        FileWriter writer = new FileWriter(toFile);

        for (int i = 0, l = cssClassList.size(); i < l; i++) {
            if (!hasReadList.contains(cssClassList.get(i))) {
                writer.write(String.format(template, i + 1, cssClassList.get(i).trim()));
                writer.write("\r\n");
                hasReadList.add(cssClassList.get(i));
            }
        }

        writer.close();
    }
}
