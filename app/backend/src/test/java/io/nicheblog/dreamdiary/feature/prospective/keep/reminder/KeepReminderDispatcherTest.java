package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.mockito.Mockito.*;

/** 가상 소유자·기기·날짜로 발송 직전 재조회와 중복·불명 응답 경계를 검증한다. */
class KeepReminderDispatcherTest {
    private static final String FIXTURE_OWNER = "Alice";
    private static final String FIXTURE_DEVICE = "11111111-1111-4111-8111-111111111111";
    private static final String FIXTURE_TOKEN = "ExpoPushToken[sample_token]";
    private static final String FIXTURE_TICKET = "sample-ticket";
    private static final LocalDate FIXTURE_DAY = LocalDate.of(2001, 10, 24);
    private static final KeepReminderRepository.Device FIXTURE_TARGET =
            new KeepReminderRepository.Device(FIXTURE_DEVICE, FIXTURE_OWNER, FIXTURE_TOKEN, "Asia/Seoul");
    private static final KeepReminderPolicy.Window FIXTURE_WINDOW =
            new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.MORNING);
    private static final List<KeepReminderPolicy.Item> FIXTURE_ITEMS =
            List.of(new KeepReminderPolicy.Item(101, FIXTURE_DAY, false));
    private KeepReminderRepository repo;
    private KeepPushGateway gateway;
    private KeepReminderDispatcher dispatcher;

    @BeforeEach void setup() {
        repo = mock(KeepReminderRepository.class); gateway = mock(KeepPushGateway.class);
        dispatcher = new KeepReminderDispatcher(repo, gateway, Clock.fixed(Instant.parse("2001-10-24T00:00:30Z"), ZoneOffset.UTC));
        when(repo.devices()).thenReturn(List.of(FIXTURE_TARGET));
        when(repo.candidates(FIXTURE_OWNER, FIXTURE_DAY)).thenReturn(FIXTURE_ITEMS);
        when(repo.claim(FIXTURE_TARGET, FIXTURE_WINDOW)).thenReturn(true);
        when(repo.device(FIXTURE_DEVICE, FIXTURE_OWNER)).thenReturn(Optional.of(FIXTURE_TARGET));
        when(gateway.send(anyString(), any(), anyList())).thenReturn(new KeepPushGateway.Result("ACCEPTED", FIXTURE_TICKET, null));
    }

    @Test void latestStateIsReadAfterClaimAndOneSummarySent() {
        dispatcher.dispatch();
        var order = inOrder(repo, gateway);
        order.verify(repo).devices();
        order.verify(repo).candidates(FIXTURE_OWNER, FIXTURE_DAY);
        order.verify(repo).claim(FIXTURE_TARGET, FIXTURE_WINDOW);
        order.verify(repo).device(FIXTURE_DEVICE, FIXTURE_OWNER);
        order.verify(repo).candidates(FIXTURE_OWNER, FIXTURE_DAY);
        order.verify(gateway).send(FIXTURE_TOKEN, FIXTURE_WINDOW, FIXTURE_ITEMS);
        order.verify(repo).finish(FIXTURE_TARGET, FIXTURE_WINDOW, "ACCEPTED", FIXTURE_TICKET, null);
    }

    @Test void completionOrDateChangeBeforeSendProducesNoPush() {
        when(repo.candidates(FIXTURE_OWNER, FIXTURE_DAY)).thenReturn(FIXTURE_ITEMS, List.of());
        dispatcher.dispatch();
        verifyNoInteractions(gateway);
        verify(repo).finish(FIXTURE_TARGET, FIXTURE_WINDOW, "SKIPPED", null, "NO_ACTIVE_TARGET");
    }

    @Test void tokenLogoutOrTimezoneChangeBeforeSendProducesNoPush() {
        when(repo.device(FIXTURE_DEVICE, FIXTURE_OWNER)).thenReturn(Optional.empty());
        dispatcher.dispatch();
        verifyNoInteractions(gateway);
        verify(repo).finish(FIXTURE_TARGET, FIXTURE_WINDOW, "SKIPPED", null, "DEVICE_OR_WINDOW_CHANGED");
    }

    @Test void concurrentSlotClaimDoesNotSendAgain() {
        when(repo.claim(FIXTURE_TARGET, FIXTURE_WINDOW)).thenReturn(true, false);
        dispatcher.dispatch(); dispatcher.dispatch();
        verify(gateway, times(1)).send(anyString(), any(), anyList());
    }

    @Test void unknownResponseIsRecordedWithoutAutomaticRetry() {
        when(gateway.send(anyString(), any(), anyList())).thenReturn(new KeepPushGateway.Result("UNKNOWN", null, "RESPONSE_UNCONFIRMED"));
        when(repo.claim(FIXTURE_TARGET, FIXTURE_WINDOW)).thenReturn(true, false);
        dispatcher.dispatch(); dispatcher.dispatch();
        verify(gateway, times(1)).send(anyString(), any(), anyList());
        verify(repo).finish(FIXTURE_TARGET, FIXTURE_WINDOW, "UNKNOWN", null, "RESPONSE_UNCONFIRMED");
    }

    @Test void receiptFailureInvalidatesTheAttemptedTokenVersionOnly() {
        var ticket = new KeepReminderRepository.Ticket(FIXTURE_DEVICE, FIXTURE_OWNER, FIXTURE_DAY, "MORNING",
                KeepReminderRepository.tokenHash(FIXTURE_TOKEN), FIXTURE_TICKET);
        when(repo.pendingReceipts(any())).thenReturn(List.of(ticket));
        when(gateway.receipts(List.of(FIXTURE_TICKET))).thenReturn(Map.of(FIXTURE_TICKET, new KeepPushGateway.Result("REJECTED", null, "DeviceNotRegistered")));
        dispatcher.receipts();
        verify(repo).receipt(ticket, "REJECTED", "DeviceNotRegistered");
        verify(gateway, never()).send(anyString(), any(), anyList());
    }

    @Test void slowLatestLookupCannotSendAfterTheSlotMinute() {
        Clock progressing = mock(Clock.class);
        when(progressing.instant()).thenReturn(Instant.parse("2001-10-24T00:00:30Z"),
                Instant.parse("2001-10-24T00:00:40Z"), Instant.parse("2001-10-24T00:01:00Z"));
        new KeepReminderDispatcher(repo, gateway, progressing).dispatch();
        verifyNoInteractions(gateway);
        verify(repo).finish(FIXTURE_TARGET, FIXTURE_WINDOW, "SKIPPED", null, "WINDOW_CLOSED");
    }
}