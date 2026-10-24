package io.nicheblog.dreamdiary.feature.journal.day.controller;

import io.nicheblog.dreamdiary.feature.journal._shared.helper.JournalCategoryMapSaveHelper;
import io.nicheblog.dreamdiary.feature.journal.day.model.JournalDayDto;
import io.nicheblog.dreamdiary.feature.journal.day.service.JournalDayService;
import io.nicheblog.dreamdiary.feature.journal.day.service.my.MyJournalDayCalService;
import io.nicheblog.dreamdiary.feature.journal.day.service.my.MyJournalDayQueryService;
import io.nicheblog.dreamdiary.feature.journal.day.service.my.MyJournalDayService;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.handler.ApplicationEventPublisherWrapper;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.infrastructure.log.event.LogEvent;
import io.nicheblog.dreamdiary.infrastructure.log.model.LogParam;
import io.nicheblog.dreamdiary.infrastructure.web.config.ValidationConfig;
import io.nicheblog.dreamdiary.infrastructure.web.handler.BaseExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 저널 일자 등록 API의 입력 검증 실패 응답 계약 테스트.
 * <pre>
 *  변경 전: 잘못된 journalDate(예: 2026-10-8)는 HTTP 400 + "{msg.journal.day.date.format}" placeholder 응답,
 *  활동 로그에는 "처리 중 오류가 발생했습니다." 공통 문구만 남았다.
 *  변경 후: 메시지 번들로 해석된 한국어 문구와 필드명을 응답·활동 로그에 담는다.
 * </pre>
 *
 * @author nichefish
 */
@ExtendWith(MockitoExtension.class)
class JournalDayRestControllerValidationTest {

    /** 한글 음절 포함 여부 (해석된 한국어 메시지 확인용) */
    private static final String HANGUL = ".*[\\uAC00-\\uD7A3].*";

    @Mock private JournalDayService journalDayService;
    @Mock private MyJournalDayQueryService myJournalDayQueryService;
    @Mock private MyJournalDayCalService myJournalDayCalService;
    @Mock private MyJournalDayService myJournalDayService;
    @Mock private JournalCategoryMapSaveHelper journalCategoryMapSaveHelper;
    @Mock private ApplicationEventPublisherWrapper publisher;

    private ResourceBundleMessageSource messageSource;
    private Object previousMessageSource;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        previousMessageSource = ReflectionTestUtils.getField(MessageUtils.class, "messageSource");
        ReflectionTestUtils.setField(MessageUtils.class, "messageSource", messageSource);

        // 운영과 같은 Validator 구성 (ValidationConfig 빈 팩토리) 을 그대로 사용한다.
        final LocalValidatorFactoryBean validator = ValidationConfig.validator(messageSource);
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new JournalDayRestController(
                        journalDayService, myJournalDayQueryService, myJournalDayCalService, myJournalDayService, journalCategoryMapSaveHelper))
                .setControllerAdvice(new BaseExceptionHandler(publisher))
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtils.setField(MessageUtils.class, "messageSource", previousMessageSource);
        LocaleContextHolder.resetLocaleContext();
    }

    private String ko(final String key) {
        return messageSource.getMessage(key, null, Locale.KOREAN);
    }

    /** 형식이 틀린 날짜는 서비스 호출 전에 400으로 거절하고, 해석된 한국어 메시지와 필드명을 응답·활동 로그에 담는다. */
    @Test
    void badJournalDateReturnsResolvedKoreanMessageAndField() throws Exception {
        final String expected = ko("journal.day.date.format");
        assertThat(expected).matches(HANGUL).doesNotContain("{");

        mockMvc.perform(multipart(Url.JOURNAL_DAYS)
                        .param("journalDate", "2026-10-8")
                        .param("journalDatePrecision", "EXACT")
                        .locale(Locale.KOREAN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.rslt").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(expected))
                .andExpect(jsonPath("$.message", not(containsString("{"))))
                .andExpect(jsonPath("$.rsltList[0].field").value("journalDate"))
                .andExpect(jsonPath("$.rsltList[0].message").value(expected));

        verifyNoInteractions(myJournalDayService, journalDayService);

        final ArgumentCaptor<LogEvent> captor = ArgumentCaptor.forClass(LogEvent.class);
        verify(publisher).publishAsyncEvent(captor.capture());
        final LogParam logParam = captor.getValue().getLog();
        assertThat(logParam.getRslt()).isFalse();
        assertThat(logParam.getRsltMsg()).isEqualTo(expected);
        assertThat(logParam.getExceptionMsg())
                .contains("field=journalDate")
                .contains("rejected=\"2026-10-8\"");
    }

    /** 날씨 길이 초과도 같은 방식으로 필드명과 해석된 메시지를 돌려준다. */
    @Test
    void tooLongWeatherReturnsResolvedMessage() throws Exception {
        mockMvc.perform(multipart(Url.JOURNAL_DAYS)
                        .param("journalDate", "2026-10-08")
                        .param("weather", "a".repeat(101))
                        .locale(Locale.KOREAN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ko("journal.day.weather.max-length")))
                .andExpect(jsonPath("$.rsltList[0].field").value("weather"));

        verifyNoInteractions(myJournalDayService, journalDayService);
    }

    /** 앞뒤 공백이 섞인 날짜는 DTO setter 에서 trim 되어 검증을 통과하고 중복 확인까지 진행된다. */
    @Test
    void journalDateIsTrimmedBeforeValidation() throws Exception {
        when(myJournalDayService.dupChck(any(JournalDayDto.class))).thenReturn(true);

        mockMvc.perform(multipart(Url.JOURNAL_DAYS)
                        .param("journalDate", "  2026-10-08  ")
                        .locale(Locale.KOREAN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rslt").value(false))
                .andExpect(jsonPath("$.message").value(ko("journal.day.duplicate")));

        final ArgumentCaptor<JournalDayDto> captor = ArgumentCaptor.forClass(JournalDayDto.class);
        verify(myJournalDayService).dupChck(captor.capture());
        assertThat(captor.getValue().getJournalDate()).isEqualTo("2026-10-08");
    }

    /** 비밀번호 같은 민감 필드의 거절 값은 활동 로그에 원문 대신 마스킹되어 남는다. */
    @Test
    void sensitiveRejectedValueIsMaskedInLog() {
        LocaleContextHolder.setLocale(Locale.KOREAN);
        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "userPwChgParam");
        bindingResult.addError(new FieldError("userPwChgParam", "newPw", "secret-value-123", false,
                new String[]{"Size.userPwChgParam.newPw", "Size"}, null, "{user.pw.size}"));
        final BindException exception = new BindException(bindingResult);
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("Accept", "application/json");

        final Object result = new BaseExceptionHandler(publisher).handleBingdingException(exception, new ServletWebRequest(servletRequest));

        assertThat(result).isInstanceOf(ResponseEntity.class);
        final ArgumentCaptor<LogEvent> captor = ArgumentCaptor.forClass(LogEvent.class);
        verify(publisher).publishAsyncEvent(captor.capture());
        final LogParam logParam = captor.getValue().getLog();
        assertThat(logParam.getRsltMsg()).isEqualTo(ko("user.pw.size"));
        assertThat(logParam.getExceptionMsg()).contains("field=newPw").contains("rejected=****").doesNotContain("secret-value-123");
    }
}
