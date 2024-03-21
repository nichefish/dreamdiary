package io.nicheblog.dreamdiary.global.util;

import com.ibm.icu.util.ChineseCalendar;
import lombok.experimental.UtilityClass;

import java.util.Calendar;
import java.util.Date;

/**
 * ChineseCalUtils
 * <pre>
 *  음력/양력 관련 처리 유틸리티 모듈
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
public class ChineseCalUtil {

    /**
     * 양력 날짜를 음력 날짜로 변환
     */
    public static Date solarToLunar(final Object date) throws Exception {

        ChineseCalendar chineseCal = new ChineseCalendar();
        Calendar cal = Calendar.getInstance();

        String dateStr = DateUtil.asStr(date, DateUtil.PTN_DATE);
        String[] dateStrArr = dateStr.split("-");

        // Calendar에 날짜 세팅
        cal.set(Calendar.YEAR, Integer.parseInt(dateStrArr[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(dateStrArr[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateStrArr[2]));

        // ChineseCalendar로 변환 후 날짜 추출
        chineseCal.setTimeInMillis(cal.getTimeInMillis());

        int yyyy = chineseCal.get(chineseCal.EXTENDED_YEAR) - 2637;
        int mm = chineseCal.get(chineseCal.MONTH) + 1;
        int dd = chineseCal.get(chineseCal.DAY_OF_MONTH);

        return DateUtil.asDate(String.format("%04d", yyyy) + String.format("%02d", mm) + String.format("%02d", dd));
    }

    /**
     * 양력 날짜를 음력 날짜 문자열로 변환
     */
    public static String solarToLunarStr(
            final Object date,
            final String dtFormat
    ) throws Exception {
        return DateUtil.asStr(solarToLunar(date), dtFormat);
    }

    /**
     * 음력 날짜를 양력 날짜로 변환
     */
    public static Date lunarToSolar(final Object date) throws Exception {

        ChineseCalendar chineseCal = new ChineseCalendar();
        Calendar cal = Calendar.getInstance();

        String dateStr = DateUtil.asStr(date, DateUtil.PTN_DATE);
        String[] dateStrArr = dateStr.split("-");

        // ChineseCalendar에 날짜 세팅
        chineseCal.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(dateStrArr[0]) + 2637);
        chineseCal.set(ChineseCalendar.MONTH, Integer.parseInt(dateStrArr[1]) - 1);
        chineseCal.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(dateStrArr[2]));

        // calendar로 변환 후 날짜 추출
        cal.setTimeInMillis(chineseCal.getTimeInMillis());

        return cal.getTime();
    }

    /**
     * 음력 날짜를 양력 날짜 문자열로 변환
     */
    public static String lunarToSolarStr(
            final Object date,
            final String dtFormat
    ) throws Exception {
        return DateUtil.asStr(lunarToSolar(date), dtFormat);
    }

}