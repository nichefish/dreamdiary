package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import java.util.List;
import java.util.Map;

/** Expo와의 외부 전달 경계. 접수·전달 서비스 인계는 기기 표시 성공과 구분한다. */
public interface KeepPushGateway {
    /** 토큰과 개인 내용을 포함하지 않는 전달 결과. */
    record Result(String status, String ticketId, String code) {}

    /** 한 기기의 요약 알림을 한 번 요청한다. 응답 불명은 재전송하지 않는다. */
    Result send(String token, KeepReminderPolicy.Window window, List<KeepReminderPolicy.Item> items);

    /** Expo 접수 ID의 전달 결과를 조회한다. 조회 자체는 반복할 수 있다. */
    Map<String, Result> receipts(List<String> ids);
}