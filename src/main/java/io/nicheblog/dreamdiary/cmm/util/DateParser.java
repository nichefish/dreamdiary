package io.nicheblog.dreamdiary.cmm.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * DateParser
 * <pre>
 *  날짜 정보 관련 처리 유틸리티 모듈
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
public class DateParser {

    /**
     * 해당날짜의 시작시간(ex:2021-09-15 00:00:00) 반환
     */
    public static Date sDateParse(final Object paramDate) throws Exception {
        Date searchDate = DateUtil.asDate(paramDate);
        if (searchDate == null) return null;
        LocalDateTime startOfDay = DateUtil.asLocalDateTime(searchDate)
                                            .with(LocalTime.MIN);
        return DateUtil.localDateTimeToDate(startOfDay);
    }

    /**
     * 해당날짜의 시작시간(ex:2021-09-15 00:00:00) 문자열 반환
     */
    public static String sDateParseStr(
            final Object paramDate,
            final String dtFormat
    ) throws Exception {
        Date sDate = sDateParse(paramDate);
        return DateUtil.dateToStr(sDate, dtFormat);
    }

    /**
     * 해당날짜의 시작시간(ex:2021-09-15 00:00:00) 문자열 반환
     */
    public static String sDateParseStr(final Object paramDate) throws Exception {
        Date sDate = sDateParse(paramDate);
        return DateUtil.dateToStr(sDate, DateUtil.PTN_DATETIME);
    }

    /**
     * 해당날짜의 끝 시간(ex:2021-09-15 23:59:59) 반환
     */
    public static Date eDateParse(final Object paramDate) throws Exception {
        Date searchDate = DateUtil.asDate(paramDate);
        if (searchDate == null) return null;
        LocalDateTime endOfDay = DateUtil.asLocalDateTime(searchDate)
                                          .with(LocalTime.MAX);
        return DateUtil.localDateTimeToDate(endOfDay);
    }

    /**
     * 해당날짜의 끝 시간(ex:2021-09-15 23:59:59) 문자열 반환
     */
    public static String eDateParseStr(
            final Object paramDate,
            final String dtFormat
    ) throws Exception {
        Date eDate = eDateParse(paramDate);
        return DateUtil.dateToStr(eDate, dtFormat);
    }

    /**
     * 해당날짜의 끝 시간(ex:2021-09-15 23:59:59) 문자열 반환
     */
    public static String eDateParseStr(final Object paramDate) throws Exception {
        Date eDate = eDateParse(paramDate);
        return DateUtil.dateToStr(eDate, DateUtil.PTN_DATETIME);
    }

    /**
     * 해당날짜의 24시간 전 반환
     */
    public static Date bfDateParse(final Object paramDate) throws Exception {
        Date searchDate = DateUtil.asDate(paramDate);
        if (searchDate == null) return null;
        searchDate = DateUtil.getDateAddDay(searchDate, -1);
        if (searchDate == null) return null;
        LocalDateTime endOfDay = DateUtil.asLocalDateTime(searchDate)
                                          .with(LocalTime.MAX);
        return DateUtil.localDateTimeToDate(endOfDay);
    }
}