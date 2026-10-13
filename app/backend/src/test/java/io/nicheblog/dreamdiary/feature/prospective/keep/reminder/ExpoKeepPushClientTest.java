package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/** Expo의 가상 ticket·receipt로 외부 접수와 기기 전달 보장 경계를 검증한다. */
class ExpoKeepPushClientTest {
    private static final String FIXTURE_TOKEN = "ExpoPushToken[sample_token]";
    private static final String FIXTURE_TICKET = "sample-ticket";
    private static final LocalDate FIXTURE_DAY = LocalDate.of(2001, 10, 24);
    private static final KeepReminderPolicy.Window FIXTURE_WINDOW = new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.MORNING);
    private static final List<KeepReminderPolicy.Item> FIXTURE_ITEMS = List.of(new KeepReminderPolicy.Item(101, FIXTURE_DAY, false));

    @Test void summaryPayloadContainsCountsAndSlotWithoutPersonalContent() {
        var http = new RestTemplate(); var server = MockRestServiceServer.createServer(http);
        server.expect(requestTo(ExpoKeepPushClient.SEND_URL)).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.to").value(FIXTURE_TOKEN))
                .andExpect(jsonPath("$.ttl").value(60))
                .andExpect(jsonPath("$.body").value("오늘 기한 1개 · 내일 0개 · 3일 후 0개"))
                .andExpect(jsonPath("$.data.kind").value("keep-reminder"))
                .andExpect(jsonPath("$.data.localDate").value(FIXTURE_DAY.toString()))
                .andRespond(withSuccess("{\"data\":{\"status\":\"ok\",\"id\":\"sample-ticket\"}}", MediaType.APPLICATION_JSON));
        var result = new ExpoKeepPushClient(http, new ObjectMapper(), "").send(FIXTURE_TOKEN, FIXTURE_WINDOW, FIXTURE_ITEMS);
        assertEquals("ACCEPTED", result.status()); assertEquals(FIXTURE_TICKET, result.ticketId());
        server.verify();
    }

    @Test void lostOrInvalidResponseIsUnknownAndNotRetried() {
        var http = new RestTemplate(); var server = MockRestServiceServer.createServer(http);
        server.expect(requestTo(ExpoKeepPushClient.SEND_URL)).andRespond(withServerError());
        assertEquals("UNKNOWN", new ExpoKeepPushClient(http, new ObjectMapper(), "").send(FIXTURE_TOKEN, FIXTURE_WINDOW, FIXTURE_ITEMS).status());
        server.verify();
    }

    @Test void receiptsDistinguishHandoffFromInvalidDevice() {
        var http = new RestTemplate(); var server = MockRestServiceServer.createServer(http);
        server.expect(requestTo(ExpoKeepPushClient.RECEIPT_URL))
                .andRespond(withSuccess("{\"data\":{\"sample-ticket\":{\"status\":\"error\",\"details\":{\"error\":\"DeviceNotRegistered\"}}}}", MediaType.APPLICATION_JSON));
        var result = new ExpoKeepPushClient(http, new ObjectMapper(), "").receipts(List.of(FIXTURE_TICKET)).get(FIXTURE_TICKET);
        assertEquals("REJECTED", result.status()); assertEquals("DeviceNotRegistered", result.code()); server.verify();
    }
}