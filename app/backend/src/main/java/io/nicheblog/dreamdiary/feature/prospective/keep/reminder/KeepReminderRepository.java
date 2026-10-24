package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 서버 리마인드의 기기·발송 기록과 최신 Keep을 읽는다.
 * 주 데이터 소스의 JDBC 트랜잭션으로 등록을 직렬화한다. 토큰 테이블 SQL은 P6Spy 로거의 자격정보 제외 계약을 따른다.
 */
@Repository
public class KeepReminderRepository {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;

    /** 토큰은 식별정보 로그에 포함하지 않는 기기 연결 값이다. */
    public record Device(String id, String username, String token, String zone) {}
    /** Expo 접수 이후 전달 결과 확인 대상. 토큰 자체는 저장하지 않는다. */
    public record Ticket(String deviceId, String username, LocalDate day, String slot, String tokenHash, String ticketId) {}

    public KeepReminderRepository(@Qualifier("primaryOriginalDataSource") DataSource source) {
        jdbc = new JdbcTemplate(source);
        transaction = new TransactionTemplate(new DataSourceTransactionManager(source));
    }

    /** 기기 UUID·Expo 토큰·IANA 시간대를 검증하고 활성 소유 경계를 지키며 등록한다. */
    public void register(String username, String id, String token, String zone) {
        try {
            if (!UUID.fromString(id).toString().equals(id)) throw new IllegalArgumentException();
            if (!ZoneId.getAvailableZoneIds().contains(zone)) throw new IllegalArgumentException();
            if (zone.length() > 80 || token == null || !token.matches("(Expo|Exponent)PushToken\\[[A-Za-z0-9_-]{1,200}\\]")) {
                throw new IllegalArgumentException();
            }
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "기기 ID·Expo 토큰·시간대를 확인하세요.");
        }
        try {
            transaction.executeWithoutResult(status -> {
                List<java.util.Map<String, Object>> rows = jdbc.queryForList(
                        "SELECT device_id, username, active FROM keep_reminder_device WHERE device_id=? OR expo_token=? FOR UPDATE", id, token);
                for (var row : rows) {
                    boolean active = ((Number) row.get("active")).intValue() != 0;
                    if (active && !username.equals(row.get("username"))) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "다른 계정의 활성 기기입니다. 먼저 기존 계정에서 알림 등록을 해제하세요.");
                    }
                    if (!id.equals(row.get("device_id"))) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "이 토큰은 다른 기기에 등록돼 있습니다.");
                    }
                }
                int updated = jdbc.update("UPDATE keep_reminder_device SET username=?, expo_token=?, time_zone=?, active=1, updated_at=UTC_TIMESTAMP(6) WHERE device_id=?",
                        username, token, zone, id);
                if (updated == 0) jdbc.update("INSERT INTO keep_reminder_device(device_id, username, expo_token, time_zone, active) VALUES(?,?,?,?,1)",
                        id, username, token, zone);
            });
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "기기 등록이 충돌했습니다. 다시 확인하세요.");
        }
    }

    /** 인증 소유자의 기기만 해제한다. 다른 소유자 기기의 상태는 변경하지 않는다. */
    public boolean unregister(String username, String id) {
        try {
            if (!UUID.fromString(id).toString().equals(id)) throw new IllegalArgumentException();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "기기 ID를 확인하세요.");
        }
        return jdbc.update("UPDATE keep_reminder_device SET active=0, updated_at=UTC_TIMESTAMP(6) WHERE device_id=? AND username=? AND active=1", id, username) > 0;
    }

    /** 삭제된 계정과 비활성 기기를 발송 열거에서 제외한다. */
    public List<Device> devices() {
        return jdbc.query("SELECT d.* FROM keep_reminder_device d WHERE d.active=1 AND EXISTS (SELECT 1 FROM user u WHERE u.username=d.username AND u.deleted_at IS NULL)",
                (rs, n) -> new Device(rs.getString("device_id"), rs.getString("username"), rs.getString("expo_token"), rs.getString("time_zone")));
    }

    /** 발송 직전 같은 계정의 현재 활성 기기를 다시 확인한다. */
    public Optional<Device> device(String id, String username) {
        return devicesById(id, username).stream().findFirst();
    }

    private List<Device> devicesById(String id, String username) {
        return jdbc.query("SELECT d.* FROM keep_reminder_device d WHERE d.device_id=? AND d.username=? AND d.active=1 AND EXISTS (SELECT 1 FROM user u WHERE u.username=d.username AND u.deleted_at IS NULL)",
                (rs, n) -> new Device(rs.getString("device_id"), rs.getString("username"), rs.getString("expo_token"), rs.getString("time_zone")), id, username);
    }

    /** 소유·삭제·OPEN·기한·중요를 원천 테이블에서 읽는다. lifecycle 행 부재는 OPEN이다. */
    public List<KeepReminderPolicy.Item> candidates(String username, LocalDate day) {
        return jdbc.query("""
                SELECT k.id, k.expire_dt,
                  EXISTS(SELECT 1 FROM state s WHERE s.ref_id=k.id AND s.ref_content_type='PROSPECTIVE_KEEP'
                    AND s.state_key='IMPRTC' AND s.deleted_at IS NULL) AS important
                FROM prospective_keep k
                LEFT JOIN lifecycle l ON l.ref_id=k.id AND l.ref_content_type='PROSPECTIVE_KEEP' AND l.deleted_at IS NULL
                WHERE k.created_by=? AND k.deleted_at IS NULL AND k.expire_dt BETWEEN ? AND ?
                  AND COALESCE(l.lifecycle_key,'OPEN')='OPEN'
                ORDER BY k.expire_dt,k.id
                """, (rs, n) -> new KeepReminderPolicy.Item(rs.getInt("id"), rs.getDate("expire_dt").toLocalDate(), rs.getBoolean("important")),
                username, java.sql.Date.valueOf(day), java.sql.Date.valueOf(day.plusDays(3)));
    }

    /** 사용자·기기·로컬 날짜·슬롯별 한 번의 외부 발송 시도를 확보한다. 충돌은 정상 중복 차단이다. */
    public boolean claim(Device device, KeepReminderPolicy.Window window) {
        try {
            jdbc.update("INSERT INTO keep_reminder_delivery(device_id, username, local_date, slot, token_hash, status) VALUES(?,?,?,?,?,'CLAIMED')",
                    device.id(), device.username(), java.sql.Date.valueOf(window.date()), window.slot().name(), tokenHash(device.token()));
            return true;
        } catch (DuplicateKeyException e) { return false; }
    }

    /** 전송·확인 상태는 기기와 슬롯 유일키로 갱신한다. Keep 상태는 변경하지 않는다. */
    public void finish(Device device, KeepReminderPolicy.Window window, String state, String ticketId, String code) {
        jdbc.update("UPDATE keep_reminder_delivery SET status=?, ticket_id=?, error_code=?, updated_at=UTC_TIMESTAMP(6) WHERE device_id=? AND username=? AND local_date=? AND slot=?",
                state, ticketId, code, device.id(), device.username(), java.sql.Date.valueOf(window.date()), window.slot().name());
    }

    /** 15분 지난 Expo 접수 건의 receipt를 확인한다. 조회 배치는 100건 이내다. */
    public List<Ticket> pendingReceipts(Instant now) {
        return jdbc.query("SELECT * FROM keep_reminder_delivery WHERE status='ACCEPTED' AND created_at<=? ORDER BY created_at LIMIT 100",
                (rs, n) -> new Ticket(rs.getString("device_id"), rs.getString("username"), rs.getDate("local_date").toLocalDate(),
                        rs.getString("slot"), rs.getString("token_hash"), rs.getString("ticket_id")),
                LocalDateTime.ofInstant(now.minusSeconds(900), ZoneOffset.UTC));
    }

    /** 접수 후 하루 이상 미확인 건과 발송 시도 중 서버가 중단된 건은 불명 상태로 종결한다. */
    public int expireUnknown(Instant now) {
        return jdbc.update("""
                UPDATE keep_reminder_delivery SET status='UNKNOWN', error_code='RESULT_UNCONFIRMED', updated_at=UTC_TIMESTAMP(6)
                WHERE (status='ACCEPTED' AND created_at<?) OR (status='CLAIMED' AND created_at<?)
                """, LocalDateTime.ofInstant(now.minusSeconds(86400), ZoneOffset.UTC),
                LocalDateTime.ofInstant(now.minusSeconds(600), ZoneOffset.UTC));
    }

    /** receipt 결과를 기록하며 토큰이 갱신된 기기를 오래된 오류로 비활성화하지 않는다. */
    public void receipt(Ticket ticket, String state, String code) {
        jdbc.update("UPDATE keep_reminder_delivery SET status=?, error_code=?, updated_at=UTC_TIMESTAMP(6) WHERE device_id=? AND username=? AND local_date=? AND slot=? AND status='ACCEPTED'",
                state, code, ticket.deviceId(), ticket.username(), java.sql.Date.valueOf(ticket.day()), ticket.slot());
        if ("DeviceNotRegistered".equals(code)) invalidate(ticket.deviceId(), ticket.username(), ticket.tokenHash());
    }

    /** 확인된 만료 토큰 버전만 비활성화한다. */
    public void invalidate(String id, String username, String hash) {
        device(id, username).filter(d -> Objects.equals(hash, tokenHash(d.token())))
                .ifPresent(d -> jdbc.update("UPDATE keep_reminder_device SET active=0, updated_at=UTC_TIMESTAMP(6) WHERE device_id=? AND username=? AND expo_token=?", id, username, d.token()));
    }

    /** 전달 기록에 토큰 원문 대신 SHA-256 지문을 보관한다. */
    public static String tokenHash(String token) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
}