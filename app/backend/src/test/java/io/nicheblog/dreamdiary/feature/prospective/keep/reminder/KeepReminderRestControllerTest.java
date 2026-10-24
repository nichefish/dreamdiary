package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 가상 인증 소유자로 기기 API의 소유자 결정·충돌 HTTP 상태·토큰 응답 배제를 검증한다. */
class KeepReminderRestControllerTest {
    private static final String FIXTURE_OWNER = "Alice";
    private static final String FIXTURE_TITLE = "샘플 항목";
    private static final String FIXTURE_DEVICE = "11111111-1111-4111-8111-111111111111";
    private static final String FIXTURE_TOKEN = "ExpoPushToken[sample_token]";
    private static final String FIXTURE_JSON =
            "{\"deviceId\":\"11111111-1111-4111-8111-111111111111\",\"expoToken\":\"ExpoPushToken[sample_token]\",\"timeZone\":\"Asia/Seoul\"}";

    @Test void registrationUsesAuthenticatedOwnerAndDoesNotReturnToken() throws Exception {
        var repository = mock(KeepReminderRepository.class);
        var mvc = MockMvcBuilders.standaloneSetup(new KeepReminderRestController(repository, mock(io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService.class))).build();
        try (var auth = mockStatic(AuthUtils.class)) {
            auth.when(AuthUtils::requireLoginUsername).thenReturn(FIXTURE_OWNER);
            mvc.perform(post("/api/prospective/keep/reminder-devices").contentType(MediaType.APPLICATION_JSON).content(FIXTURE_JSON))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.rslt").value(true))
                    .andExpect(jsonPath("$.rsltObj.deviceId").value(FIXTURE_DEVICE))
                    .andExpect(jsonPath("$.rsltObj.expoToken").doesNotExist());
            verify(repository).register(FIXTURE_OWNER, FIXTURE_DEVICE, FIXTURE_TOKEN, "Asia/Seoul");
        }
    }

    @Test void activeOwnerConflictIs409InsteadOfGenericServerError() throws Exception {
        var repository = mock(KeepReminderRepository.class);
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "가상 기기 충돌"))
                .when(repository).register(anyString(), anyString(), anyString(), anyString());
        var mvc = MockMvcBuilders.standaloneSetup(new KeepReminderRestController(repository, mock(io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService.class))).build();
        try (var auth = mockStatic(AuthUtils.class)) {
            auth.when(AuthUtils::requireLoginUsername).thenReturn(FIXTURE_OWNER);
            mvc.perform(post("/api/prospective/keep/reminder-devices").contentType(MediaType.APPLICATION_JSON).content(FIXTURE_JSON))
                    .andExpect(status().isConflict()).andExpect(jsonPath("$.rslt").value(false));
        }
    }

    @Test void logoutUnregisterIsScopedToAuthenticatedOwner() throws Exception {
        var repository = mock(KeepReminderRepository.class);
        var mvc = MockMvcBuilders.standaloneSetup(new KeepReminderRestController(repository, mock(io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService.class))).build();
        try (var auth = mockStatic(AuthUtils.class)) {
            auth.when(AuthUtils::requireLoginUsername).thenReturn(FIXTURE_OWNER);
            mvc.perform(delete("/api/prospective/keep/reminder-devices/" + FIXTURE_DEVICE)).andExpect(status().isOk());
            verify(repository).unregister(FIXTURE_OWNER, FIXTURE_DEVICE);
        }
    }

    @Test void malformedJsonReturnsFixedMessageWithoutCredentialText() throws Exception {
        var repository = mock(KeepReminderRepository.class);
        var mvc = MockMvcBuilders.standaloneSetup(new KeepReminderRestController(repository, mock(io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService.class))).build();
        mvc.perform(post("/api/prospective/keep/reminder-devices").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expoToken\":\"ExpoPushToken[sample_token]\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("기기 등록 JSON을 확인하세요."));
        verifyNoInteractions(repository);
    }
    /** 가상 목록 중 현재 OPEN 정책에 해당하는 ID만 현재 인증 소유자 범위에서 반환한다. */
    @Test void tapQueriesCurrentOwnerAndReevaluatesLatestCandidates() throws Exception {
        var repository = mock(KeepReminderRepository.class);
        var service = mock(io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService.class);
        var day = java.time.LocalDate.of(2001, 10, 24);
        var first = new io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepDto();
        first.setId(101);
        first.setTitle(FIXTURE_TITLE);
        var ended = new io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepDto();
        ended.setId(102);
        when(service.getListDtoWithLifecycleByUser(eq(FIXTURE_OWNER), any())).thenReturn(java.util.List.of(first, ended));
        when(repository.candidates(FIXTURE_OWNER, day)).thenReturn(java.util.List.of(
                new KeepReminderPolicy.Item(101, day, false),
                new KeepReminderPolicy.Item(102, day.plusDays(1), false)));
        var mvc = MockMvcBuilders.standaloneSetup(new KeepReminderRestController(repository, service)).build();
        try (var auth = mockStatic(AuthUtils.class)) {
            auth.when(AuthUtils::requireLoginUsername).thenReturn(FIXTURE_OWNER);
            mvc.perform(get("/api/prospective/keep/reminder-targets").param("localDate", "2001-10-24").param("slot", "MORNING"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.rsltList.length()").value(1))
                    .andExpect(jsonPath("$.rsltList[0].id").value(101));
            when(repository.candidates(FIXTURE_OWNER, day)).thenReturn(java.util.List.of());
            mvc.perform(get("/api/prospective/keep/reminder-targets").param("localDate", "2001-10-24").param("slot", "MORNING"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.rsltList.length()").value(0));
            verify(service, times(2)).getListDtoWithLifecycleByUser(eq(FIXTURE_OWNER), any());
            verify(repository, times(2)).candidates(FIXTURE_OWNER, day);
        }
    }

    /** 불가능한 날짜와 미정의 슬롯은 DB·서비스 호출 전 400으로 반환한다. */
    @Test void invalidReminderWindowIsRejectedBeforeDataRead() throws Exception {
        var repository = mock(KeepReminderRepository.class);
        var service = mock(io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new KeepReminderRestController(repository, service)).build();
        for (var pair : java.util.List.of(new String[]{"2001-02-30", "MORNING"}, new String[]{"2001-10-24", "NIGHT"},
                new String[]{"9999-12-31", "EVENING"}, new String[]{"01/24", "MORNING"})) {
            mvc.perform(get("/api/prospective/keep/reminder-targets").param("localDate", pair[0]).param("slot", pair[1]))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.rslt").value(false));
        }
        verifyNoInteractions(repository, service);
    }
}