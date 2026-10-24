package io.nicheblog.dreamdiary.infrastructure.log.p6spy;

import com.p6spy.engine.logging.Category;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 가상 Expo 토큰으로 운영 드라이버의 SQL·예외 로그에 자격정보가 출력되지 않는지 검증한다. */
class P6SpyCredentialLogTest {
    private static final String FIXTURE_TOKEN = "ExpoPushToken[sample_token]";

    @Test void credentialTableSqlIsDroppedBeforeFormattingEvenForErrors() {
        var log = mock(Logger.class);
        var logger = new P6SpySlf4JLogger(log);
        logger.logSQL(1, "2001-10-24", 0, Category.ERROR,
                "INSERT INTO keep_reminder_device(expo_token) VALUES(?)",
                "INSERT INTO keep_reminder_device(expo_token) VALUES('" + FIXTURE_TOKEN + "')", "");
        verifyNoInteractions(log);
        assertTrue(P6SpySlf4JLogger.isKeepCredentialSql("SELECT * FROM KEEP_REMINDER_DEVICE"));
        assertFalse(P6SpySlf4JLogger.isKeepCredentialSql("SELECT * FROM keep_reminder_delivery"));
    }

    @Test void tokenBearingExceptionAndTextDoNotPrintRawValues() {
        var log = mock(Logger.class);
        var logger = new P6SpySlf4JLogger(log);
        var error = new RuntimeException("가상 DB 예외", new RuntimeException(FIXTURE_TOKEN));
        logger.logException(error);
        verify(log).info("푸시 자격정보가 포함된 SQL 예외 원문을 제외했습니다.");
        verify(log, never()).info("", error);
        logger.logText(FIXTURE_TOKEN);
        verify(log).info("푸시 자격정보가 포함된 로그 원문을 제외했습니다.");
    }

    @Test void unrelatedTextKeepsItsLogContract() {
        var log = mock(Logger.class);
        new P6SpySlf4JLogger(log).logText("샘플 일반 로그");
        verify(log).info("샘플 일반 로그");
    }
}