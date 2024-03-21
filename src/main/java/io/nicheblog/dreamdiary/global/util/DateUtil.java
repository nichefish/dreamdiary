package io.nicheblog.dreamdiary.global.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * DateUtil
 * <pre>
 *  날짜 연산 관련 로직 처리 유틸리티 모듈
 *  기존 apache 함수도 다 쓰기 위해 commons.lang3.time.DateUtils 상속
 * </pre>
 *
 * @author nichefish
 * @extends DateUtils
 */
@UtilityClass
public class DateUtil
        extends DateUtils {

    private static final TimeZone seoulTZ = TimeZone.getTimeZone("Asia/Seoul");
    private static final Locale lc = new Locale("ko", "KR");

    public static final String PTN_DATE = "yyyy-MM-dd";
    public static final String PTN_DATEDY = "yyyy.MM.dd '('EEE')'";
    public static final String PTN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PTN_LDATETIME = "yyyyy-MM-dd HH:mm:ss";      // 머신러닝측 날짜포맷 실수?커버위해 작성
    public static final String PTN_PDATE = "yyyyMMdd";
    public static final String PTN_PDATETIME = "yyyyMMddHHmmss";
    public static final String PTN_MDATETIME = "yyyyMMddHHmm";
    public static final String PTN_ZDATETIME = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PTN_DATETIMES = "yyyy-MM-dd HH:mm:ss.S";
    public static final String PTN_SDATE = "yyyy/MM/dd";
    public static final String PTN_SDATETIME = "yyyy/MM/dd HH:mm:ss";
    public static final String PTN_BRTHDY = "MM월 dd일";

    public static final String[] parsePattern = new String[]{PTN_DATE, PTN_DATEDY, PTN_DATETIME, PTN_LDATETIME, PTN_PDATE, PTN_PDATETIME, PTN_MDATETIME, PTN_ZDATETIME, PTN_DATETIMES, PTN_SDATE, PTN_SDATETIME, PTN_BRTHDY};

    public static final SimpleDateFormat dfDate = new SimpleDateFormat(PTN_DATE, lc);
    public static final SimpleDateFormat dfDatetime = new SimpleDateFormat(PTN_DATETIME, lc);
    public static final SimpleDateFormat dfPdate = new SimpleDateFormat(PTN_PDATE, lc);
    public static final SimpleDateFormat dfPdatetime = new SimpleDateFormat(PTN_PDATETIME, lc);
    public static final SimpleDateFormat dfZdatetime = new SimpleDateFormat(PTN_ZDATETIME, lc);
    public static final SimpleDateFormat dfMdatetime = new SimpleDateFormat(PTN_MDATETIME, lc);
    public static final SimpleDateFormat dfDateDy = new SimpleDateFormat(PTN_DATEDY, lc);
    public static final SimpleDateFormat dfSdate = new SimpleDateFormat(PTN_SDATE, lc);
    public static final SimpleDateFormat dfSdatetime = new SimpleDateFormat(PTN_SDATETIME, lc);

    /**
     * 날짜Date를 문자열String로 변환
     */
    public static String dateToStr(
            final Date date,
            final String dtFormat
    ) {
        if (date == null) return "";
        SimpleDateFormat df = (dtFormat != null) ? new SimpleDateFormat(dtFormat, lc) : dfDatetime;
        return df.format(date);
    }

    /**
     * 문자열String을 날짜Date로 변환 :: 정의된 패턴 전부 체크
     */
    public static Date strToDate(final String dateStrParam) throws Exception {
        if (StringUtils.isEmpty(dateStrParam)) return null;
        // microsecond 포함/미포함이 섞여서 넘어오는 문제 해결 위해 microsecond 미사용 처리
        String dateStr = (dateStrParam.length() > 20) ? dateStrParam.substring(0, 19) : dateStrParam;
        return parseDateStrictly(dateStr, parsePattern);
    }

    /**
     * 문자열String을 날짜Date로 변환
     */
    public static Date strToDate(
            final String dateStrParam,
            final String dtFormat
    ) throws Exception {
        if (StringUtils.isEmpty(dateStrParam)) return null;
        // microsecond 포함/미포함이 섞여서 넘어오는 문제 해결 위해 microsecond 미사용 처리
        String dateStr = (dateStrParam.length() > 20) ? dateStrParam.substring(0, 19) : dateStrParam;
        SimpleDateFormat df = (dtFormat != null) ? new SimpleDateFormat(dtFormat, lc) : dfDatetime;
        return df.parse(dateStr);
    }

    /**
     * 현재 날짜Date 반환
     */
    public static Date getCurrDate() throws Exception {
        Calendar today = Calendar.getInstance(seoulTZ, lc);
        return new Date(today.getTimeInMillis());
    }

    /**
     * 현재 날짜LocalDate 반환
     */
    public static LocalDate getCurrLocalDate() throws Exception {
        return asLocalDate(getCurrDate());
    }

    /**
     * 현재 날짜LocalDateTime 반환
     */
    public static LocalDateTime getCurrLocalDateTime() throws Exception {
        return asLocalDateTime(getCurrDate());
    }

    /**
     * 현재 날짜 문자열String 반환
     */
    public static String getCurrDateStr(final String dtFormat) throws Exception {
        if (dtFormat == null) return dfDate.format(getCurrDate());
        return new SimpleDateFormat(dtFormat, lc).format(getCurrDate());
    }

    /**
     * 현재 년도"yyyy"(int) 반환
     */
    public static Integer getCurrYear() throws Exception {
        return Calendar.getInstance(seoulTZ, lc)
                       .get(Calendar.YEAR);
    }

    /**
     * 현재 년도"yyyy" 문자열로 반환
     */
    public static String getCurrYearStr() throws Exception {
        return Integer.toString(getCurrYear());
    }

    /**
     * 현재 월"MM" 인덱스 반환 (1월=0)
     */
    public static Integer getCurrMnthIdx() throws Exception {
        return Calendar.getInstance(seoulTZ, lc)
                       .get(Calendar.MONTH);
    }

    public static Integer getCurrMnth() throws Exception {
        return Calendar.getInstance(seoulTZ, lc)
                       .get(Calendar.MONTH) + 1;
    }

    /**
     * 이전 월"MM" 인덱스 반환 (1월=0)
     */
    public static Integer getPrevMnthIdx() throws Exception {
        int currMnthIdx = getCurrMnthIdx();
        return currMnthIdx == 0 ? 11 : currMnthIdx - 1;
    }

    public static Integer getPrevMnth() throws Exception {
        return getPrevMnthIdx() + 1;
    }

    /**
     * 다음 월"MM" 인덱스 반환 (1월=0)
     */
    public static Integer getNextMnthIdx() throws Exception {
        int currMnthIdx = getCurrMnthIdx();
        return currMnthIdx == 11 ? 0 : currMnthIdx + 1;
    }

    public static Integer getNextMnth() throws Exception {
        return getNextMnthIdx() + 1;
    }

    /**
     * 현재 년도"yyyy"/월"MM" 세트 반환 (인덱스 대신 실제 월로 반환 = 1월=0 -> 1로 변환)
     */
    public static Integer[] getCurrYyMnth() throws Exception {
        return new Integer[]{getCurrYear(), Calendar.getInstance(seoulTZ, lc)
                                                    .get(Calendar.MONTH) + 1};
    }

    /**
     * 현재 년월"yyyyMM" 문자열
     */
    public static String getCurrYyMnthStr() throws Exception {
        Integer[] currYyMnth = getCurrYyMnth();
        return currYyMnth[0] + String.format("%02d", currYyMnth[1]);
    }

    /**
     * 이전달의 년도"yyyy"/월"MM" 세트 반환 (인덱스 대신 실제 월로 반환 = 1월=1)
     */
    public static Integer[] getPrevYyMnth() throws Exception {
        int currYy = getCurrYyMnth()[0];
        int currMnth = getCurrYyMnth()[1];
        int yy = (currMnth == 1) ? currYy - 1 : currYy;
        int prevMnth = currMnth == 1 ? 12 : currMnth - 1;
        return new Integer[]{yy, prevMnth};
    }

    /**
     * 다음달의 년도"yyyy"/월"MM" 세트 반환 (인덱스 대신 실제 월로 반환 = 1월=1)
     */
    public static Integer[] getNextYyMnth() throws Exception {
        int currYy = getCurrYyMnth()[0];
        int currMnth = getCurrYyMnth()[1];
        int yy = (currMnth == 12) ? currYy + 1 : currYy;
        int nextMnth = currMnth == 12 ? 1 : currMnth + 1;
        return new Integer[]{yy, nextMnth};
    }

    /**
     * 어제 날짜Date 반환
     */
    public static Date getPrevDate() throws Exception {
        return getCurrDateAddDay(-1);
    }

    /**
     * 어제 날짜 문자열String 반환
     */
    public static String getPrevDateStr(final String dtFormat) throws Exception {
        if (dtFormat == null) return dfDate.format(getPrevDate());
        return new SimpleDateFormat(dtFormat, lc).format(getPrevDate());
    }

    /**
     * 내일 날짜Date 반환
     */
    public static Date getNextDate() throws Exception {
        return getCurrDateAddDay(1);
    }

    /**
     * 내일 날짜 문자열String 반환
     */
    public static String getNextDateStr(final String dtFormat) throws Exception {
        if (dtFormat == null) return dfDate.format(getNextDate());
        return new SimpleDateFormat(dtFormat, lc).format(getNextDate());
    }

    /**
     * 현재 날짜Date에 기간(일자) 더해서 날짜Date로 반환
     */
    public static Date getCurrDateAddDay(final int dayCnt) throws Exception {
        Calendar today = new GregorianCalendar(seoulTZ, lc);
        return getDateAddDay(today.getTime(), dayCnt);
    }

    /**
     * 현재 날짜Date에 기간(일자) 더해서 문자열String로 반환
     */
    public static String getCurrDateAddDayStr(final int dayCnt) throws Exception {
        return getCurrDateAddDayStr(dayCnt, PTN_DATETIME);
    }

    public static String getCurrDateAddDayStr(
            final int dayCnt,
            final String dtFormat
    ) throws Exception {
        return dateToStr(getCurrDateAddDay(dayCnt), dtFormat);
    }

    /**
     * 날짜Date에 기간(일자) 더해서 날짜Date로 반환
     */
    public static Date getDateAddDay(
            final Object date,
            final int dayCnt
    ) throws Exception {
        Date asDate = asDate(date);
        if (asDate == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(asDate);
        cal.add(Calendar.DATE, dayCnt);        // 일자 더하기
        return cal.getTime();
    }

    /**
     * 날짜Date에 기간(일자) 더해서 문자열String로 반환
     */
    public static String getDateAddDayStr(
            final Object date,
            final String dtFormat,
            final int dayCnt
    ) throws Exception {
        return dateToStr(getDateAddDay(date, dayCnt), dtFormat);
    }

    /**
     * 날짜Date에 기간(년도) 더해서 날짜Date로 반환
     */
    public static Date getDateAddYy(
            final Object date,
            final int yyCnt
    ) throws Exception {
        Date asDate = asDate(date);
        if (asDate == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(asDate);
        cal.add(Calendar.YEAR, yyCnt);        // 일자 더하기
        return cal.getTime();
    }

    /**
     * 날짜Date에 기간(년도) 더해서 문자열로 반환
     */
    public static String getDateAddYyStr(
            final Object date,
            final String dtFormat,
            final int yyCnt
    ) throws Exception {
        return dateToStr(getDateAddYy(date, yyCnt), dtFormat);
    }

    /**
     * 날짜Date에 기간(분) 더해서 날짜Date로 반환
     */
    public static Date addMinutes(
            final Object date,
            final int minCnt
    ) throws Exception {
        Date asDate = asDate(date);
        if (asDate == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(asDate);
        cal.add(Calendar.MINUTE, minCnt);        // 일자 더하기
        return cal.getTime();
    }

    /**
     * Date를 LocalDateTime으로 변환
     */
    public static LocalDateTime asLocalDateTime(final Object date) throws Exception {
        Date asDate = asDate(date);
        return LocalDateTime.ofInstant(asDate.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date를 LocalDate로 변환
     */
    public static LocalDate asLocalDate(final Object date) throws Exception {
        return LocalDate.from(asLocalDateTime(date));
    }

    /**
     * LocalDateTime을 Date로 변환
     */
    public static Date localDateTimeToDate(final LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault())
                                      .toInstant());
    }

    /**
     * 두 날짜 사이의 차이 반환
     */
    public static Long getDateDiff(
            final Object fromDate,
            final Object toDate,
            final TimeUnit timeUnit
    ) throws Exception {
        Date asFromDate = asDate(fromDate);
        Date asToDate = asDate(toDate);
        if (asFromDate == null || asToDate == null) return null;
        long diffInMillies = asToDate.getTime() - asFromDate.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    /**
     * 날짜 받아서 요일(숫자) 반환
     */
    public static Integer getDayOfWeek(final Object date) throws Exception {
        Date asDate = asDate(date);
        if (asDate == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(asDate);
        // "1은 일요일, 7은 토요일을 나타냅니다."
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 날짜 받아서 요일(한글) 반환
     */
    public static String getDayOfWeekStr(final Object date) throws Exception {
        Integer dayOfWeek = getDayOfWeek(date);
        if (dayOfWeek == null) return null;
        switch (dayOfWeek) {
            case 1:
                return "일";
            case 2:
                return "월";
            case 3:
                return "화";
            case 4:
                return "수";
            case 5:
                return "목";
            case 6:
                return "금";
            case 7:
                return "토";
            default:
                return null;
        }
    }

    /**
     * 날짜 받아서 주말여부 반환
     */
    public static Boolean isWeekend(final Object date) throws Exception {
        return Arrays.asList(1, 7)
                     .contains(getDayOfWeek(date));
    }

    /**
     * 두 날짜를 받아서 같은날짜 여부 반환
     */
    public static boolean isSameDay(
            final Object date1,
            final Object date2
    ) throws Exception {
        String date1str = asStr(date1, DateUtil.PTN_PDATE);
        String date2str = asStr(date2, DateUtil.PTN_PDATE);
        return date1str.equals(date2str);
    }

    /**
     * 해당 주의 월요일 구하기
     */
    public static Date getFirstDayOfWeek(final Object date) throws Exception {
        Date asDate = asDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(asDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    public static Date getFirstDayOfCurrWeek() throws Exception {
        Date currDate = getCurrDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    /**
     * 날짜 또는 문자열을 받아서 날짜Date로 일괄 반환
     * Date, 문자열, LocalDateTime
     */
    public static Date asDate(final Object date) throws Exception {
        if (date == null) return null;
        if (date instanceof String) {
            String dateStrParam = date.toString();
            String dateStr = (dateStrParam.length() > 20) ? dateStrParam.substring(0, 19) : dateStrParam;
            return strToDate(dateStr);
        }
        if (date instanceof Date) return (Date) date;
        if (date instanceof LocalDate) return Date.from(((LocalDate) date).atStartOfDay(ZoneId.of("Asia/Seoul"))
                                                                          .toInstant());
        if (date instanceof LocalDateTime) return localDateTimeToDate((LocalDateTime) date);
        return null;
    }

    /**
     * 날짜 또는 문자열을 받아서 문자열로 일괄 반환
     * Date, 문자열, LocalDateTime
     */
    public static String asStr(
            final Object date,
            final String dtFormat
    ) throws Exception {
        if (date == null) return null;
        Date asDate = asDate(date);
        return dateToStr(asDate, dtFormat);
    }

    /**
     * 문자열이 날짜형식인지 체크
     */
    public static Boolean isDateStr(final String dateStr) {
        try {
            asDate(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}