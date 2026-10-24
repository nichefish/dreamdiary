package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.util.Objects;

/** 최신 상태 확인·슬롯 확보·단일 외부 발송·receipt 확인을 연결한다. */
@Service
@Log4j2
public class KeepReminderDispatcher {
    private final KeepReminderRepository repository;
    private final KeepPushGateway gateway;
    private final Clock clock;

    @Autowired
    public KeepReminderDispatcher(KeepReminderRepository repository, KeepPushGateway gateway) {
        this(repository, gateway, Clock.systemUTC());
    }

    /** 실제 시간과 분 경계를 가상 시계로 검증한다. */
    KeepReminderDispatcher(KeepReminderRepository repository, KeepPushGateway gateway, Clock clock) {
        this.repository = repository; this.gateway = gateway; this.clock = clock;
    }

    /** 활성 기기를 순회한다. 한 기기의 실패는 다른 기기의 판정을 중단하지 않는다. */
    public void dispatch() {
        for (var device : repository.devices()) {
            try { dispatch(device); }
            catch (Exception e) { log.error("Keep reminder device failed. deviceId={}, errorType={}", device.id(), e.getClass().getSimpleName()); }
        }
    }

    private void dispatch(KeepReminderRepository.Device device) {
        var optional = KeepReminderPolicy.window(clock.instant(), device.zone());
        if (optional.isEmpty()) return;
        var window = optional.get();
        if (KeepReminderPolicy.select(repository.candidates(device.username(), window.date()), window).isEmpty()) return;
        if (!repository.claim(device, window)) {
            log.debug("Keep reminder duplicate skipped. deviceId={}, slot={}", device.id(), window.slot());
            return;
        }
        try {
            var fresh = repository.device(device.id(), device.username());
            if (fresh.isEmpty() || !Objects.equals(device.zone(), fresh.get().zone())
                    || !Objects.equals(device.token(), fresh.get().token())
                    || !KeepReminderPolicy.window(clock.instant(), device.zone()).filter(window::equals).isPresent()) {
                repository.finish(device, window, "SKIPPED", null, "DEVICE_OR_WINDOW_CHANGED");
                log.info("Keep reminder skipped after latest device check. deviceId={}", device.id());
                return;
            }
            var items = KeepReminderPolicy.select(repository.candidates(device.username(), window.date()), window);
            if (KeepReminderPolicy.window(clock.instant(), device.zone()).filter(window::equals).isEmpty()) {
                repository.finish(device, window, "SKIPPED", null, "WINDOW_CLOSED");
                log.info("Keep reminder skipped at closed minute boundary. deviceId={}", device.id());
                return;
            }
            if (items.isEmpty()) {
                repository.finish(device, window, "SKIPPED", null, "NO_ACTIVE_TARGET");
                log.info("Keep reminder skipped after latest state check. deviceId={}", device.id());
                return;
            }
            var result = gateway.send(device.token(), window, items);
            repository.finish(device, window, result.status(), result.ticketId(), result.code());
            if ("DeviceNotRegistered".equals(result.code())) {
                repository.invalidate(device.id(), device.username(), KeepReminderRepository.tokenHash(device.token()));
            }
            log.info("Keep reminder attempted. deviceId={}, slot={}, count={}, status={}, code={}",
                    device.id(), window.slot(), items.size(), result.status(), result.code());
        } catch (Exception e) {
            repository.finish(device, window, "UNKNOWN", null, "ATTEMPT_UNCONFIRMED");
            log.error("Keep reminder attempt unknown. deviceId={}, errorType={}", device.id(), e.getClass().getSimpleName());
        }
    }

    /** 전달 서비스 인계 결과를 확인한다. 이미 요청한 슬롯을 다시 발송하지 않는다. */
    public void receipts() {
        int expired = repository.expireUnknown(clock.instant());
        if (expired > 0) log.warn("Keep reminder unresolved attempts closed. count={}", expired);
        var pending = repository.pendingReceipts(clock.instant());
        var results = gateway.receipts(pending.stream().map(KeepReminderRepository.Ticket::ticketId).toList());
        for (var ticket : pending) {
            var result = results.get(ticket.ticketId());
            if (result == null) continue;
            repository.receipt(ticket, result.status(), result.code());
            log.info("Keep reminder receipt checked. deviceId={}, status={}, code={}", ticket.deviceId(), result.status(), result.code());
        }
    }
}