package org.es.framework.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/***
 * �ж��ļ����� ����֤100%��ȷ�� �����Խ����һ����ļ�û����
 * <p/>
 * ֻ���жϳ�����GBK��UTF-16LE��UTF-16BE,UTF-8����ֱ��Ӧwindow�µļ��±������Ϊ�ı�������ANSI,Unicode,Unicode big endian,UTF-8
 * <p/>
 * <p/>
 * 
 * @author kejun.song
 * @version $Id: FileCharset.java, v 0.1 2014��11��19�� ����11:11:55 kejun.song Exp $
 */
public class FileCharset {

    private static final String DEFAULT_CHARSET = "GBK";

    public static String getCharset(String fileName) {
        return getCharset(new File(fileName));
    }

    /**
     * ֻ���жϳ�����GBK��UTF-16LE��UTF-16BE,UTF-8����ֱ��Ӧwindow�µļ��±������Ϊ�ı�������ANSI,Unicode,Unicode big endian,UTF-8
     *
     * @param file
     * @return
     */
    public static String getCharset(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return getCharset(new BufferedInputStream(is));
        } catch (FileNotFoundException e) {
            return DEFAULT_CHARSET;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static String getCharset(final BufferedInputStream is) {
        String charset = DEFAULT_CHARSET;
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            is.mark(0);
            int read = is.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                       && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            is.reset();
            if (!checked) {
                int loc = 0;

                while ((read = is.read()) != -1 && loc < 100) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // ��������BF���µģ�Ҳ����GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = is.read();
                        if (0x80 <= read && read <= 0xBF) // ˫�ֽ� (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),Ҳ������GB������
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// Ҳ�п��ܳ������Ǽ��ʽ�С
                        read = is.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = is.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            is.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return charset;
    }

}
