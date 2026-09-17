package io.nicheblog.dreamdiary.feature.ai.person;

import io.nicheblog.dreamdiary.feature.ai.AiPersonTestFixtures;
import io.nicheblog.dreamdiary.feature.ai.AiRagTestSupport;
import io.nicheblog.dreamdiary.feature.ai.client.OllamaClient;
import io.nicheblog.dreamdiary.feature.ai.model.AiChatMessage;
import io.nicheblog.dreamdiary.feature.ai.rag.RagContext;
import io.nicheblog.dreamdiary.feature.ai.rag.RagIntent;
import io.nicheblog.dreamdiary.feature.ai.rag.RagSearchFacade;
import io.nicheblog.dreamdiary.feature.journal.embedding.model.RagSearchResult;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * PersonSynthesisHybridService Path C 오케스트레이션·프롬프트 라우팅·결정적 폴백의 동작 계약 테스트.
 *
 * <pre>
 *  resolveHybrid 의 가드 분기(정상·언어 가드·저하 재시도), shouldUsePathC 진입 조건,
 *  질문 유형별(태도·등장·의미) 프롬프트 블록 라우팅, personFocus·results 부재 시 폴백 문구를 고정한다.
 *  모든 검증은 public 진입점을 통해 수행하며 private 메소드를 리플렉션으로 호출하지 않는다.
 *  가상 인물 픽스처는 {@link AiPersonTestFixtures}를 재사용한다.
 * </pre>
 *
 * @author nichefish
 */
class PersonSynthesisHybridServiceCharacterizationTest {

    /** 언어 가드 재시도 프롬프트 식별 마커. FakeGuard가 반환하고 LLM 스텁이 호출 구분에 쓴다. */
    private static final String LANGUAGE_RETRY_MARKER = "<<LANGUAGE_RETRY>>";
    /** FakeGuard가 허용되지 않는 Han script로 판정하는 표지. */
    private static final String DISALLOWED_HAN_MARKER = "漢";
    /** FakeGuard가 저하 응답으로 판정하는 표지. */
    private static final String DEGRADED_MARKER = "빈약";

    /** 1인칭 태도 질문(Path C stance 분기). */
    private static final String ATTITUDE_QUERY = "나는 " + AiPersonTestFixtures.PERSON_A + "님을 어떻게 생각하고 있니?";
    /** 등장 방식 질문(Path C appearance 분기). */
    private static final String APPEARANCE_QUERY = AiPersonTestFixtures.PERSON_B + "님은 어떻게 등장해?";
    /** 의미 통섭 질문(Path C meaning 분기). */
    private static final String MEANING_QUERY = AiPersonTestFixtures.PERSON_A + "님은 어떤 의미야?";
    /** 인물 질문이 아닌 질의. */
    private static final String NON_PERSON_QUERY = "오늘 일정 정리해줘";

    @BeforeAll
    static void bindMessageSourceForChatCatalog() throws Exception {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        final Field field = MessageUtils.class.getDeclaredField("messageSource");
        field.setAccessible(true);
        field.set(null, messageSource);
        LocaleContextHolder.setLocale(Locale.KOREAN);
    }

    /**
     * 테스트용 PersonSynthesisHybridService. 실 협력자를 주입하고 LLM 클라이언트만 교체한다.
     */
    private static PersonSynthesisHybridService newHybridService(final OllamaClient ollamaClient) {
        final RagSearchFacade ragSearchFacade = new RagSearchFacade(null, null);
        final PersonFocusResolver personFocusResolver = new PersonFocusResolver(null, null, ragSearchFacade);
        final PersonSnapshotService personSnapshotService = new PersonSnapshotService(personFocusResolver);
        return new PersonSynthesisHybridService(ollamaClient, personSnapshotService, personFocusResolver);
    }

    /**
     * 카탈로그 메시지를 인자 없이 조회한다.
     */
    private static String message(final String key) {
        return MessageUtils.getMessage(key, new Object[]{});
    }

    /**
     * 프롬프트에 담긴 마커로 호출 회차를 구분해 응답을 돌려주는 LLM 스텁.
     *
     * @param first 최초 호출 응답
     * @param languageRetry 언어 가드 재시도 응답
     * @param guardRetry person 가드 재시도 응답
     */
    private static OllamaClient stubLlm(final String first, final String languageRetry, final String guardRetry) {
        final OllamaClient ollamaClient = mock(OllamaClient.class);
        when(ollamaClient.chat(anyString(), anyList())).thenAnswer(invocation -> {
            final String systemPrompt = invocation.getArgument(0);
            if (systemPrompt.contains(LANGUAGE_RETRY_MARKER)) return languageRetry;
            if (systemPrompt.contains("PERSON_MEANING_RETRY") || systemPrompt.contains("PERSON_STANCE_RETRY")) {
                return guardRetry;
            }
            return first;
        });
        return ollamaClient;
    }

    /**
     * 표지 문자열로 판정하는 가드 스텁. 사유 코드는 판정 대상 응답을 그대로 실어 회차를 구분한다.
     *
     * @param disallowedHan 언어 가드 판정 술어
     * @param degraded person 저하 판정 술어
     */
    private record FakeGuard(Predicate<String> disallowedHan, Predicate<String> degraded)
            implements PersonSynthesisGuardPort {

        /**
         * 어떤 응답도 가드에 걸리지 않는 스텁.
         */
        static FakeGuard clean() {
            return new FakeGuard(text -> false, text -> false);
        }

        /**
         * 표지 문자열이 포함된 응답만 가드에 거는 스텁.
         */
        static FakeGuard markerBased() {
            return new FakeGuard(
                    text -> text != null && text.contains(DISALLOWED_HAN_MARKER),
                    text -> text != null && text.contains(DEGRADED_MARKER)
            );
        }

        @Override
        public boolean containsDisallowedHanScript(final String text) {
            return disallowedHan.test(text);
        }

        @Override
        public String languageRetryPrompt() {
            return LANGUAGE_RETRY_MARKER;
        }

        @Override
        public boolean isDegradedPersonResponse(
                final String response,
                final PersonFocus personFocus,
                final List<RagSearchResult> results,
                final RagIntent intent,
                final String queryText
        ) {
            return degraded.test(response);
        }

        @Override
        public String describePersonGuardFailure(
                final String response,
                final PersonFocus personFocus,
                final List<RagSearchResult> results,
                final RagIntent intent,
                final String queryText
        ) {
            return "detail:" + response;
        }
    }

    /**
     * SYNTHESIS 의도 + personFocus + person-meaning 질문이 모두 성립할 때만 Path C로 진입합니다.
     */
    @Test
    void shouldUsePathC_shouldRequireSynthesisIntentAndPersonFocusAndMeaningQuery() throws Exception {
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));
        final PersonFocus personFocus =
                (PersonFocus) AiRagTestSupport.buildTestPersonFocus(AiPersonTestFixtures.PERSON_A);

        assertTrue(service.shouldUsePathC(personFocus, RagIntent.SYNTHESIS, MEANING_QUERY));
        assertFalse(service.shouldUsePathC(personFocus, RagIntent.LOOKUP, MEANING_QUERY));
        assertFalse(service.shouldUsePathC(null, RagIntent.SYNTHESIS, MEANING_QUERY));
        assertFalse(service.shouldUsePathC(personFocus, RagIntent.SYNTHESIS, NON_PERSON_QUERY));
    }

    /**
     * personFocus가 없으면 LLM을 호출하지 않고 empty_tagged_sources RULE_PRIMARY로 끝냅니다.
     */
    @Test
    void resolveHybrid_shouldSkipLlmAndReturnRulePrimary_whenPersonFocusMissing() throws Exception {
        final OllamaClient ollamaClient = mock(OllamaClient.class);
        final PersonSynthesisHybridService service = newHybridService(ollamaClient);

        final PersonSynthesisResult result = service.resolveHybrid(
                1, MEANING_QUERY, RagIntent.SYNTHESIS, null, List.of(), List.of(), FakeGuard.clean());

        assertEquals("RULE_PRIMARY", result.responseMode());
        assertEquals("empty_tagged_sources", result.guardDetail());
        assertEquals(message("chat.ai.clarify.need-context"), result.content());
        verifyNoInteractions(ollamaClient);
    }

    /**
     * 태그된 검색 결과가 비면 LLM을 호출하지 않고 대상 안내 문구로 폴백합니다.
     */
    @Test
    void resolveHybrid_shouldSkipLlmAndReturnRulePrimary_whenResultsEmpty() throws Exception {
        final OllamaClient ollamaClient = mock(OllamaClient.class);
        final PersonSynthesisHybridService service = newHybridService(ollamaClient);
        final PersonFocus personFocus =
                (PersonFocus) AiRagTestSupport.buildTestPersonFocus(AiPersonTestFixtures.PERSON_A);

        final PersonSynthesisResult result = service.resolveHybrid(
                1, MEANING_QUERY, RagIntent.SYNTHESIS, personFocus, List.of(), List.of(), FakeGuard.clean());

        assertEquals("RULE_PRIMARY", result.responseMode());
        assertEquals("empty_tagged_sources", result.guardDetail());
        assertTrue(result.content().contains(AiPersonTestFixtures.PERSON_A));
        verifyNoInteractions(ollamaClient);
    }

    /**
     * 가드를 모두 통과하면 내부 기록 인용([1] 등)을 제거한 본문을 hybrid 모드로 반환합니다.
     */
    @Test
    void resolveHybrid_shouldReturnHybridWithCitationsStripped_whenGuardsPass() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(
                stubLlm("[1] 기록을 보면 회의 장면이 반복돼 [2]", "쓰이지 않음", "쓰이지 않음"));

        final PersonSynthesisResult result = service.resolveHybrid(
                1, MEANING_QUERY, RagIntent.SYNTHESIS, ragContext.personFocus(), ragContext.results(),
                List.<AiChatMessage>of(), FakeGuard.clean());

        assertEquals("PERSON_SYNTHESIS_HYBRID", result.responseMode());
        assertNull(result.guardDetail());
        assertNull(result.retryGuardDetail());
        assertFalse(result.content().contains("[1]"));
        assertFalse(result.content().contains("[2]"));
        assertTrue(result.content().startsWith("기록을 보면"));
    }

    /**
     * 최초 응답이 언어 가드에 걸리면 언어 재시도 프롬프트로 1회 재호출하고, 재시도가 통과하면 hybrid로 반환합니다.
     */
    @Test
    void resolveHybrid_shouldRetryWithLanguagePrompt_whenFirstResponseFailsLanguageGuard() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(
                stubLlm(DISALLOWED_HAN_MARKER + " 응답", "한국어로 다시 쓴 응답", "쓰이지 않음"));

        final PersonSynthesisResult result = service.resolveHybrid(
                1, MEANING_QUERY, RagIntent.SYNTHESIS, ragContext.personFocus(), ragContext.results(),
                List.<AiChatMessage>of(), FakeGuard.markerBased());

        assertEquals("PERSON_SYNTHESIS_HYBRID", result.responseMode());
        assertEquals("한국어로 다시 쓴 응답", result.content());
    }

    /**
     * 언어 재시도 응답도 언어 가드에 걸리면 language_guard 사유로 RULE_PRIMARY 폴백합니다.
     */
    @Test
    void resolveHybrid_shouldFallbackWithLanguageGuard_whenRetryStillFailsLanguageGuard() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(
                stubLlm(DISALLOWED_HAN_MARKER + " 응답", DISALLOWED_HAN_MARKER + " 재시도 응답", "쓰이지 않음"));

        final PersonSynthesisResult result = service.resolveHybrid(
                1, MEANING_QUERY, RagIntent.SYNTHESIS, ragContext.personFocus(), ragContext.results(),
                List.<AiChatMessage>of(), FakeGuard.markerBased());

        assertEquals("RULE_PRIMARY", result.responseMode());
        assertEquals("language_guard", result.guardDetail());
        assertNull(result.retryGuardDetail());
    }

    /**
     * 저하 응답은 재시도 프롬프트로 1회 재호출하고, 재시도가 통과하면 재시도 본문을 hybrid로 반환합니다.
     */
    @Test
    void resolveHybrid_shouldReturnRetryResponse_whenFirstDegradedAndRetryPasses() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(
                stubLlm(DEGRADED_MARKER + "한 일반론", "쓰이지 않음", "근거 장면을 인용한 답"));

        final PersonSynthesisResult result = service.resolveHybrid(
                1, MEANING_QUERY, RagIntent.SYNTHESIS, ragContext.personFocus(), ragContext.results(),
                List.<AiChatMessage>of(), FakeGuard.markerBased());

        assertEquals("PERSON_SYNTHESIS_HYBRID", result.responseMode());
        assertEquals("근거 장면을 인용한 답", result.content());
        assertNull(result.guardDetail());
    }

    /**
     * 재시도까지 저하로 판정되면 1차·재시도 가드 사유를 모두 실어 RULE_PRIMARY 폴백합니다.
     */
    @Test
    void resolveHybrid_shouldFallbackWithBothGuardDetails_whenRetryAlsoDegraded() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(
                stubLlm(DEGRADED_MARKER + "한 일반론", "쓰이지 않음", DEGRADED_MARKER + "한 재시도"));

        final PersonSynthesisResult result = service.resolveHybrid(
                1, MEANING_QUERY, RagIntent.SYNTHESIS, ragContext.personFocus(), ragContext.results(),
                List.<AiChatMessage>of(), FakeGuard.markerBased());

        assertEquals("RULE_PRIMARY", result.responseMode());
        assertEquals("detail:" + DEGRADED_MARKER + "한 일반론", result.guardDetail());
        assertEquals("detail:" + DEGRADED_MARKER + "한 재시도", result.retryGuardDetail());
        assertTrue(result.content().contains(AiPersonTestFixtures.PERSON_A));
    }

    /**
     * 태도 질문은 stance 규칙 블록만 싣고 등장·의미 블록을 싣지 않습니다.
     */
    @Test
    void buildHybridSystemPrompt_shouldSelectStanceBlocks_forAttitudeQuery() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String prompt = service.buildHybridSystemPrompt(
                ragContext.personFocus(), ragContext.results(), ATTITUDE_QUERY);

        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.stance.opening")));
        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.stance.format-free")));
        assertFalse(prompt.contains(message("chat.ai.prompt.rag.hybrid.appearance.format")));
        assertFalse(prompt.contains(message("chat.ai.prompt.rag.hybrid.meaning.guide")));
    }

    /**
     * 등장 질문은 appearance 규칙 블록만 싣습니다.
     */
    @Test
    void buildHybridSystemPrompt_shouldSelectAppearanceBlocks_forAppearanceQuery() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_B);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String prompt = service.buildHybridSystemPrompt(
                ragContext.personFocus(), ragContext.results(), APPEARANCE_QUERY);

        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.appearance.format")));
        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.appearance.forbidden")));
        assertFalse(prompt.contains(message("chat.ai.prompt.rag.hybrid.stance.opening")));
        assertFalse(prompt.contains(message("chat.ai.prompt.rag.hybrid.meaning.guide")));
    }

    /**
     * 태도·등장 어느 쪽도 아닌 의미 질문은 meaning 규칙 블록을 싣습니다.
     */
    @Test
    void buildHybridSystemPrompt_shouldSelectMeaningBlocks_forMeaningQuery() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String prompt = service.buildHybridSystemPrompt(
                ragContext.personFocus(), ragContext.results(), MEANING_QUERY);

        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.meaning.question")));
        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.meaning.guide")));
        assertFalse(prompt.contains(message("chat.ai.prompt.rag.hybrid.stance.opening")));
        assertFalse(prompt.contains(message("chat.ai.prompt.rag.hybrid.appearance.format")));
    }

    /**
     * personFocus가 없으면 SNAPSHOT 블록의 대상 라벨을 싣지 않고 규칙 블록만 남깁니다.
     */
    @Test
    void buildHybridSystemPrompt_shouldOmitSnapshotBody_whenPersonFocusMissing() {
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String prompt = service.buildHybridSystemPrompt(null, List.of(), MEANING_QUERY);

        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.meaning.question")));
        assertFalse(prompt.contains(message("chat.ai.prompt.rag.hybrid.snapshot.label.target") + ": "));
    }

    /**
     * SNAPSHOT 블록은 표시용 태그(대괄호 범주 제거)와 해석 시드를 함께 싣습니다.
     */
    @Test
    void buildHybridSystemPrompt_shouldCarryDisplayTagsAndInterpretiveSeed() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String prompt = service.buildHybridSystemPrompt(
                ragContext.personFocus(), ragContext.results(), MEANING_QUERY);

        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.snapshot.label.target")));
        assertTrue(prompt.contains(message("chat.ai.prompt.rag.hybrid.snapshot.label.interpretive-seed")));
        assertTrue(prompt.contains("#조직역동"));
        assertFalse(prompt.contains("[엔서클]"));
    }

    /**
     * 태도 질문 재시도 프롬프트는 stance 재시도 헤더로 분기합니다.
     */
    @Test
    void buildPersonMeaningRetryPrompt_shouldRouteToStanceRetry_forAttitudeQuery() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String prompt = service.buildPersonMeaningRetryPrompt(
                ragContext.personFocus(), ragContext.results(), ATTITUDE_QUERY, "person_stance_too_short");

        assertTrue(prompt.contains("PERSON_STANCE_RETRY"));
        assertFalse(prompt.contains("PERSON_MEANING_RETRY"));
    }

    /**
     * 의미 질문 재시도 프롬프트는 meaning 헤더와 가드 사유 힌트·스냅샷 라벨을 함께 싣습니다.
     */
    @Test
    void buildPersonMeaningRetryPrompt_shouldCarryGuardHintAndSnapshotLabels_forMeaningQuery() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String prompt = service.buildPersonMeaningRetryPrompt(
                ragContext.personFocus(), ragContext.results(), MEANING_QUERY, "person_meaning_hollow");

        assertTrue(prompt.contains("PERSON_MEANING_RETRY"));
        assertTrue(prompt.contains(message("chat.ai.prompt.rag.retry.hint.person-meaning-hollow")));
        assertTrue(prompt.contains(message("chat.ai.prompt.rag.retry.label.person-tags")));
        assertTrue(prompt.contains(message("chat.ai.prompt.rag.retry.label.linked-context")));
    }

    /**
     * 가드 사유가 비면 재시도 프롬프트에 사유 힌트를 싣지 않습니다.
     */
    @Test
    void buildPersonMeaningRetryPrompt_shouldOmitGuardHint_whenGuardDetailBlank() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String prompt = service.buildPersonMeaningRetryPrompt(
                ragContext.personFocus(), ragContext.results(), MEANING_QUERY, "  ");

        assertTrue(prompt.contains("PERSON_MEANING_RETRY"));
        assertFalse(prompt.contains(message("chat.ai.prompt.rag.retry.hint.default")));
    }

    /**
     * person-meaning 결정적 폴백은 focus·결과 부재를 각각 다른 안내 문구로 구분합니다.
     */
    @Test
    void buildPersonMeaningDeterministicFallback_shouldDistinguishMissingFocusAndEmptyResults() throws Exception {
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));
        final PersonFocus personFocus =
                (PersonFocus) AiRagTestSupport.buildTestPersonFocus(AiPersonTestFixtures.PERSON_A);

        assertEquals(
                message("chat.ai.clarify.need-context"),
                service.buildPersonMeaningDeterministicFallback(null, List.of()));
        assertEquals(
                MessageUtils.getMessage("chat.ai.clarify.no-info-for-target",
                        new Object[]{AiPersonTestFixtures.PERSON_A}),
                service.buildPersonMeaningDeterministicFallback(personFocus, List.of()));
        assertEquals(
                message("chat.ai.clarify.need-context"),
                service.buildPersonMeaningDeterministicFallback(null, null));
    }

    /**
     * person-meaning 결정적 폴백은 해석 리드에 이어 반복 축·역할 기능·불확실성 문구를 싣습니다.
     */
    @Test
    void buildPersonMeaningDeterministicFallback_shouldComposeSectionsFromSnapshot() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String fallback = service.buildPersonMeaningDeterministicFallback(
                ragContext.personFocus(), ragContext.results());

        assertTrue(fallback.contains(message("chat.ai.fallback.section.repeated-axis") + " #"));
        assertTrue(fallback.contains(message("chat.ai.fallback.section.linked-context") + " #"));
        assertTrue(fallback.contains(message("chat.ai.fallback.section.role-function")));
        assertTrue(fallback.contains(message("chat.ai.fallback.person-meaning.uncertainty")));
    }

    /**
     * 역할 축이 비어 연결 맥락으로 대체될 때, person-meaning 폴백의 역할·기능 줄은 범주 접두가 붙은 원본 태그를 싣습니다.
     *
     * <p>같은 출력의 반복 축·연결 맥락 줄은 표시용 태그(범주 접두 제거)를 쓰므로 이 줄만 표기가 다르다.
     * TODO 두 표기의 차이가 의도된 계약인지 확인이 필요하다. 확인 전까지 현재 동작을 그대로 고정한다.</p>
     */
    @Test
    void buildPersonMeaningDeterministicFallback_shouldCarryRawCategoryTagInRoleHint() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_A);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String fallback = service.buildPersonMeaningDeterministicFallback(
                ragContext.personFocus(), ragContext.results());

        assertTrue(fallback.contains(message("chat.ai.fallback.section.role-function")));
        assertTrue(fallback.contains("[엔서클]#조직역동"));
        assertTrue(fallback.contains(message("chat.ai.fallback.section.linked-context") + " #조직역동"));
    }

    /**
     * person-appearance 결정적 폴백은 focus·결과 부재를 등장 전용 안내 문구로 구분합니다.
     */
    @Test
    void buildPersonAppearanceDeterministicFallback_shouldDistinguishMissingFocusAndEmptyResults() throws Exception {
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));
        final PersonFocus personFocus =
                (PersonFocus) AiRagTestSupport.buildTestPersonFocus(AiPersonTestFixtures.PERSON_B);

        assertEquals(
                message("chat.ai.clarify.need-context"),
                service.buildPersonAppearanceDeterministicFallback(null, List.of()));
        assertEquals(
                MessageUtils.getMessage("chat.ai.clarify.no-appearance-for-target",
                        new Object[]{AiPersonTestFixtures.PERSON_B}),
                service.buildPersonAppearanceDeterministicFallback(personFocus, List.of()));
    }

    /**
     * person-appearance 결정적 폴백은 등장 톤·반복 패턴·함께 묶인 축·불확실성 섹션을 싣습니다.
     */
    @Test
    void buildPersonAppearanceDeterministicFallback_shouldComposeAppearanceSections() throws Exception {
        final RagContext ragContext = (RagContext) AiRagTestSupport.buildTestRagContextWithTaggedResults(
                AiPersonTestFixtures.PERSON_B);
        final PersonSynthesisHybridService service = newHybridService(mock(OllamaClient.class));

        final String fallback = service.buildPersonAppearanceDeterministicFallback(
                ragContext.personFocus(), ragContext.results());

        assertTrue(fallback.contains(message("chat.ai.fallback.person-appearance.section-tone")));
        assertTrue(fallback.contains(message("chat.ai.fallback.person-appearance.section-repeat")));
        assertTrue(fallback.contains(message("chat.ai.fallback.person-appearance.section-bundled")));
        assertTrue(fallback.contains(message("chat.ai.fallback.person-appearance.uncertainty")));
    }
}
