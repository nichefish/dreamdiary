package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.infrastructure.web.model.AjaxResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepSearchParam;

/** 인증 소유자의 서버 리마인드 수신 기기를 등록·해제하고 최신 알림 대상을 조회한다. 토큰을 응답과 로그에 노출하지 않는다. */
@RestController
@RequiredArgsConstructor
@Log4j2
public class KeepReminderRestController {
    private final KeepReminderRepository repository;
    private final ProspectiveKeepService keepService;

    /** 알림 선택은 현재 인증 소유자의 최신 목록·OPEN/중요/기한으로 같은 서버 정책을 재평가한다. */
    @GetMapping("/api/prospective/keep/reminder-targets")
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> targets(@RequestParam String localDate, @RequestParam String slot) throws Exception {
        final KeepReminderPolicy.Window window;
        try {
            if (!localDate.matches("\\d{4}-\\d{2}-\\d{2}")) throw new IllegalArgumentException();
            LocalDate day = LocalDate.parse(localDate);
            if (day.getYear() < 1 || day.plusDays(3).getYear() > 9999) throw new IllegalArgumentException();
            window = new KeepReminderPolicy.Window(day, KeepReminderPolicy.Slot.valueOf(slot));
        } catch (DateTimeException | IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "알림 날짜와 시간대를 확인하세요.");
        }
        String username = AuthUtils.requireLoginUsername();
        var list = keepService.getListDtoWithLifecycleByUser(username, new ProspectiveKeepSearchParam());
        Set<Integer> ids = KeepReminderPolicy.select(repository.candidates(username, window.date()), window)
                .stream().map(KeepReminderPolicy.Item::id).collect(Collectors.toSet());
        var targets = list.stream().filter(item -> ids.contains(item.getId())).toList();
        log.info("Keep reminder targets queried. slot={}, count={}", window.slot(), targets.size());
        return ResponseEntity.ok(AjaxResponse.withAjaxResult(true, "알림 대상 목록을 조회했습니다.").withList(targets));
    }

    /** 등록 검증·소유 충돌을 해당 HTTP 상태로 반환하고 토큰·입력 본문은 기록하지 않는다. */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<AjaxResponse> invalidRequest(ResponseStatusException error) {
        log.warn("Keep reminder request rejected. status={}", error.getRawStatusCode());
        return ResponseEntity.status(error.getStatus()).body(AjaxResponse.withAjaxResult(false, error.getReason()));
    }

    /** JSON 해석 오류는 토큰을 포함할 수 있는 예외 본문 대신 고정 안내로 반환한다. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AjaxResponse> malformedRequest() {
        log.warn("Keep reminder registration JSON rejected.");
        return ResponseEntity.badRequest().body(AjaxResponse.withAjaxResult(false, "기기 등록 JSON을 확인하세요."));
    }

    /** 앱 설치 UUID와 Expo 토큰·IANA 시간대. 소유자는 인증에서 결정한다. */
    public record DeviceRequest(String deviceId, String expoToken, String timeZone) {}

    @PostMapping("/api/prospective/keep/reminder-devices")
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> register(@RequestBody DeviceRequest request) {
        repository.register(AuthUtils.requireLoginUsername(), request.deviceId(), request.expoToken(), request.timeZone());
        log.info("Keep reminder device registered. deviceId={}", request.deviceId());
        return ResponseEntity.ok(AjaxResponse.withAjaxResult(true, "알림 기기를 등록했습니다.").withObj(Map.of("deviceId", request.deviceId())));
    }

    @DeleteMapping("/api/prospective/keep/reminder-devices/{deviceId}")
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> unregister(@PathVariable String deviceId) {
        boolean changed = repository.unregister(AuthUtils.requireLoginUsername(), deviceId);
        log.info("Keep reminder device unregistered. deviceId={}, changed={}", deviceId, changed);
        return ResponseEntity.ok(AjaxResponse.withAjaxResult(true, "알림 기기를 해제했습니다."));
    }
}