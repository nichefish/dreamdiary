package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 개인 데이터와 분리된 H2 MySQL 모드에서 JDBC 소유 경계·최신 상태·발송 유일키를 검증한다.
 * MariaDB 전용 문자 collation·테이블 옵션은 메모리 테스트에서 제외하며 배포 SQL의 MariaDB 적용 검증과 구분한다.
 */
public class KeepReminderRepositoryTest {
    private static final String FIXTURE_OWNER = "Alice";
    private static final String FIXTURE_OTHER = "Bob";
    private static final String FIXTURE_DEVICE = "11111111-1111-4111-8111-111111111111";
    private static final String FIXTURE_OTHER_DEVICE = "22222222-2222-4222-8222-222222222222";
    private static final String FIXTURE_TOKEN = "ExpoPushToken[sample_token]";
    private static final String FIXTURE_NEW_TOKEN = "ExpoPushToken[sample_new_token]";
    private static final LocalDate FIXTURE_DAY = LocalDate.of(2001, 10, 24);
    private static final Instant FIXTURE_TIME = Instant.parse("2001-10-24T00:00:00Z");
    private JdbcTemplate jdbc;
    private KeepReminderRepository repository;

    /** 테스트 DB UTC 함수는 가상 시각을 사용한다. */
    public static Timestamp utcTimestamp(int precision) { return Timestamp.valueOf("2001-10-24 00:00:00"); }

    @BeforeEach void setup() throws Exception {
        var source = new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1", "sa", "");
        jdbc = new JdbcTemplate(source);
        jdbc.execute("CREATE ALIAS UTC_TIMESTAMP FOR 'io.nicheblog.dreamdiary.feature.prospective.keep.reminder.KeepReminderRepositoryTest.utcTimestamp'");
        try (var input = getClass().getResourceAsStream("/schema/migration/mariadb/V0.30.17__create_keep_reminder_push.sql")) {
            assertNotNull(input);
            String schema = new String(input.readAllBytes(), StandardCharsets.UTF_8)
                    .replace(" CHARACTER SET ascii COLLATE ascii_bin", "").replace(" DEFAULT CHARSET=utf8mb4", "")
                    .replaceAll(" COMMENT='[^']*';", ";");
            for (String statement : schema.split(";")) if (!statement.isBlank()) jdbc.execute(statement);
        }
        jdbc.execute("CREATE TABLE user(username VARCHAR(20), deleted_at TIMESTAMP)");
        jdbc.execute("CREATE TABLE prospective_keep(id INT PRIMARY KEY, created_by VARCHAR(20), expire_dt DATE, deleted_at TIMESTAMP)");
        jdbc.execute("CREATE TABLE lifecycle(ref_id INT, ref_content_type VARCHAR(30), lifecycle_key VARCHAR(32), deleted_at TIMESTAMP)");
        jdbc.execute("CREATE TABLE state(ref_id INT, ref_content_type VARCHAR(30), state_key VARCHAR(64), deleted_at TIMESTAMP)");
        jdbc.update("INSERT INTO user(username) VALUES(?),(?)", FIXTURE_OWNER, FIXTURE_OTHER);
        repository = new KeepReminderRepository(source);
    }

    @Test void registrationIsIdempotentAndOtherActiveOwnerCannotTakeDevice() {
        repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_TOKEN, "Asia/Seoul");
        repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_TOKEN, "America/New_York");
        assertEquals(1, repository.devices().size());
        assertEquals("America/New_York", repository.devices().get(0).zone());
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> repository.register(FIXTURE_OTHER, FIXTURE_DEVICE, FIXTURE_TOKEN, "UTC")).getRawStatusCode());
        assertFalse(repository.unregister(FIXTURE_OTHER, FIXTURE_DEVICE));
        assertEquals(1, repository.devices().size());
        assertTrue(repository.unregister(FIXTURE_OWNER, FIXTURE_DEVICE));
        repository.register(FIXTURE_OTHER, FIXTURE_DEVICE, FIXTURE_TOKEN, "UTC");
        assertEquals(FIXTURE_OTHER, repository.devices().get(0).username());
    }

    @Test void malformedRegistrationAndDuplicateTokenProduceNoAdditionalDevice() {
        assertThrows(ResponseStatusException.class, () -> repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, "bad", "UTC"));
        assertThrows(ResponseStatusException.class, () -> repository.unregister(FIXTURE_OWNER, "bad-id"));
        assertThrows(ResponseStatusException.class, () -> repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_TOKEN, "+09:00"));
        repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_TOKEN, "UTC");
        assertThrows(ResponseStatusException.class, () -> repository.register(FIXTURE_OWNER, FIXTURE_OTHER_DEVICE, FIXTURE_TOKEN, "UTC"));
        assertEquals(1, repository.devices().size());
    }

    @Test void latestQueryExcludesEveryNonOpenStateDeletedRecordsAndOtherOwner() {
        jdbc.update("INSERT INTO prospective_keep VALUES(101,?,?,NULL)", FIXTURE_OWNER, java.sql.Date.valueOf(FIXTURE_DAY));
        jdbc.update("INSERT INTO prospective_keep VALUES(102,?,?,NULL)", FIXTURE_OTHER, java.sql.Date.valueOf(FIXTURE_DAY));
        assertEquals(List.of(101), repository.candidates(FIXTURE_OWNER, FIXTURE_DAY).stream().map(KeepReminderPolicy.Item::id).toList());
        for (String state : List.of("PENDING", "RESOLVED", "FAILED", "EXPIRED", "DROPPED")) {
            jdbc.update("DELETE FROM lifecycle");
            jdbc.update("INSERT INTO lifecycle VALUES(101,'PROSPECTIVE_KEEP',?,NULL)", state);
            assertTrue(repository.candidates(FIXTURE_OWNER, FIXTURE_DAY).isEmpty(), state);
        }
        jdbc.update("DELETE FROM lifecycle");
        jdbc.update("INSERT INTO state VALUES(101,'PROSPECTIVE_KEEP','IMPRTC',NULL)");
        assertTrue(repository.candidates(FIXTURE_OWNER, FIXTURE_DAY).get(0).important());
        jdbc.update("UPDATE prospective_keep SET deleted_at=CURRENT_TIMESTAMP WHERE id=101");
        assertTrue(repository.candidates(FIXTURE_OWNER, FIXTURE_DAY).isEmpty());
    }

    @Test void parallelClaimsPermitOnlyOneAttemptForEachDeviceDayAndSlot() throws Exception {
        repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_TOKEN, "Asia/Seoul");
        var device = repository.devices().get(0);
        var morning = new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.MORNING);
        var pool = Executors.newFixedThreadPool(2);
        try {
            var tasks = pool.invokeAll(List.of(() -> repository.claim(device, morning), () -> repository.claim(device, morning)));
            long count = 0;
            for (var task : tasks) if ((Boolean) task.get()) count++;
            assertEquals(1, count);
        } finally { pool.shutdownNow(); }
        assertTrue(repository.claim(device, new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.EVENING)));
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM keep_reminder_delivery", Integer.class));
    }

    @Test void expiredReceiptCannotDisableReplacementToken() {
        repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_TOKEN, "UTC");
        var device = repository.devices().get(0);
        var morning = new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.MORNING);
        repository.claim(device, morning);
        repository.finish(device, morning, "ACCEPTED", "sample-ticket", null);
        var pending = repository.pendingReceipts(FIXTURE_TIME.plusSeconds(901));
        assertEquals(1, pending.size());
        repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_NEW_TOKEN, "UTC");
        repository.receipt(pending.get(0), "REJECTED", "DeviceNotRegistered");
        assertEquals(FIXTURE_NEW_TOKEN, repository.devices().get(0).token());
        repository.invalidate(FIXTURE_DEVICE, FIXTURE_OWNER, KeepReminderRepository.tokenHash(FIXTURE_NEW_TOKEN));
        assertTrue(repository.devices().isEmpty());
    }

    @Test void unresolvedAttemptBecomesUnknownAndDeletedAccountIsExcluded() {
        repository.register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_TOKEN, "UTC");
        var device = repository.devices().get(0);
        var window = new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.MORNING);
        repository.claim(device, window);
        assertEquals(1, repository.expireUnknown(FIXTURE_TIME.plusSeconds(601)));
        assertFalse(repository.claim(device, window));
        assertEquals("UNKNOWN", jdbc.queryForObject("SELECT status FROM keep_reminder_delivery", String.class));
        jdbc.update("UPDATE user SET deleted_at=CURRENT_TIMESTAMP WHERE username=?", FIXTURE_OWNER);
        assertTrue(repository.devices().isEmpty());
    }
}