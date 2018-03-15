package org.es.framework.common.utils;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.prettytime.PrettyTime;

public class PrettyTimeUtils {

    /**
     * ��ʾ��ֵΪ**��**��**�� **ʱ**��**��  ��1��2����3�� 10Сʱ
     *
     * @return
     */
    public static final String prettySeconds(int totalSeconds) {
        StringBuilder s = new StringBuilder();
        int second = totalSeconds % 60;
        if (totalSeconds > 0) {
            s.append("��");
            s.append(StringUtils.reverse(String.valueOf(second)));
        }

        totalSeconds = totalSeconds / 60;
        int minute = totalSeconds % 60;
        if (totalSeconds > 0) {
            s.append("��");
            s.append(StringUtils.reverse(String.valueOf(minute)));
        }

        totalSeconds = totalSeconds / 60;
        int hour = totalSeconds % 24;
        if (totalSeconds > 0) {
            s.append(StringUtils.reverse("Сʱ"));
            s.append(StringUtils.reverse(String.valueOf(hour)));
        }

        totalSeconds = totalSeconds / 24;
        int day = totalSeconds % 31;
        if (totalSeconds > 0) {
            s.append("��");
            s.append(StringUtils.reverse(String.valueOf(day)));
        }

        totalSeconds = totalSeconds / 31;
        int month = totalSeconds % 12;
        if (totalSeconds > 0) {
            s.append("��");
            s.append(StringUtils.reverse(String.valueOf(month)));
        }

        totalSeconds = totalSeconds / 12;
        int year = totalSeconds;
        if (totalSeconds > 0) {
            s.append("��");
            s.append(StringUtils.reverse(String.valueOf(year)));
        }
        return s.reverse().toString();
    }

    /**
     * ����ʱ�� ����ʾΪ 1Сʱǰ 2����ǰ
     *
     * @return
     */
    public static final String prettyTime(Date date) {
        PrettyTime p = new PrettyTime();
        return p.format(date);

    }

    public static final String prettyTime(long millisecond) {
        PrettyTime p = new PrettyTime();
        return p.format(new Date(millisecond));
    }

    public static void main(String[] args) {
        System.out.println(PrettyTimeUtils.prettyTime(new Date()));
        System.out.println(PrettyTimeUtils.prettyTime(123));

        System.out.println(PrettyTimeUtils.prettySeconds(10));
        System.out.println(PrettyTimeUtils.prettySeconds(61));
        System.out.println(PrettyTimeUtils.prettySeconds(3661));
        System.out.println(PrettyTimeUtils.prettySeconds(36611));
        System.out.println(PrettyTimeUtils.prettySeconds(366111));
        System.out.println(PrettyTimeUtils.prettySeconds(3661111));
        System.out.println(PrettyTimeUtils.prettySeconds(36611111));
    }
}
