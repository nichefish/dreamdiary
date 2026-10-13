package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.time.Duration;
import java.util.*;

/** Expo HTTPS API의 ticket·receipt를 해석한다. 토큰·외부 응답 본문은 로그에 남기지 않는다. */
@Component
@Log4j2
public class ExpoKeepPushClient implements KeepPushGateway {
    static final String SEND_URL = "https://exp.host/--/api/v2/push/send";
    static final String RECEIPT_URL = "https://exp.host/--/api/v2/push/getReceipts";
    private final RestTemplate http;
    private final ObjectMapper mapper;
    private final String accessToken;

    @Autowired
    public ExpoKeepPushClient(ObjectMapper mapper, @Value("${app.keep.reminder.expo-access-token:}") String accessToken) {
        this(new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(3)).setReadTimeout(Duration.ofSeconds(5)).build(), mapper, accessToken);
    }

    /** 외부 요청을 가상 HTTP 서버로 검증할 수 있는 전달 경계 생성자. */
    ExpoKeepPushClient(RestTemplate http, ObjectMapper mapper, String accessToken) {
        this.http = http; this.mapper = mapper; this.accessToken = accessToken;
    }

    /** 개수 요약과 슬롯 식별값만 전달한다. 외부 요청의 자동 재시도는 수행하지 않는다. */
    @Override
    public Result send(String token, KeepReminderPolicy.Window window, List<KeepReminderPolicy.Item> items) {
        Map<String, Object> payload = Map.of(
                "to", token, "sound", "default", "channelId", "keep-reminder", "ttl", 60,
                "title", window.slot() == KeepReminderPolicy.Slot.MORNING ? "Keep 아침 알림" : "Keep 저녁 알림",
                "body", KeepReminderPolicy.summary(items, window),
                "data", Map.of("kind", "keep-reminder", "localDate", window.date().toString(), "slot", window.slot().name()));
        try {
            JsonNode data = mapper.readTree(post(SEND_URL, payload)).path("data");
            if (data.isArray()) data = data.path(0);
            return result(data, true);
        } catch (HttpClientErrorException e) {
            log.warn("Keep push request rejected. httpStatus={}", e.getRawStatusCode());
            return new Result("REJECTED", null, "HTTP_" + e.getRawStatusCode());
        } catch (Exception e) {
            log.warn("Keep push result unknown. errorType={}", e.getClass().getSimpleName());
            return new Result("UNKNOWN", null, "RESPONSE_UNCONFIRMED");
        }
    }

    /** receipt 미생성은 다음 조회에서 다시 확인한다. 잘못된 외부 응답은 상태를 추정하지 않는다. */
    @Override
    public Map<String, Result> receipts(List<String> ids) {
        if (ids.isEmpty()) return Map.of();
        try {
            JsonNode data = mapper.readTree(post(RECEIPT_URL, Map.of("ids", ids))).path("data");
            Map<String, Result> results = new HashMap<>();
            for (String id : ids) {
                if (data.has(id)) results.put(id, result(data.get(id), false));
            }
            return results;
        } catch (Exception e) {
            log.warn("Keep push receipt lookup failed. errorType={}", e.getClass().getSimpleName());
            return Map.of();
        }
    }

    private String post(String url, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null && !accessToken.isBlank()) headers.setBearerAuth(accessToken);
        return http.postForObject(url, new HttpEntity<>(body, headers), String.class);
    }

    private Result result(JsonNode data, boolean ticket) {
        if ("ok".equals(data.path("status").asText())) {
            String id = data.path("id").asText();
            if (ticket && (id.isBlank() || id.length() > 100)) return new Result("UNKNOWN", null, "INVALID_TICKET");
            return new Result(ticket ? "ACCEPTED" : "HANDED_OFF", ticket ? id : null, null);
        }
        if ("error".equals(data.path("status").asText())) {
            String code = data.path("details").path("error").asText("EXPO_ERROR");
            if (!code.matches("[A-Za-z0-9_]{1,80}")) code = "EXPO_ERROR";
            return new Result("REJECTED", null, code);
        }
        return new Result("UNKNOWN", null, "INVALID_RESPONSE");
    }
}