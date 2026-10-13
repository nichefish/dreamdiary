package io.nicheblog.dreamdiary.infrastructure.log.p6spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Locale;

/**
 * p6spy SQL을 Slf4J 로거 {@code p6spy}로 보낸다.
 *
 * <p>기본 statement는 INFO이다. {@link P6SpySqlLogQuietScope} 구간에서는 statement를 DEBUG로 내린다.
 * ERROR/WARN 카테고리와 로거 레벨 자체는 유지한다.</p>
 *
 * <p>Keep 푸시 토큰 테이블 SQL은 메시지 포맷 이전에 제외하고 토큰이 들어 있는 예외·직접 로그는 원문을 출력하지 않는다.</p>
 *
 * @author nichefish
 */
public class P6SpySlf4JLogger extends FormattedLogger {

    private final Logger log;

    /**
     * p6spy가 이름으로 생성하는 기본 생성자.
     */
    public P6SpySlf4JLogger() {
        this(LoggerFactory.getLogger("p6spy"));
    }

    /** 가상 로거로 자격정보 출력 차단을 검증하는 생성자. */
    P6SpySlf4JLogger(Logger log) { this.log = log; }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logException(final Exception e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (containsPushToken(cause.getMessage())) {
                log.info("푸시 자격정보가 포함된 SQL 예외 원문을 제외했습니다.");
                return;
            }
            if (cause.getCause() == cause) break;
        }
        log.info("", e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logText(final String text) {
        if (containsPushToken(text)) { log.info("푸시 자격정보가 포함된 로그 원문을 제외했습니다."); return; }
        if (P6SpySqlLogQuietScope.isQuiet()) {
            log.debug(text);
            return;
        }
        log.info(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logSQL(
            final int connectionId,
            final String now,
            final long elapsed,
            final Category category,
            final String prepared,
            final String sql,
            final String url
    ) {
        final String statement = prepared == null || prepared.isBlank() ? sql : prepared;
        if (isKeepCredentialSql(statement) || containsPushToken(sql)) return;
        final String msg = strategy.formatMessage(
                connectionId, now, elapsed, category.toString(), prepared, sql, url);
        if (Category.ERROR.equals(category)) {
            log.error(msg);
        } else if (Category.WARN.equals(category)) {
            log.warn(msg);
        } else if (Category.DEBUG.equals(category) || shouldLogStatementAtDebug(category)) {
            log.debug(msg);
        } else {
            log.info(msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCategoryEnabled(final Category category) {
        if (Category.ERROR.equals(category)) {
            return log.isErrorEnabled();
        }
        if (Category.WARN.equals(category)) {
            return log.isWarnEnabled();
        }
        if (Category.DEBUG.equals(category) || shouldLogStatementAtDebug(category)) {
            return log.isDebugEnabled();
        }
        return log.isInfoEnabled();
    }

    /**
     * quiet 구간의 statement를 DEBUG로 내릴지 판단한다.
     *
     * @param category p6spy 로그 카테고리
     * @return DEBUG로 내려야 하면 {@code true}
     */
    static boolean shouldLogStatementAtDebug(final Category category) {
        if (!P6SpySqlLogQuietScope.isQuiet()) {
            return false;
        }
        return !Category.ERROR.equals(category) && !Category.WARN.equals(category);
    }

    /** 기기 토큰 테이블의 조회·쓰기·오류 SQL을 포맷·로깅 전에 차단한다. */
    static boolean isKeepCredentialSql(String sql) {
        return sql != null && sql.toLowerCase(Locale.ROOT).matches("(?s).*\\bkeep_reminder_device\\b.*");
    }

    /** 정상 요청에서 사용하는 Expo 토큰 표기를 로그 자격정보로 식별한다. */
    private static boolean containsPushToken(String value) {
        return value != null && (value.contains("ExpoPushToken[") || value.contains("ExponentPushToken["));
    }
}
