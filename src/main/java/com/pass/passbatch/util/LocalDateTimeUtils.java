package com.pass.passbatch.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class LocalDateTimeUtils {

    /**
     * 날짜와 시간을 나타내는 포맷: yyyy-MM-dd HH:mm
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 날짜를 나타내는 포맷: yyyyMMdd
     */
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * LocalDateTime을 지정된 포맷으로 변환합니다.
     *
     * @param localDateTime 변환할 LocalDateTime 객체
     * @return 지정된 포맷으로 변환된 문자열
     */
    public static String format(final LocalDateTime localDateTime) {
        return localDateTime.format(YYYY_MM_DD_HH_MM);
    }

    /**
     * LocalDateTime을 지정된 포맷으로 변환합니다.
     *
     * @param localDateTime 변환할 LocalDateTime 객체
     * @param formatter    사용할 포맷터
     * @return 지정된 포맷으로 변환된 문자열
     */
    public static String format(final LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    /**
     * 문자열을 LocalDateTime 객체로 파싱합니다.
     *
     * @param localDateTimeString 파싱할 문자열
     * @return 파싱된 LocalDateTime 객체
     * @throws "DateTimeParseException" 문자열이 올바른 포맷이 아닌 경우 발생
     */
    public static LocalDateTime parse(final String localDateTimeString) {
        if (StringUtils.isBlank(localDateTimeString)) {
            return null;
        }
        return LocalDateTime.parse(localDateTimeString, YYYY_MM_DD_HH_MM);
    }

    /**
     * LocalDateTime 객체에서 주차를 구합니다.
     *
     * @param localDateTime 주차를 구할 LocalDateTime 객체
     * @return 주차(1 ~ 53)
     */
    public static int getWeekOfYear(final LocalDateTime localDateTime) {
        return localDateTime.get(WeekFields.of(Locale.KOREA).weekOfYear());
    }
}
