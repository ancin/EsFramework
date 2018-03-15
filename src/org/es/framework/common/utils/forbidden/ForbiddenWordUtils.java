package org.es.framework.common.utils.forbidden;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.es.framework.common.Constants;
import org.es.framework.common.utils.fetch.RemoteFileFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class ForbiddenWordUtils {

    private static final Logger  LOGGER         = LoggerFactory.getLogger(ForbiddenWordUtils.class);

    /**
     * Ĭ�ϵ���������
     */
    private static final String  DEFAULT_MASK   = "***";
    /**
     * ���ιؼ���ץȡ��url
     */
    private static String        forbiddenWordFetchURL;

    /**
     * ���ιؼ���ץȡʱ���� ����
     */
    private static int           reloadInterval = 60000;                                            //10����

    /**
     * ���ιؼ���
     */
    private static List<Pattern> forbiddenWords;

    public static void setForbiddenWordFetchURL(String forbiddenWordFetchURL) {
        ForbiddenWordUtils.forbiddenWordFetchURL = forbiddenWordFetchURL;
    }

    public static void setReloadInterval(int reloadInterval) {
        ForbiddenWordUtils.reloadInterval = reloadInterval;
    }

    /**
     * �滻input�е����ιؼ���ΪĬ�ϵ�����
     *
     * @param input
     * @return
     */
    public static String replace(String input) {
        return replace(input, DEFAULT_MASK);
    }

    /**
     * �����ιؼ��� �滻Ϊ mask
     *
     * @param input
     * @param mask
     * @return
     */
    public static String replace(String input, String mask) {
        for (int i = 0, l = forbiddenWords.size(); i < l; i++) {
            Pattern forbiddenWordPattern = forbiddenWords.get(i);
            input = forbiddenWordPattern.matcher(input).replaceAll(mask);
        }
        return input;
    }

    /**
     * �Ƿ�������ιؼ���
     *
     * @param input
     * @return
     */
    public static boolean containsForbiddenWord(String input) {
        for (int i = 0, l = forbiddenWords.size(); i < l; i++) {
            Pattern forbiddenWordPattern = forbiddenWords.get(i);
            if (forbiddenWordPattern.matcher(input).find()) {
                return true;
            }
        }
        return false;
    }

    static {
        InputStream is = null;
        try {
            String fileName = "forbidden.txt";
            is = ForbiddenWordUtils.class.getResourceAsStream(fileName);
            byte[] fileCBytes;
            fileCBytes = IOUtils.toByteArray(is);
            ForbiddenWordUtils.loadForbiddenWords(fileCBytes);
        } catch (IOException e) {
            LOGGER.error("read forbidden file failed", e);
        } finally {
            IOUtils.closeQuietly(is);
        }

    }

    /**
     * ��ʼ��Զ��ץȡ����
     */
    public static void initRemoteFetch() {
        RemoteFileFetcher.createPeriodFetcher(forbiddenWordFetchURL, reloadInterval,
            new RemoteFileFetcher.FileChangeListener() {
                @Override
                public void fileReloaded(byte[] fileConent) throws IOException {
                    ForbiddenWordUtils.loadForbiddenWords(fileConent);
                }
            });
    }

    private static void loadForbiddenWords(byte[] fileCBytes) throws IOException {
        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileCBytes),
                Constants.ENCODING));
            List<String> forbiddenWordsStrList = IOUtils.readLines(reader);
            forbiddenWords = Lists.newArrayList();
            for (int i = forbiddenWordsStrList.size() - 1; i >= 0; i--) {
                String forbiddenWord = forbiddenWordsStrList.get(i).trim();
                if (forbiddenWord.length() == 0 || forbiddenWord.startsWith("#")) {
                    continue;
                } else {
                    forbiddenWords.add(Pattern.compile(forbiddenWord));
                }
            }
        } catch (Exception e) {
            LOGGER.error("load forbidden words failed", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

}
