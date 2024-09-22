package io.nicheblog.dreamdiary.api.jandi.controller;

import io.nicheblog.dreamdiary.api.jandi.JandiTopic;
import io.nicheblog.dreamdiary.api.jandi.model.JandiParam;
import io.nicheblog.dreamdiary.api.jandi.service.JandiApiService;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.Url;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JandiApiControllerTest
 * <pre>
 *  API:: JANDI (incoming/outgoing)webhook 컨트롤러 테스트
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
@Log4j2
public class JandiApiControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private JandiApiController jandiApiController;

    @Mock
    private JandiApiService jandiApiService;
    @Mock
    private HttpServletRequest request;
    @Mock
    protected ApplicationEventPublisher publisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jandiApiController).build();
    }

    /**
     * JANDI:: 메세지 송신 검증
     */
    @Test
    public void testSendMsg() throws Exception {
        // Given::

        // When::
        when(request.getRequestURL())
                .thenReturn(new StringBuffer(Url.API_JANDI_SND_MSG));
        when(jandiApiService.sendMsg(any(JandiParam.class)))
                .thenReturn(true);

        // Then::
        mockMvc.perform(
                    post(Url.API_JANDI_SND_MSG)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)  // 폼 데이터를 전송하는 경우
                        .param("trgetTopic", JandiTopic.TEST.name())         // JandiParam의 필드 설정
                        .param("msg", "MSG")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(TestConstant.EXPECTED_JSON_RESPONSE));
    }
}
