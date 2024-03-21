package io.nicheblog.dreamdiary.global.util;

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
        Date searchDate = DateUtils.asDate(paramDate);
        if (searchDate == null) return null;
        LocalDateTime startOfDay = DateUtils.asLocalDateTime(searchDate)
                                            .with(LocalTime.MIN);
        return DateUtils.localDateTimeToDate(startOfDay);
    }

    /**
     * 해당날짜의 시작시간(ex:2021-09-15 00:00:00) 문자열 반환
     */
    public static String sDateParseStr(
            final Object paramDate,
            final String dtFormat
    ) throws Exception {
        Date sDate = sDateParse(paramDate);
        return DateUtils.dateToStr(sDate, dtFormat);
    }

    /**
     * 해당날짜의 시작시간(ex:2021-09-15 00:00:00) 문자열 반환
     */
    public static String sDateParseStr(final Object paramDate) throws Exception {
        Date sDate = sDateParse(paramDate);
        return DateUtils.dateToStr(sDate, DateUtils.PTN_DATETIME);
    }

    /**
     * 해당날짜의 끝 시간(ex:2021-09-15 23:59:59) 반환
     */
    public static Date eDateParse(final Object paramDate) throws Exception {
        Date searchDate = DateUtils.asDate(paramDate);
        if (searchDate == null) return null;
        LocalDateTime endOfDay = DateUtils.asLocalDateTime(searchDate)
                                          .with(LocalTime.MAX);
        return DateUtils.localDateTimeToDate(endOfDay);
    }

    /**
     * 해당날짜의 끝 시간(ex:2021-09-15 23:59:59) 문자열 반환
     */
    public static String eDateParseStr(
            final Object paramDate,
            final String dtFormat
    ) throws Exception {
        Date eDate = eDateParse(paramDate);
        return DateUtils.dateToStr(eDate, dtFormat);
    }

    /**
     * 해당날짜의 끝 시간(ex:2021-09-15 23:59:59) 문자열 반환
     */
    public static String eDateParseStr(final Object paramDate) throws Exception {
        Date eDate = eDateParse(paramDate);
        return DateUtils.dateToStr(eDate, DateUtils.PTN_DATETIME);
    }

    /**
     * 해당날짜의 24시간 전 반환
     */
    public static Date bfDateParse(final Object paramDate) throws Exception {
        Date searchDate = DateUtils.asDate(paramDate);
        if (searchDate == null) return null;
        searchDate = DateUtils.getDateAddDay(searchDate, -1);
        if (searchDate == null) return null;
        LocalDateTime endOfDay = DateUtils.asLocalDateTime(searchDate)
                                          .with(LocalTime.MAX);
        return DateUtils.localDateTimeToDate(endOfDay);
    }
}